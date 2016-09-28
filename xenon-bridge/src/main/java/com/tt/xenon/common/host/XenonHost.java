package com.tt.xenon.common.host;

import com.tt.xenon.common.dns.XenonDnsService;
import com.tt.xenon.common.VrbcServiceInfo;
import com.tt.xenon.common.client.JaxRsServiceClient;
import com.tt.xenon.common.dns.document.DnsState;
import com.vmware.xenon.common.FactoryService;
import com.vmware.xenon.common.Operation;
import com.vmware.xenon.common.ServiceHost;
import com.vmware.xenon.common.StatelessService;
import com.vmware.xenon.services.common.RootNamespaceService;
import com.vmware.xenon.swagger.SwaggerDescriptorService;
import com.vmware.xenon.ui.UiService;
import io.swagger.models.Info;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.tt.xenon.common.client.ServiceClientUtil.cloneWithAvailabilityYes;
import static com.tt.xenon.common.client.ServiceClientUtil.toDnsState;
import static com.vmware.xenon.common.UriUtils.buildUri;


/**
 * Created by mageshwaranr on 8/29/2016.
 */
public class XenonHost extends ServiceHost {

  private static final Logger log = LoggerFactory.getLogger(XenonHost.class);

  private Arguments arguments;
  private String[] args;
  private String dnsHostUri;
  private Optional<XenonDnsService> dnsService;

  public XenonHost(String[] args) {
    this(args, new Arguments());
  }

  public XenonHost(String[] args, Arguments arguments) {
    this.arguments = arguments;
    this.args = args;
  }


  @Override
  public ServiceHost start() throws Throwable {
    super.initialize(args, arguments);
    super.start();
    startDefaultCoreServicesSynchronously();
    log.info("Started core services");
    super.startService(new RootNamespaceService());
    super.startService(new UiService());
    startSwaggerDescriptorService();
    log.info("Starting UI and RootNamespace services");
    if (dnsHostUri == null) {
      dnsService = Optional.empty();
    } else {
      dnsService = Optional.of(JaxRsServiceClient.newProxy(XenonDnsService.class, buildUri(dnsHostUri)));
      log.info("Using DNS service running @ {}", dnsHostUri);
    }
    return this;
  }


  public ServiceHost startFactoryServices(Map<VrbcServiceInfo, FactoryService> factoryServiceMap) {
    factoryServiceMap.forEach((serviceInfo, statelessService) -> {
      super.startService(Operation.createPost(buildUri(this, serviceInfo.serviceLink())), statelessService);
      registerWithDns(cloneWithAvailabilityYes(serviceInfo));
    });


    return this;
  }


  public ServiceHost startStatelessServices(Map<VrbcServiceInfo, StatelessService> statelessServiceMap) {
    statelessServiceMap.forEach((serviceInfo, statelessService) -> {
      super.startService(Operation.createPost(buildUri(this, serviceInfo.serviceLink())), statelessService);
      registerWithDns(serviceInfo);
    });

    return this;
  }

  public ServiceHost registerWithDns(VrbcServiceInfo serviceInfo) {
    return registerWithDns(serviceInfo, this.getUri());
  }


  public ServiceHost registerWithDns(VrbcServiceInfo serviceInfo, URI serviceHostUri) {
    dnsService.ifPresent(dns -> {
      DnsState state = toDnsState(serviceInfo,serviceHostUri);
      DnsState savedDns = dns.registerService(state);
      log.info("Registered service {} with URI {} with DNS @ {}", savedDns.serviceName, savedDns.serviceLink, dnsHostUri);
    });
    return this;
  }

  private void startSwaggerDescriptorService() {
    // Serve Swagger 2.0 compatible API description
    SwaggerDescriptorService swagger = new SwaggerDescriptorService();

    // Provide API metainfo
    Info apiInfo = new Info();
    apiInfo.setVersion("1.0.0");
    apiInfo.setTitle("Vrbc Xenon Host");
    swagger.setInfo(apiInfo);

    // Serve swagger on default uri
    super.startService(swagger);
  }

  public static HostBuilder newBuilder() {
    return new HostBuilder();
  }


  public static class HostBuilder {

    private Map<VrbcServiceInfo, FactoryService> factoryServices = new HashMap<>();
    private Map<VrbcServiceInfo, StatelessService> statelessServices = new HashMap<>();
    private Map<VrbcServiceInfo, String> dnsRegistry = new HashMap<>();
    private String[] cmdArgs = new String[0];
    private Arguments args;
    private String dnsHostUri;

    public HostBuilder withDns(String dnsHostUri) {
      this.dnsHostUri = dnsHostUri;
      return this;
    }

    public HostBuilder withService(VrbcServiceInfo info, FactoryService service) {
      factoryServices.put(info, service);
      return this;
    }

    public HostBuilder withService(VrbcServiceInfo info, StatelessService service) {
      statelessServices.put(info, service);
      return this;
    }

    public HostBuilder registerWithDns(VrbcServiceInfo info, String hostUri) {
      dnsRegistry.put(info, hostUri);
      return this;
    }

    public HostBuilder withArguments(String[] args) {
      this.cmdArgs = args;
      return this;
    }

    public HostBuilder withArguments(ServiceHost.Arguments args) {
      this.args = args;
      return this;
    }

    public XenonHost buildAndStart() throws Throwable {
      XenonHost host;
      if (args == null) {
        args = new Arguments();
      }
      if (cmdArgs == null) {
        cmdArgs = new String[0];
      }
      host = new XenonHost(cmdArgs, args);

      host.dnsHostUri = this.dnsHostUri;

      host.start();

      host.startFactoryServices(factoryServices);
      host.startStatelessServices(statelessServices);

      if (dnsHostUri == null && !dnsRegistry.isEmpty()) {
        log.warn("No dnsHostUri configured. Hence Can't register services with DNS");
      }

      dnsRegistry.forEach((info, s) -> host.registerWithDns(info, buildUri(s)));

      return host;
    }
  }


}
