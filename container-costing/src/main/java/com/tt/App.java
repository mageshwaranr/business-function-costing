package com.tt;

import com.tt.businesssvc.ServiceLineage;
import com.tt.container.dao.ContainerDiscoveryService;
import com.tt.container.dao.UnderlyerDaoService;
import com.tt.ui.svc.ChartingService;
import com.tt.underlyer.svc.UnderlyerService;
import com.tt.xenon.common.ServiceInfo;
import com.tt.xenon.common.host.XenonHost;
import com.vmware.xenon.common.ServiceHost;

import static com.tt.CostServicesInfo.*;

/**
 * Created by mageshwaranr on 9/28/2016.
 */
public class App {

  public static void main(String[] args) throws Throwable {
    XenonHost.newBuilder()
        .withArguments(new String[]{"--port=9090", "--bindAddress=0.0.0.0"})
        .withService(UI_SERVICE, new ChartingService())
        .withService(UNDERLYER_SVC, new UnderlyerService())
        .withService(CONTAINER_DISCOVERY_SVC, new ContainerDiscoveryService())
        .withService(SERVICE_LINEAGE_SVC, new ServiceLineage())
        .withService(UNDERLYER_DAO, new UnderlyerDaoService.UnderlyerDaoServiceFactory())
        .buildAndStart();
  }
}
