package com.tt.xenon.common.client;

import static com.vmware.xenon.common.UriUtils.buildUri;
import static com.vmware.xenon.common.UriUtils.extendUri;
import static com.vmware.xenon.common.UriUtils.extendUriWithQuery;
import static com.vmware.xenon.common.Utils.DEFAULT_THREAD_COUNT;
import static java.util.stream.Collectors.toList;

import java.lang.reflect.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import com.tt.xenon.common.ServiceInfo;
import com.tt.xenon.common.dns.XenonDnsService;
import com.tt.xenon.common.reflect.MethodInfo;
import com.tt.xenon.common.reflect.ParamMetadata;
import com.tt.xenon.common.router.ExceptionResponse;
import com.tt.xenon.common.dns.document.DnsState;
import com.vmware.xenon.common.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tt.xenon.common.reflect.MethodInfoBuilder;
import com.vmware.xenon.common.http.netty.NettyHttpServiceClient;

/**
 * Created by mageshwaranr on 8/9/2016.
 */
public class JaxRsServiceClient implements InvocationHandler {

  private final List<URI> baseUris;
  private final Class<?> resourceInterface;
  private List<MethodInfo> httpMethods;
  private ServiceRequestSender client;
  private String referer = "/jaxrs/xenon/client";
  private int uriIndex = 0;
  private ServiceHost host;

//  private Map<String,List<Method>> methodCache

  private static Logger log = LoggerFactory.getLogger(JaxRsServiceClient.class);

  private JaxRsServiceClient(Class<?> resourceInterface, URI baseUri) {
    this.resourceInterface = resourceInterface;
    this.baseUris = new ArrayList<>();
    this.baseUris.add(baseUri);
  }

  private JaxRsServiceClient(Class<?> resourceInterface, List<URI> baseUris) {
    this.resourceInterface = resourceInterface;
    this.baseUris = baseUris;
  }

  private void init() {
    this.httpMethods = MethodInfoBuilder.parseJaxRsMethodInfo(resourceInterface);
    if (host == null) {
      try {
        ServiceClient serviceClient = NettyHttpServiceClient.create(
            JaxRsServiceClient.class.getSimpleName(),
            Executors.newFixedThreadPool(DEFAULT_THREAD_COUNT),
            Executors.newScheduledThreadPool(DEFAULT_THREAD_COUNT));
        serviceClient.start();
        this.client = serviceClient;
      } catch (URISyntaxException illegalUri) {
        throw new RuntimeException("Unable to create ServiceClient. Is this because of HTTP PROXY settings ?", illegalUri);
      }
    } else {
      host.getClient().start();
      this.client = host;
      this.referer = host.getUri().toString();
    }
  }

  /**
   * @param resourceInterface interface holding the contract
   * @param baseUri           base URI pointing to the host and port.
   *                          Note that, if @PATH annotation on interface is found as a child path of baseUri, path value will be ignored
   * @param <C>               An instance of given interface is provided
   * @return
   */
  public static <C> C newProxy(Class<C> resourceInterface, URI baseUri) {
    URI serviceUri = addPathFromAnnotation(resourceInterface, baseUri);
    JaxRsServiceClient factory = new JaxRsServiceClient(resourceInterface, serviceUri);
    factory.init();
    return (C) Proxy.newProxyInstance(resourceInterface.getClassLoader(), new Class[] {resourceInterface}, factory);
  }

  private static URI addPathFromAnnotation(AnnotatedElement element, URI parent) {
    String pathToBeAdded = parsePath(element);
    return extendUri(parent, pathToBeAdded);
  }


  private static String parsePath(AnnotatedElement element) {
    if (element != null) {
      Path path = element.getAnnotation(Path.class);
      if (path != null) {
        return path.value();
      }
    }
    return null;
  }

