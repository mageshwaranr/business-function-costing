package com.tt.xenon.common.dns;

import com.tt.xenon.common.VrbcServiceInfo;

/**
 * Created by mageshwaranr on 8/31/2016.
 */
public enum DnsServiceInfo implements VrbcServiceInfo {

  DNS("VrbcDNSService", "/core/dns/service-records"),
  DNS_QUERY("VrbcDNSQueryService", "/core/dns/service-records/query");

  private String name, link;

  DnsServiceInfo(String name, String link) {
    this.name = name;
    this.link = link;
  }

  @Override
  public String serviceLink() {
    return link;
  }

  @Override
  public String serviceName() {
    return name;
  }

}
