package com.tt;

import com.tt.container.dao.UnderlyerDaoService;
import com.tt.ui.svc.ChartingService;
import com.tt.underlyer.svc.UnderlyerService;
import com.tt.xenon.common.ServiceInfo;
import com.tt.xenon.common.host.XenonHost;
import com.vmware.xenon.common.ServiceHost;

import static com.tt.CostServicesInfo.UI_SERVICE;
import static com.tt.CostServicesInfo.UNDERLYER_DAO;
import static com.tt.CostServicesInfo.UNDERLYER_SVC;

/**
 * Created by mageshwaranr on 9/28/2016.
 */
public class App {

  public static void main(String[] args) throws Throwable {
    XenonHost.newBuilder()
        .withArguments(new String[]{"--port=9090", "--bindAddress=0.0.0.0"})
        .withService(UI_SERVICE, new ChartingService())
        .withService(UNDERLYER_SVC, new UnderlyerService())
        .withService(UNDERLYER_DAO, new UnderlyerDaoService.UnderlyerDaoServiceFactory())
        .buildAndStart();
  }
}
