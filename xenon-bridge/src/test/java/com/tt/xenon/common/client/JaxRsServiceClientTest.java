package com.tt.xenon.common.client;

import com.github.tomakehurst.wiremock.client.WireMock;

import com.github.tomakehurst.wiremock.junit.WireMockClassRule;
import com.google.gson.Gson;
import com.tt.xenon.common.error.XenonHttpRuntimeException;
import com.vmware.xenon.common.ServiceErrorResponse;

import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static java.util.Arrays.asList;
import static org.junit.Assert.*;

/**
 * Created by mageshwaranr on 8/22/2016.
 */
public class JaxRsServiceClientTest {

  @ClassRule
  public static WireMockClassRule wireMockRule = new WireMockClassRule(8089);


  private static MockServiceClient clientService;

  @BeforeClass
  public static void initJaxRsClientInvoker() {
    clientService = JaxRsServiceClient.newBuilder()
        .withBaseUri("http://localhost:" + wireMockRule.port())
        .withResourceInterface(MockServiceClient.class)
        .build();
    WireMock.reset();
  }

  @Test
  public void testGetAction() {
    stubFor(get(urlEqualTo("/vrbc/xenon/util/test/get/pathValue1?query=queryValue1"))
        .willReturn(aResponse()
            .withHeader("Content-Type", "application/json")
            .withBody(successResp())));

    Map<String, String> action = clientService.getAction("queryValue1", "pathValue1");
    assertNotNull(action);
    assertEquals("success", action.get("result"));
  }


  @Test(expected = Exception.class)
  public void testGetActionWithReturnTypeException() {
    stubFor(get(urlEqualTo("/vrbc/xenon/util/test/get/pathValue1?query=queryValue1"))
        .willReturn(aResponse()
            .withBody("string body instead of map")));

    clientService.getAction("queryValue1", "pathValue1");
    fail("Shouldn't reach here");
  }


  @Test
  public void testGetActionWithNoBody() {
    stubFor(get(urlEqualTo("/vrbc/xenon/util/test/get/pathValue1?query=queryValue1"))
        .willReturn(aResponse()));

    Map<String, String> action = clientService.getAction("queryValue1", "pathValue1");
    assertNotNull(action);
    assertTrue(action.isEmpty());
  }


  @Test(expected = XenonHttpRuntimeException.class)
  public void testGetActionWithXenonErrorResponse() {
    stubFor(get(urlEqualTo("/vrbc/xenon/util/test/get/pathValue1?query=queryValue1"))
        .willReturn(aResponse()
            .withBody(xenonServiceErrorResp())
            .withStatus(500)));

    clientService.getAction("queryValue1", "pathValue1");
    fail("Shouldn't reach here");
  }


  @Test(expected = XenonHttpRuntimeException.class)
  public void testGetActionWithServiceException() {
    stubFor(get(urlEqualTo("/vrbc/xenon/util/test/get/pathValue1?query=queryValue1"))
        .willReturn(aResponse()
            .withStatus(404)));

    clientService.getAction("queryValue1", "pathValue1");
    fail("Shouldn't reach here");
  }

  @Test
  public void testPostAction() {
    List<String> contents = asList("POST_BODY_1", "POST_BODY_2");
    stubFor(post(urlEqualTo("/vrbc/xenon/util/test/post"))
        .withRequestBody(equalToJson(new Gson().toJson(contents)))
        .willReturn(aResponse()
            .withHeader("Content-Type", "application/json")
            .withBody(successResp())));

    Map<String, String> action = clientService.postAction(new ArrayList<>(contents));
    assertNotNull(action);
    assertEquals("success", action.get("result"));
  }


  @Test(expected = Exception.class)
  public void testPostActionFailure() {
    List<String> contents = asList("POST_BODY_1", "POST_BODY_2");
    stubFor(post(urlEqualTo("/vrbc/xenon/util/test/post"))
        .withRequestBody(equalToJson(new Gson().toJson(contents)))
        .willReturn(aResponse()
            .withHeader("Content-Type", "application/json")
            .withBody(successResp())));

    Map<String, String> action = clientService.postAction(new ArrayList<>());
    assertNotNull(action);
    assertEquals("success", action.get("result"));
  }


