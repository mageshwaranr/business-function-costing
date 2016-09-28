package com.tt.xenon.common.client;

import com.tt.xenon.common.VrbcServiceInfo;
import com.tt.xenon.common.dns.document.DnsState;
import org.junit.Test;

import java.net.URI;

import static com.tt.xenon.common.client.ServiceClientUtil.cloneWithAvailabilityYes;
import static com.tt.xenon.common.client.ServiceClientUtil.fromJaxRsResource;
import static com.tt.xenon.common.client.ServiceClientUtil.toDnsState;
import static com.tt.xenon.common.client.TestServicesInfo.DUMMY_SERVICE;
import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

/**
 * Created by mageshwaranr on 9/2/2016.
 */
public class ServiceClientUtilTest {

  @Test
  public void testToDnsState() throws Exception {

    URI serviceHostUri = new URI("http://localhost:8080");
    DnsState dnsState = toDnsState(cloneWithAvailabilityYes(DUMMY_SERVICE), serviceHostUri);
    assertEquals(DUMMY_SERVICE.serviceLink(), dnsState.serviceLink);
    assertEquals(DUMMY_SERVICE.serviceName(), dnsState.serviceName);
    assertNotNull(dnsState.healthCheckLink);
    assertEquals(1, dnsState.tags.size());
    assertEquals(1, dnsState.nodeReferences.size());
    assertEquals(serviceHostUri, dnsState.nodeReferences.iterator().next());

  }

  @Test
  public void testFromJaxRsResource() throws Exception {
    VrbcServiceInfo vrbcServiceInfo = fromJaxRsResource(MockServiceClient.class);
    assertEquals("/vrbc/xenon/util/test", vrbcServiceInfo.serviceLink());
    assertEquals("MockServiceClient", vrbcServiceInfo.serviceName());
  }


}