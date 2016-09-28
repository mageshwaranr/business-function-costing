package com.tt.xenon.common.router;

import com.tt.xenon.common.annotations.OperationBody;
import com.tt.xenon.common.annotations.PATCH;
import com.tt.xenon.common.error.XenonHttpRuntimeException;
import com.tt.xenon.common.reflect.MethodInfo;
import com.tt.xenon.common.reflect.MethodInfoBuilder;
import com.vmware.xenon.common.Operation;
import com.vmware.xenon.common.Service;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.ws.rs.*;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by mageshwaranr on 8/17/2016.
 */
@RunWith(MockitoJUnitRunner.class)
public class RoutingOperationHandlerTest {

  @Mock
  private Operation op;

  @Mock
  private MockXenonServiceWithJaxRsAnnotations service;

  @Mock
  private List<String> mockedBody;

  @Test
  public void testOperationHandlerForGetAndPostParam() throws Exception {
    //given
    Method getMethod = MockXenonServiceWithJaxRsAnnotations.class.getMethod("testGetAction", String.class, String.class, Operation.class);

    List<MethodInfo> methodInfos = MethodInfoBuilder.generateMethodInfo(new Method[] {getMethod});

    String path = "/resource/get/{path}";
    RoutingOperationHandler testClass = new RoutingOperationHandler(path, methodInfos.get(0), service);
    testClass.init();
    when(op.getUri()).thenReturn(new URI("/resource/get/PathValue1?query=QueryValue1"));
    //test
    testClass.accept(op);
    //verify
    verify(service).testGetAction("QueryValue1", "PathValue1", op);
    assertFalse(testClass.hasValidReturnType);
    assertTrue(testClass.hasOperationAsAnArgument);
  }

  @Test
  public void testOperationHandlerForPatchWithBodyParam() throws Exception {
    //given
    Method patchMethod = MockXenonServiceWithJaxRsAnnotations.class.getMethod("testPatchAction", String.class, List.class, Operation.class);
    String path = "/resource/patch/{path}";

    List<MethodInfo> methodInfos = MethodInfoBuilder.generateMethodInfo(new Method[] {patchMethod});
    RoutingOperationHandler testClass = new RoutingOperationHandler(path, methodInfos.get(0), service);

    testClass.init();
    when(op.getUri()).thenReturn(new URI("/resource/patch/PathValue1/PathValue2"));
    when(op.getBody(List.class)).thenReturn(mockedBody);
    //test
    testClass.accept(op);
    //verify
    verify(service).testPatchAction("PathValue1", mockedBody, op);
    assertFalse(testClass.hasValidReturnType);
    assertTrue(testClass.hasOperationAsAnArgument);
  }

  @Test
  public void testOperationHandlerForDeleteWithOutOperation() throws Exception {
    //given
    Method deleteMethod = MockXenonServiceWithJaxRsAnnotations.class.getMethod("testDeleteAction",
        String.class, String.class, String.class);
    String path = "/resource/delete/{path}";

    List<MethodInfo> methodInfos = MethodInfoBuilder.generateMethodInfo(new Method[] {deleteMethod});


    RoutingOperationHandler testClass = new RoutingOperationHandler(path, methodInfos.get(0), service);
    testClass.init();
    when(op.getUri()).thenReturn(new URI("/resource/delete/PathValue1/PathValue2"));
    when(op.getRequestHeader("header")).thenReturn("headerValue");
    when(service.testDeleteAction("PathValue1", "headerValue", null)).thenReturn(mockedBody);
    //test
    testClass.accept(op);
    //verify
    verify(service).testDeleteAction("PathValue1", "headerValue", null);
    assertTrue(testClass.hasValidReturnType);
    assertFalse(testClass.hasOperationAsAnArgument);
    verify(op).setBody(mockedBody);
    verify(op).complete();
  }


