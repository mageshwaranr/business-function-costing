package com.tt.xenon.common.client;

import com.vmware.xenon.common.Operation;
import com.vmware.xenon.common.Utils;

import javax.ws.rs.core.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.*;

import static java.util.Arrays.asList;

/**
 * Created by mageshwaranr on 9/8/2016.
 */
class InboundResponse extends Response {

  private Operation completedOperation;

  InboundResponse(Operation completedOperation) {
    this.completedOperation = completedOperation;

  }


  public int getStatus() {
    return completedOperation.getStatusCode();
  }

  public StatusType getStatusInfo() {
    final Response.Status responseStatus = Response.Status.fromStatusCode(getStatus());
    return new Response.StatusType() {

      public Status.Family getFamily() {
        return responseStatus.getFamily();
      }

      public String getReasonPhrase() {
        return responseStatus.getReasonPhrase();
      }

      public int getStatusCode() {
        return responseStatus.getStatusCode();
      }

    };
  }

  public Object getEntity() {
    return completedOperation.getBodyRaw();
  }

  public boolean hasEntity() {
    return completedOperation.hasBody();
  }

  public MultivaluedMap<String, Object> getMetadata() {
    return getHeaders();
  }

  public MultivaluedMap<String, Object> getHeaders() {
    MultivaluedHashMap<String, Object> headers = new MultivaluedHashMap<>();
    completedOperation.getResponseHeaders().forEach((name, value) -> headers.put(name, asList(value)));
    return headers;
  }

  public MultivaluedMap<String, String> getStringHeaders() {
    MultivaluedHashMap<String, String> headers = new MultivaluedHashMap<>();
    completedOperation.getResponseHeaders().forEach((name, value) -> headers.put(name, asList(value)));
    return headers;
  }

  public String getHeaderString(String header) {
    return completedOperation.getResponseHeader(header);
  }

  public Set<String> getAllowedMethods() {
    String methodValues = completedOperation.getResponseHeader(HttpHeaders.ALLOW);
    if (methodValues == null) {
      return Collections.emptySet();
    } else {
      Set<String> methods = new HashSet<>();
      for (String o : methodValues.split(",")) {
        methods.add(o);
      }
      return methods;
    }
  }


  public Map<String, NewCookie> getCookies() {
    Map<String, String> cookieValues = completedOperation.getCookies();
    if (cookieValues == null) {
      return Collections.emptyMap();
    } else {
      Map<String, NewCookie> cookies = new HashMap<>();
      cookieValues.forEach((name, value) -> cookies.put(name, new NewCookie(name, value)));
      return cookies;
    }
  }

  public Date getDate() {
    return null;
  }

  public EntityTag getEntityTag() {
    return null;
  }

  private String getHeader(String name) {
    return completedOperation.getResponseHeader(name);
  }

  public Locale getLanguage() {
    return getLocale(getHeader(HttpHeaders.CONTENT_LANGUAGE));
  }

  public Date getLastModified() {
    return null;
  }

  public int getLength() {
    return (int)completedOperation.getContentLength();
  }

  public URI getLocation() {
    String header = getHeader(HttpHeaders.LOCATION);
    return header == null ? null : URI.create(header);
  }

  public MediaType getMediaType() {
    String header = completedOperation.getContentType();
    if (header == null) {
      return null;
    } else {
      String[] split = header.split("/");
      if (split.length == 2) {
        return new MediaType(split[0], split[1]);
      } else {
        return null;
      }
    }
  }

  public boolean hasLink(String relation) {
    return getLink(relation) != null;
  }

  public Link getLink(String relation) {
    return null;
  }

  public Link.Builder getLinkBuilder(String relation) {
    return null;
  }

  public Set<Link> getLinks() {
    return new HashSet<>(getAllLinks().values());
  }

  private Map<String, Link> getAllLinks() {
    return Collections.emptyMap();
  }

  public <T> T readEntity(Class<T> cls) throws IllegalStateException {
    return completedOperation.getBody(cls);
  }

  public <T> T readEntity(GenericType<T> genType) throws IllegalStateException {
    Type type = genType.getType();
    String json = Utils.toJson(completedOperation.getBodyRaw());
    return Utils.fromJson(json, type);
  }

  public <T> T readEntity(Class<T> cls, Annotation[] anns) throws IllegalStateException {
    throw new UnsupportedOperationException("Not yet implemented");
  }

  @SuppressWarnings("unchecked")
  public <T> T readEntity(GenericType<T> genType, Annotation[] anns) throws IllegalStateException {
    throw new UnsupportedOperationException("Not yet implemented");
  }

//  <T> T doReadEntity(Class<T> cls, Type t, Annotation[] anns) throws IllegalStateException {
//
//    if (!hasEntity()) {
//      return null;
//    }

//    if (cls.isAssignableFrom(entity.getClass())) {
//      T response = cls.cast(entity);
//      closeIfNotBufferred(cls);
//      return response;
//    }
//
//    if (responseMessage != null && entity instanceof InputStream) {
//      MediaType mediaType = getMediaType();
//
//      List<ReaderInterceptor> readers = ProviderFactory.getInstance(responseMessage)
//          .createMessageBodyReaderInterceptor(cls, t, anns, mediaType,
//              responseMessage.getExchange().getOutMessage());
//      if (readers != null) {
//        try {
//          responseMessage.put(Message.PROTOCOL_HEADERS, this.getMetadata());
//          return cls.cast(JAXRSUtils.readFromMessageBodyReader(readers, cls, t,
//              anns,
//              InputStream.class.cast(entity),
//              mediaType,
//              responseMessage));
//        } catch (Exception ex) {
//          throw new MessageProcessingException(ex);
//        } finally {
//          closeIfNotBufferred(cls);
//        }
//      }
//    }
//
//    throw new IllegalStateException("No Message Body reader is available");
//  }

  public boolean bufferEntity() throws IllegalStateException {
    throw new IllegalStateException();
  }

  public void close() {

  }


  //utils
  private static Locale getLocale(String value) {
    if (value == null) {
      return null;
    }

    String[] values = value.split("-");
    if (values.length == 0 || values.length > 2) {
      throw new IllegalArgumentException("Illegal locale value : " + value);
    }
    if (values.length == 1) {
      return new Locale(values[0]);
    } else {
      return new Locale(values[0], values[1]);
    }

  }



}
