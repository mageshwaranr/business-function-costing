package com.tt.xenon.common.client;

import com.github.tomakehurst.wiremock.junit.WireMockClassRule;
import com.google.gson.Gson;
import com.tt.xenon.common.host.XenonHost;
import com.tt.xenon.common.error.XenonHttpRuntimeException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.tt.xenon.common.client.ServiceClientUtil.*;
import static com.vmware.xenon.common.UriUtils.buildUri;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by mageshwaranr on 8/31/2016.
 */
public class JaxRsServiceClientWithDnsTest {

  @ClassRule
  public static WireMockClassRule wireMockRule1 = new WireMockClassRule(9090);

  private static MockServiceClient clientService;

  private static XenonHost dnsHost;


  @BeforeClass
  public static void initJaxRsClientInvoker() throws Throwable {
    dnsHost = XenonHost.newBuilder()
        .withArguments(new String[] {"--port=6060"})
        .buildAndStart();
    startAndWaitForVrbcDnsService(dnsHost);

    newDnsServiceClient(dnsHost).registerService(toDnsState(TestServicesInfo.SYNC_MOCK, buildUri("http://127.0.0.1:" + wireMockRule1.port())));

    clientService = JaxRsServiceClient.newBuilder()
        .withDnsHostUri(dnsHost.getPublicUri().toString())
        .withResourceInterface(MockServiceClient.class)
        .withServiceInfo(TestServicesInfo.SYNC_MOCK)
        .build();

  }

  @Test(expected = XenonHttpRuntimeException.class)
  public void testServiceNotPresent() {

    JaxRsServiceClient.newBuilder()
        .withDnsHostUri(dnsHost.getPublicUri().toString())
        .withResourceInterface(MockServiceClient.class)
        .withServiceInfo(TestServicesInfo.DUMMY_SERVICE)
        .build();

  }


  @Test(expected = IllegalArgumentException.class)
  public void testIncorrectDnsUri() {
    JaxRsServiceClient.newBuilder()
        .withResourceInterface(MockServiceClient.class)
        .withServiceInfo(TestServicesInfo.DUMMY_SERVICE)
        .build();
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

    action = clientService.getAction("queryValue1", "pathValue1");
    assertNotNull(action);
    assertEquals("success", action.get("result"));

  }

  @AfterClass
  public static void shutdownDns() {
    dnsHost.stop();
    wireMockRule1.stop();
  }


  String successResp() {
    Map<String, String> map = new HashMap<>();
    map.put("result", "success");
    return new Gson().toJson(map);
  }


}