  @Test
  public void testPatchAction() throws Exception {
    List<Integer> contents = asList(1, 2, 3);
    stubFor(patch(urlEqualTo("/vrbc/xenon/util/test/patch"))
        .withRequestBody(equalToJson(new Gson().toJson(contents)))
        .willReturn(aResponse()
            .withHeader("Content-Type", "application/json")
            .withBody(successResp())));

    Map<String, String> action = clientService.patchAction(new ArrayList<>(contents));
    assertNotNull(action);
    assertEquals("success", action.get("result"));
  }

  @Test
  public void testDeleteAction() throws Exception {
    stubFor(delete(urlEqualTo("/vrbc/xenon/util/test/delete/delete_path_param_1"))
        .willReturn(aResponse()
            .withHeader("Content-Type", "application/json")
            .withBody("[ \"success\" ] ")));

    List<String> action = clientService.deleteAction("delete_path_param_1");
    assertNotNull(action);
    assertEquals("success", action.get(0));

  }

  @Test
  public void testPostActionWithAuthInfo() {
    List<Integer> contents = asList(1, 2, 3);
    stubFor(post(urlEqualTo("/vrbc/xenon/util/test/post/auth"))
        .withRequestBody(equalToJson(new Gson().toJson(contents)))
        .withCookie("cookie", equalTo("auth-cookie-value"))
        .withHeader("header", equalTo("auth-header-value"))
        .willReturn(aResponse()
            .withBody("[ \"success\" ] ")));

    List<String> action = clientService.postActionWithAuthInfo("auth-header-value",
        "auth-cookie-value", new ArrayList<>(contents));
    assertNotNull(action);
    assertEquals("success", action.get(0));
  }

  @Test
  public void testPostActionWithReturnResponse() {
    List<String> contents = asList("POST_BODY_1", "POST_BODY_2");

    Map<String, Object> outer = new HashMap<>();
    Map<String, Object> inner = new HashMap<>();
    inner.put("code", 20);
    inner.put("message", "success");
    outer.put("result", inner);
    String successResp = new Gson().toJson(outer);

    stubFor(post(urlEqualTo("/vrbc/xenon/util/test/post"))
        .withRequestBody(equalToJson(new Gson().toJson(contents)))
        .willReturn(aResponse()
            .withHeader("Content-Type", "application/json")
            .withHeader("custom-header", "Custom-Value")
            .withBody(successResp)));

    Response response = clientService.postActionWithReturnResponse(new ArrayList<>(contents));
    assertNotNull(response);
    Map<String, MockServiceClient.SuccessResponse> genericMapResp = response.readEntity(new GenericType<Map<String, MockServiceClient.SuccessResponse>>() {
    });
    assertNotNull(genericMapResp);
    assertEquals("success", genericMapResp.get("result").getMessage());
    assertEquals(20, genericMapResp.get("result").getCode());

    Map<String, Map<String, Object>> genericMapOfMapResp = response.readEntity(new GenericType<Map<String, Map<String, Object>>>() {
    });
    assertNotNull(genericMapOfMapResp);
    assertEquals("success", genericMapOfMapResp.get("result").get("message"));
    assertEquals("20", genericMapOfMapResp.get("result").get("code"));

    assertFalse(response.getHeaders().isEmpty());
    assertEquals("Custom-Value", response.getHeaders().getFirst("custom-header"));
    assertEquals("Custom-Value", response.getHeaderString("custom-header"));
  }

  @Test
  public void testGetActionWithGenericReturn() {
    Map<String, Object> outer = new HashMap<>();
    Map<String, Object> inner = new HashMap<>();
    inner.put("code", 20);
    inner.put("message", "success");
    outer.put("result", inner);
    String successResp = new Gson().toJson(outer);

    stubFor(get(urlEqualTo("/vrbc/xenon/util/test/get/genericReturn"))
        .willReturn(aResponse()
            .withHeader("Content-Type", "application/json")
            .withHeader("custom-header", "Custom-Value")
            .withBody(successResp)));

    Map<String, MockServiceClient.SuccessResponse> genericReturn = clientService.getActionWithGenericReturn();
    assertNotNull(genericReturn);
    assertEquals(1, genericReturn.size());
    assertTrue(genericReturn.containsKey("result"));
    assertEquals("success", genericReturn.get("result").getMessage());
    assertEquals(20, genericReturn.get("result").getCode());
  }



  String successResp() {
    Map<String, String> map = new HashMap<>();
    map.put("result", "success");
    return new Gson().toJson(map);
  }

  String xenonServiceErrorResp() {
    ServiceErrorResponse errorRsp = ServiceErrorResponse.create(new IllegalArgumentException("bad request"), 404);
    return new Gson().toJson(errorRsp);
  }


}