  Object invoke(MethodInfo httpMethod, Object[] args) throws Throwable {
    String methodUri = httpMethod.getUriPath();
    List<String> queryUri = new ArrayList<>();

    Operation op = new Operation();
    if (op.getCookies() == null) {
      op.setCookies(new HashMap<>());
    }
    for (ParamMetadata paramMetadata : httpMethod.getParameters()) {
      if (args[paramMetadata.getParameterIndex()] == null) {
        continue;
      }
      String paramValue = String.valueOf(args[paramMetadata.getParameterIndex()]);
      if (paramMetadata.getType() == ParamMetadata.Type.PATH) {
        String regex = Pattern.quote("{" + paramMetadata.getName() + "}");
        methodUri = methodUri.replaceAll(regex, paramValue);
      } else if (paramMetadata.getType() == ParamMetadata.Type.QUERY) {
        queryUri.add(paramMetadata.getName());
        queryUri.add(paramValue);
      } else if (paramMetadata.getType() == ParamMetadata.Type.BODY) {
        op.setBody(args[paramMetadata.getParameterIndex()]);
      } else if (paramMetadata.getType() == ParamMetadata.Type.HEADER) {
        op.addRequestHeader(paramMetadata.getName(), paramValue);
      } else if (paramMetadata.getType() == ParamMetadata.Type.COOKIE) {
        op.getCookies().put(paramMetadata.getName(), paramValue);
      }
    }

    op.setUri(extendUriWithQuery(extendUri(baseUris.get(uriIndex), methodUri), queryUri.toArray(new String[] {})));
    op.setAction(httpMethod.getAction());
    op.setReferer(referer);
    uriIndex = ++uriIndex % baseUris.size();
    if (httpMethod.isAsyncApi()) {
      return invokeASync(httpMethod, op);
    } else {
      return invokeSync(httpMethod, op);
    }

  }

  private Object invokeASync(MethodInfo httpMethod, Operation op) throws Throwable {
    Class returnType = httpMethod.getReturnType();
    CompletableFuture future = new CompletableFuture<>();
    op.setCompletion(((completedOp, failure) -> {
      if (failure == null) { // happy flow
        if (returnType != Void.TYPE) {
          try {
            future.complete(getValidReturnValue(completedOp, httpMethod));
          } catch (Exception e) {
            future.completeExceptionally(e);
          }
        } else {
          future.complete(Void.TYPE);
        }
      } else { // on exception
        Throwable actual = failure;
        try {
          ExceptionResponse errorResponse = completedOp.getBody(ExceptionResponse.class);
          if (errorResponse != null) {
            actual = errorResponse.toError();
          }
        } catch (Exception e) {
          // don't care. The body is of different format. Cascade the failure
          if (completedOp.getBodyRaw() != null) {
            actual = new RuntimeException(completedOp.getBodyRaw().toString(), actual);
          }

        } finally {
          future.completeExceptionally(actual);
        }

      }

    }));
    op.sendWith(client);
    return future;
  }

  private Object invokeSync(MethodInfo httpMethod, Operation op) throws Throwable {
    Object[] outputHolder = new Object[2];
    CountDownLatch latch = new CountDownLatch(1);
    op.setCompletion(((completedOp, failure) -> {
      try {
        if (failure == null) {  // happy flow
          Class<?> returnType = httpMethod.getReturnType();
          if (returnType != Void.TYPE) {
            outputHolder[0] = getValidReturnValue(completedOp, httpMethod);
          }
        } else { // on exception
          Throwable actual = failure;
          try {
            ExceptionResponse errorResponse = completedOp.getBody(ExceptionResponse.class);
            if (errorResponse != null) {
              actual = errorResponse.toError();
            }
          } catch (Exception e) {
            // don't care. The body is of different format. Cascade the failure
            if (completedOp.getBodyRaw() != null) {
              actual = new RuntimeException(completedOp.getBodyRaw().toString(), actual);
            }
          } finally {
            outputHolder[1] = actual;
          }
        }
      } catch (Throwable e) {
        outputHolder[1] = e;
      } finally {
        latch.countDown();
      }
    }));
    op.sendWith(client);
    latch.await(60, TimeUnit.SECONDS);
    if (outputHolder[1] == null) {
      return outputHolder[0];
    } else {
      throw (Throwable) outputHolder[1];
    }
  }

