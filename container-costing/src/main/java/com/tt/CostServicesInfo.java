package com.tt;

import com.tt.businesssvc.ServiceLineage;
import com.tt.container.dao.ContainerDiscoveryService;
import com.tt.container.dao.UnderlyerDaoService;
import com.tt.ui.svc.ChartingService;
import com.tt.underlyer.svc.UnderlyerService;
import com.tt.xenon.common.ServiceInfo;

/**
 * Created by mageshwaranr on 9/28/2016.
 */
public enum CostServicesInfo implements ServiceInfo {

  //  ACCOUNT("AccountService", "vrbc/demo/account"),
//  ACCOUNT_QUERY_SERVICE("ACCOUNT_QUERY_SERVICE", "/vrbc/demo/account/query/name"),
  UNDERLYER_DAO("UnderlyerDaoService", UnderlyerDaoService.SELF_LINK),
  UNDERLYER_SVC("UnderlyerService", UnderlyerService.SELF_LINK),
  CONTAINER_DISCOVERY_SVC("ContainerDiscoveryService", ContainerDiscoveryService.SELF_LINK),
  SERVICE_LINEAGE_SVC("BusinessServiceLineage", ServiceLineage.SELF_LINK),
  UI_SERVICE ("CostingUiService", ChartingService.SELF_LINK);

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
