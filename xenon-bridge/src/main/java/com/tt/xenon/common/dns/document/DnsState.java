package com.tt.xenon.common.dns.document;

import com.vmware.xenon.common.ServiceDocument;

import java.net.URI;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Created by mageshwaranr on 8/26/2016.
 */
public class DnsState extends ServiceDocument {

  public enum ServiceStatus {
    AVAILABLE, UNKNOWN, UNAVAILABLE
  }

  public String serviceLink;
  public String serviceName;
  public Set<String> tags;
  public String healthCheckLink;
  public Long healthCheckIntervalSeconds = TimeUnit.SECONDS.toMillis(30);
  public String hostName;
  public Set<URI> nodeReferences;
  public ServiceStatus serviceStatus;

}
