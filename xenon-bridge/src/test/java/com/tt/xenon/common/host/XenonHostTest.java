package com.tt.xenon.common.host;

import com.github.tomakehurst.wiremock.junit.WireMockClassRule;
import com.tt.xenon.common.client.ServiceClientUtil;
import com.tt.xenon.common.client.TestServicesInfo;
import com.tt.xenon.common.dns.XenonDnsService;
import com.tt.xenon.common.dns.document.DnsState;
import com.vmware.xenon.common.StatelessService;
import org.junit.*;

import java.net.URI;

import static com.vmware.xenon.common.UriUtils.buildUri;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by mageshwaranr on 8/31/2016.
 */
public class XenonHostTest {

  private XenonHost dnsHost;
  private XenonDnsService xenonDnsService;
  private URI mockServiceUri;

  @ClassRule
  public static WireMockClassRule wireMockRule1 = new WireMockClassRule(9090);

  @Before
  public void initService() throws Throwable {
    dnsHost = XenonHost.newBuilder()
        .withArguments(new String[] {"--port=6060"})
        .buildAndStart();
    ServiceClientUtil.startAndWaitForVrbcDnsService(dnsHost);
    xenonDnsService = ServiceClientUtil.newDnsServiceClient(dnsHost);
  }

  @Test
  public void testDnsLookup() throws Throwable {
    // register mock service
    mockServiceUri = buildUri("http://127.0.0.1:" + wireMockRule1.port());
    xenonDnsService.registerService(ServiceClientUtil.toDnsState(TestServicesInfo.SYNC_MOCK, mockServiceUri));

    //look up
    DnsState serviceObtained = xenonDnsService.findServiceByName(TestServicesInfo.SYNC_MOCK.serviceName());
    assertNotNull(serviceObtained);
    assertEquals(1, serviceObtained.nodeReferences.size());
    Assert.assertEquals(TestServicesInfo.SYNC_MOCK.serviceLink(), serviceObtained.serviceLink);
    serviceObtained.nodeReferences.forEach(uri -> assertEquals(mockServiceUri, uri));
  }

  @Test
  public void testAutoRegistrationToDns() throws Throwable {

    XenonHost xenonHost = XenonHost.newBuilder()
        .withArguments(new String[] {"--port=7070"})
        .withDns(dnsHost.getPublicUri().toString())
        .withService(TestServicesInfo.DUMMY_SERVICE, new DummyService())
        .buildAndStart();

    try {
      //look up
      DnsState serviceObtained = xenonDnsService.findServiceByName(TestServicesInfo.DUMMY_SERVICE.serviceName());
      assertNotNull(serviceObtained);
      assertEquals(1, serviceObtained.nodeReferences.size());
      Assert.assertEquals(TestServicesInfo.DUMMY_SERVICE.serviceLink(), serviceObtained.serviceLink);
      serviceObtained.nodeReferences.forEach(uri -> assertEquals(xenonHost.getUri(), uri));
    } finally {
      xenonHost.stop();
    }
  }

  @After
  public void tearDown() {
    dnsHost.stop();
  }


  public class DummyService extends StatelessService {

  }

}