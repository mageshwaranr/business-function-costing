package com.tt;

import com.tt.xenon.common.ServiceInfo;

/**
 * Created by mageshwaranr on 9/28/2016.
 */
public enum CostServicesInfo implements ServiceInfo {

  UNDERLYER_DAO("UnderlyerDaoService", "/business/underlyer/persistence"),
  UNDERLYER_SVC("UnderlyerService", "/business/underlyer/svc"),
  CONTAINER_DISCOVERY_SVC("ContainerDiscoveryService", "/business/container/discovery"),
  CONTAINER_SVC("ContainerService", "/business/container/persistence"),
  SERVICE_LINEAGE_SVC("BusinessServiceLineage", "/business/lineage/query"),
  UI_SERVICE ("CostingUiService", "/business/ux/costing");

  private String name, link;

  CostServicesInfo(String name, String link) {
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
