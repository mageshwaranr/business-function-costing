package com.tt.xenon.common.client;

import com.tt.xenon.common.VrbcServiceInfo;
import com.tt.xenon.common.dns.XenonDnsService;
import com.tt.xenon.common.dns.document.DnsState;
import com.tt.xenon.common.query.XenonQueryService;
import com.tt.xenon.common.dns.DnsServiceInfo;
import com.vmware.xenon.common.ServiceHost;
import com.vmware.xenon.dns.services.DNSServices;

import javax.ws.rs.Path;
import java.net.URI;
import java.util.HashSet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

/**
 * Created by mageshwaranr on 9/1/2016.
 */
public class ServiceClientUtil {

//  private static String QUERY_SERVICE_URI = XenonQueryService.class.getAnnotation(Path.class).value();

  /**
   * Returns XenonQueryService client instance pointing to given host
   *
   * @param host in which XenonQueryService is running
   * @return
   */
  public static XenonQueryService newQueryServiceClient(ServiceHost host) {
    return newQueryServiceClient(host.getPublicUri());
  }

  /**
   * Returns XenonQueryService client instance pointing to given base URI
   *
   * @param baseUri
   * @return
   */
  public static XenonQueryService newQueryServiceClient(URI baseUri) {
    return JaxRsServiceClient.newBuilder()
        .withBaseUri(baseUri)
        .withResourceInterface(XenonQueryService.class)
        .build();
  }

  /**
   * Returns XenonDnsService client instance pointing to given host
   *
   * @param host in which the DNS service is running
   * @return
   */
  public static XenonDnsService newDnsServiceClient(ServiceHost host) {
    return newDnsServiceClient(host.getPublicUri());
  }

  /**
   * Returns XenonDnsService client instance pointing to given base URI
   *
   * @param baseUri
   * @return
   */
  public static XenonDnsService newDnsServiceClient(URI baseUri) {
    return JaxRsServiceClient.newBuilder()
        .withBaseUri(baseUri)
        .withResourceInterface(XenonDnsService.class)
        .build();
  }

  /**
   *
   * @param serviceInfo
   * @param serviceHostUri
   * @return
   */
  public static DnsState toDnsState(VrbcServiceInfo serviceInfo, URI serviceHostUri) {
    DnsState state = new DnsState();
    state.serviceLink = serviceInfo.serviceLink();
    state.serviceName = serviceInfo.serviceName();
    state.nodeReferences = new HashSet<>(singletonList(serviceHostUri));
    state.tags = new HashSet<>(asList(serviceInfo.tags()));
    state.documentSelfLink = serviceInfo.serviceName();
    if (serviceInfo.hasAvailability()) {
      state.healthCheckLink = serviceInfo.healthCheckLink();
    }
    return state;
  }

  /**
   * Starts DNS service and waits for its availability
   *
   * @param dnsHost host in which dns services needs to be started
   * @throws Throwable
   */
  public static void startAndWaitForVrbcDnsService(ServiceHost dnsHost) throws Throwable {
    DNSServices.startServices(dnsHost, null);
    waitForServiceAvailability(dnsHost, DnsServiceInfo.DNS);
    waitForServiceAvailability(dnsHost, DnsServiceInfo.DNS_QUERY);
  }


  public static VrbcServiceInfo fromJaxRsResource(Class<?> jaxRsContract) {
    return new VrbcServiceInfo() {
      @Override
      public String serviceLink() {
        return jaxRsContract.getAnnotation(Path.class).value();
      }

      @Override
      public String serviceName() {
        return jaxRsContract.getSimpleName();
      }
    };
  }

  public static VrbcServiceInfo cloneWithAvailabilityYes(VrbcServiceInfo info) {
    return new VrbcServiceInfo() {
      @Override
      public String serviceLink() {
        return info.serviceLink();
      }

      @Override
      public String serviceName() {
        return info.serviceName();
      }

      @Override
      public boolean hasAvailability() {
        return true;
      }
    };
  }

  public static void waitForServiceAvailability(ServiceHost host, VrbcServiceInfo serviceInfo) throws InterruptedException {
    CountDownLatch latch = new CountDownLatch(1);
    host.registerForServiceAvailability(((completedOp, failure) -> latch.countDown()), serviceInfo.serviceLink());
    latch.await(500, TimeUnit.MILLISECONDS);
  }


}