  private Object getValidReturnValue(Operation completedOp, MethodInfo httpMethod) {
    Class<?> returnType = httpMethod.getReturnType();
    if (Response.class.equals(returnType)) {
      return new InboundResponse(completedOp);
    } else if (completedOp.hasBody()) {
      if (httpMethod.getType() instanceof ParameterizedType) {
        String json = Utils.toJson(completedOp.getBodyRaw());
        return Utils.fromJson(json, httpMethod.getType());
      } else {
        return completedOp.getBody(returnType);
      }
    } else if (List.class.isAssignableFrom(returnType)) {
      return Collections.emptyList();
    } else if (Set.class.isAssignableFrom(returnType)) {
      return Collections.emptySet();
    } else if (Map.class.isAssignableFrom(returnType)) {
      return Collections.emptyMap();
    } else {
      return null;
    }
  }

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    // get the interface describing the resource
    Class<?> proxyIfc = proxy.getClass().getInterfaces()[0];
    if (proxyIfc.equals(resourceInterface)) {
      Optional<MethodInfo> first = this.httpMethods.stream()
          .filter(httpMethod -> httpMethod.getMethod().equals(method))
          .findFirst();

      MethodInfo methodInfo = first.orElseGet(() ->
          MethodInfoBuilder.generateMethodInfo(new Method[] {method}).get(0)
      );

      return invoke(methodInfo, args);

    } else {
      throw new IllegalStateException("Proxy interface is not same as service interface");
    }
  }


  public static ClientBuilder newBuilder() {
    return new ClientBuilder();
  }

  /**
   * Builder class to set various options
   */
  public static class ClientBuilder {

    private Class<?> resourceInterface;
    private URI baseUri;
    private ServiceInfo serviceInfo;
    private URI dnsHostUri;
    private ServiceHost host;

    public ClientBuilder() {
    }


    public ClientBuilder withResourceInterface(Class<?> resourceInterface) {
      this.resourceInterface = resourceInterface;
      return this;
    }

    public ClientBuilder withServiceInfo(ServiceInfo serviceInfo) {
      this.serviceInfo = serviceInfo;
      return this;
    }

    public ClientBuilder withDnsHostUri(String dnsHostUri) {
      this.dnsHostUri = buildUri(dnsHostUri);
      return this;
    }

    public ClientBuilder withBaseUri(String baseUri) {
      this.baseUri = buildUri(baseUri);
      return this;
    }

    public ClientBuilder withHost(ServiceHost host) {
      this.host = host;
      this.baseUri = host.getPublicUri();
      return this;
    }

    public ClientBuilder withBaseUri(URI baseUri) {
      this.baseUri = baseUri;
      return this;
    }

    public <C> C build() {
      JaxRsServiceClient factory;
      if (dnsHostUri != null && serviceInfo != null) {
        DnsState serviceByName = JaxRsServiceClient.newProxy(XenonDnsService.class, dnsHostUri).findServiceByName(serviceInfo.serviceName());
        if (serviceByName.nodeReferences.isEmpty()) {
          throw new IllegalArgumentException("Unable to find a service with name " + serviceByName + " in DNS hosted at " + dnsHostUri);
        }
        List<URI> serviceUris = serviceByName.nodeReferences.stream().map(hostUri -> extendUri(hostUri, parsePath(resourceInterface))).collect(toList());
        factory = new JaxRsServiceClient(resourceInterface, serviceUris);
      } else if (baseUri == null) {
        throw new IllegalArgumentException("Either BaseUri of the target resource is required or DNS Host URI and Service Name is required");
      } else {
        URI serviceUri = addPathFromAnnotation(resourceInterface, baseUri);
        factory = new JaxRsServiceClient(resourceInterface, serviceUri);
      }
      factory.host = host;
      factory.init();
      return (C) Proxy.newProxyInstance(resourceInterface.getClassLoader(), new Class[] {resourceInterface}, factory);
    }
  }


}
