package com.tt.xenon.common.reflect;

import com.tt.xenon.common.annotations.OperationBody;
import com.vmware.xenon.common.Operation;
import com.vmware.xenon.common.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.IntStream;

import static com.vmware.xenon.common.UriUtils.URI_PATH_CHAR;
import static java.lang.reflect.Modifier.isPublic;
import static java.util.stream.Collectors.toList;

/**
 * Created by mageshwaranr on 8/18/2016.
 * <p>
 * Parses and creates MethodInfo of public declared method given a JaxRs annotated interfaces
 */
public class MethodInfoBuilder {

  private static Logger log = LoggerFactory.getLogger(MethodInfoBuilder.class);

  public static List<MethodInfo> parseJaxRsMethodInfo(Class<?> httpResource) {
    Method[] methods = httpResource.getDeclaredMethods();
    return generateMethodInfo(methods);
  }

  public static List<MethodInfo> generateMethodInfo(Method[] methods) {
    List<MethodInfo> httpMethods = IntStream.range(0, methods.length)
        .mapToObj(idx -> new MethodInfo(methods[idx]))
        .filter(mInfo -> isPublic(mInfo.getMethod().getModifiers()))
        .filter(mInfo -> {
          Service.Action action = parseAction(mInfo.getMethod());
          mInfo.setAction(action);
          if (action == null) {
            log.info("Skipping method {} as it has no HTTP Method annotation", mInfo.getName());
            return false;
          }
          Path pathAnnotation = mInfo.getMethod().getAnnotation(Path.class);
          mInfo.setUriPath(pathAnnotation == null ? null : pathAnnotation.value());
          return true;
        }).collect(toList());

    httpMethods.forEach(mInfo -> {
      mInfo.setPathParamsVsUriIndex(parsePathParams(mInfo.getUriPath()));
      mInfo.setParameters(extractParamMetadatas(mInfo.getMethod()));
      parseReturnTypes(mInfo);
      Collections.sort(mInfo.getParameters());
    });
    return httpMethods;
  }

  static void parseReturnTypes(MethodInfo mInfo) {
    if (CompletableFuture.class.equals(mInfo.getMethod().getReturnType())) {
      mInfo.setAsyncApi(true);
      handleCompletableFutureReturnType(mInfo);
    } else {
      mInfo.setAsyncApi(false);
      mInfo.setReturnType(mInfo.getMethod().getReturnType());
      mInfo.setType(mInfo.getMethod().getGenericReturnType());
    }
  }

  /**
   * special case : For CompletableFuture return type
   * 1. mark method as async
   * 2. set return type as generic argument ie., for CompletableFuture &lt String &gt set return type as String
   *
   * @param mInfo
   */
  private static void handleCompletableFutureReturnType(MethodInfo mInfo) {
    mInfo.setAsyncApi(true);
    Type genericReturnType = mInfo.getMethod().getGenericReturnType();
    if (genericReturnType instanceof ParameterizedType) {
      ParameterizedType type = (ParameterizedType) genericReturnType;
      Type[] typeArguments = type.getActualTypeArguments();
      // We support only completable future for async API, which means we will get only one generic argument
      Type typeArgument = typeArguments[0];
      mInfo.setType(typeArgument);
      if (typeArgument instanceof ParameterizedType) {
        mInfo.setReturnType((Class<?>) ((ParameterizedType) typeArgument).getRawType());
      } else if (typeArgument instanceof WildcardType) {
        mInfo.setReturnType(Object.class);
      } else if (typeArgument instanceof Class) {
        mInfo.setReturnType((Class<?>) typeArgument);
      } else {
        mInfo.setReturnType(Object.class);
      }
    } else {
      // method has no generic type info
      mInfo.setReturnType(Object.class);
      mInfo.setType(genericReturnType);
    }
  }

  /**
   * Given a method, finds the HTTP action
   * @param publicMethod
   * @return
   */
  static Service.Action parseAction(Method publicMethod) {
    Service.Action action = null;
    for (Annotation ann : publicMethod.getAnnotations()) {
      String httpMethod = getHttpMethodName(ann.annotationType());
      if (httpMethod != null) {
        action = Service.Action.valueOf(httpMethod);
        break;
      }
    }
    return action;
  }

  private static String getHttpMethodName(AnnotatedElement element) {
    HttpMethod httpMethod = element.getAnnotation(HttpMethod.class);
    return httpMethod == null ? null : httpMethod.value();
  }

  /**
   * Parse path params given the URI
   * @param uri
   * @return
   */
  static Map<String, Integer> parsePathParams(String uri) {
    if (uri != null && uri.contains("{") && uri.contains("}")) {
      Map<String, Integer> pathParams = new HashMap<>();
      String[] tokens = uri.split(URI_PATH_CHAR);
      for (int i = 0; i < tokens.length; i++) {
        String curToken = tokens[i];
        if (curToken.length() > 0 &&
            curToken.charAt(0) == '{' && curToken.charAt(curToken.length() - 1) == '}') {
          pathParams.put(curToken.substring(1, curToken.length() - 1), i);
        }
      }
      return pathParams;
    } else {
      return Collections.emptyMap();
    }
  }

  static List<ParamMetadata> extractParamMetadatas(Method publicMethod) {
    Parameter[] parameters = publicMethod.getParameters();
    return IntStream.range(0, parameters.length)
        .mapToObj(parameterIndex -> {
          Parameter parameter = parameters[parameterIndex];
          ParamMetadata param = new ParamMetadata();
          if (parameter.isAnnotationPresent(QueryParam.class)) {
            QueryParam annotation = parameter.getAnnotation(QueryParam.class);
            param.setName(annotation.value());
            param.setType(ParamMetadata.Type.QUERY);
          } else if (parameter.isAnnotationPresent(PathParam.class)) {
            PathParam annotation = parameter.getAnnotation(PathParam.class);
            param.setName(annotation.value());
            param.setType(ParamMetadata.Type.PATH);
          } else if (parameter.isAnnotationPresent(HeaderParam.class)) {
            HeaderParam annotation = parameter.getAnnotation(HeaderParam.class);
            param.setName(annotation.value());
            param.setType(ParamMetadata.Type.HEADER);
          } else if (parameter.isAnnotationPresent(CookieParam.class)) {
            CookieParam annotation = parameter.getAnnotation(CookieParam.class);
            param.setName(annotation.value());
            param.setType(ParamMetadata.Type.COOKIE);
          } else if (parameter.isAnnotationPresent(OperationBody.class)) {
            param.setType(ParamMetadata.Type.BODY);
            param.setParamterType(parameter.getType());
          } else if (parameter.getType().equals(Operation.class)) {
            param.setType(ParamMetadata.Type.OPERATION);
          } else {
            throw new IllegalArgumentException("Unable to understand Parameter " + parameter.getName()
                + " . It neither has supported annotations nor of type Operation ");
          }
          param.setParameterIndex(parameterIndex);
          return param;
        })
        .collect(toList());
  }


}