  @Test
  public void testOperationHandlerForPost() throws Exception {
    //given
    Method postMethod = MockXenonServiceWithJaxRsAnnotations.class.getMethod("testPostAction", String.class, List.class);
    String path = "/resource/post/{path}";

    List<MethodInfo> methodInfos = MethodInfoBuilder.generateMethodInfo(new Method[] {postMethod});

    RoutingOperationHandler testClass = new RoutingOperationHandler(path, methodInfos.get(0), service);
    testClass.init();
    when(op.getUri()).thenReturn(new URI("/resource/post/PathValue1"));
    CompletableFuture<List<String>> successFuture = new CompletableFuture<>();
    when(service.testPostAction("PathValue1", mockedBody))
        .thenReturn(successFuture);
    when(op.getBody(List.class)).thenReturn(mockedBody);
    //test
    testClass.accept(op);
    List<String> result = asList("1", "2");
    successFuture.complete(result);

    //verify
    verify(service).testPostAction("PathValue1", mockedBody);
    assertTrue(testClass.hasValidReturnType);
    assertFalse(testClass.hasOperationAsAnArgument);
    verify(op).setBody(result);
    verify(op).complete();
  }

  @Test
  public void testOperationHandlerForPostWithExceptionFlow() throws Exception {
    //given
    Method postMethod = MockXenonServiceWithJaxRsAnnotations.class.getMethod("testPostAction", String.class, List.class);
    String path = "/resource/post/{path}";

    List<MethodInfo> methodInfos = MethodInfoBuilder.generateMethodInfo(new Method[] {postMethod});

    RoutingOperationHandler testClass = new RoutingOperationHandler(path, methodInfos.get(0), service);
    testClass.init();
    when(op.getUri()).thenReturn(new URI("/resource/post/PathValue1"));
    CompletableFuture<List<String>> failureFuture1 = new CompletableFuture<>();
    CompletableFuture<List<String>> failureFuture2 = new CompletableFuture<>();
    when(service.testPostAction("PathValue1", mockedBody))
        .thenReturn(failureFuture1)
        .thenReturn(failureFuture2);
    when(op.getBody(List.class)).thenReturn(mockedBody);
    //test
    testClass.accept(op);
    failureFuture1.completeExceptionally(new XenonHttpRuntimeException(100));

    //verify
    verify(service).testPostAction("PathValue1", mockedBody);
    assertTrue(testClass.hasValidReturnType);
    assertFalse(testClass.hasOperationAsAnArgument);
    verify(op).fail(any(XenonHttpRuntimeException.class), any(ExceptionResponse.class));

    //setup
    failureFuture2.completeExceptionally(new CompletionException(new NullPointerException()));
    //test
    testClass.accept(op);
    //verify
    verify(service, times(2)).testPostAction("PathValue1", mockedBody);
    assertTrue(testClass.hasValidReturnType);
    assertFalse(testClass.hasOperationAsAnArgument);
    verify(op, times(2)).fail(any(NullPointerException.class), any(ExceptionResponse.class));
  }

  @Test
  public void testOperationHandlerForPostWithExceptionFlow2() throws Exception {
    //given
    Method postMethod = MockXenonServiceWithJaxRsAnnotations.class.getMethod("testPostAction", String.class, List.class);
    String path = "/resource/post/{path}";

    List<MethodInfo> methodInfos = MethodInfoBuilder.generateMethodInfo(new Method[] {postMethod});

    RoutingOperationHandler testClass = new RoutingOperationHandler(path, methodInfos.get(0), service);
    testClass.init();
    when(op.getUri()).thenReturn(new URI("/resource/post/PathValue1"));

    when(service.testPostAction("PathValue1", mockedBody))
        .thenThrow(new NullPointerException());
    when(op.getBody(List.class)).thenReturn(mockedBody);

    //test
    testClass.accept(op);

    //verify
    verify(service).testPostAction("PathValue1", mockedBody);
    assertTrue(testClass.hasValidReturnType);
    assertFalse(testClass.hasOperationAsAnArgument);
    verify(op).fail(any(NullPointerException.class), any(String.class));

  }


  interface MockXenonServiceWithJaxRsAnnotations extends Service {

    @GET
    void testGetAction(@QueryParam("query") String query, @PathParam("path") String path, Operation op);


    @PATCH
    @Path("/patch/{path}")
    void testPatchAction(@PathParam("path") String path, @OperationBody List<String> contents, Operation op);

    @DELETE
    @Path("/delete/{path}")
    List<String> testDeleteAction(@PathParam("path") String path, @HeaderParam("header") String header,
                                  @CookieParam("cookie") String cookie);

    @POST
    @Path("/post/{path}")
    CompletableFuture<List<String>> testPostAction(@PathParam("path") String path,
                                                   @OperationBody List<String> contents);

  }

}