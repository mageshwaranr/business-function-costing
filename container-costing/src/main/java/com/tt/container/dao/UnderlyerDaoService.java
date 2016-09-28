package com.tt.container.dao;

import com.tt.container.entity.Underlyer;
import com.vmware.xenon.common.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.vmware.xenon.common.Service.ServiceOption.*;

/**
 * Created by mageshwaranr on 9/28/2016.
 */
public class UnderlyerDaoService extends StatefulService {


  public static final String SELF_LINK = UnderlyerDaoServiceFactory.SELF_LINK;

  public UnderlyerDaoService() {
    super(Underlyer.class);
    super.toggleOption(REPLICATION, true);
    super.toggleOption(PERSISTENCE , true);
    super.toggleOption(OWNER_SELECTION, true);
    super.toggleOption(INSTRUMENTATION, true);
  }

  public static class UnderlyerDaoServiceFactory extends FactoryService {

    public static final String SELF_LINK =  "/business/function/dao/underlyer";

    private final Logger log = LoggerFactory.getLogger(getClass());

    /**
     * Creates a default instance of a factory service that can create and start instances
     * of the supplied service type
     */
    public UnderlyerDaoServiceFactory() {
      super(Underlyer.class);
      super.toggleOption(IDEMPOTENT_POST, true);
      log.info("IDEMPOTENT_POST true for FactoryService");
    }

    @Override
    public Service createServiceInstance() throws Throwable {
      return new UnderlyerDaoService();
    }
  }
}
