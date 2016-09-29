package com.tt.container.dao;

import com.vmware.xenon.common.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tt.container.entity.Container;

import static com.vmware.xenon.common.Service.ServiceOption.*;

/**
 * Created by mageshwaranr on 9/28/2016.
 */
public class ContainerService extends StatefulService {

	private Logger log = LoggerFactory.getLogger(getClass());
    public ContainerService() {
		super(Container.class);
        super.toggleOption(REPLICATION, true);
        super.toggleOption(PERSISTENCE , true);
        super.toggleOption(OWNER_SELECTION, true);
        super.toggleOption(INSTRUMENTATION, true);
	}
    public static FactoryService createFactory() {
        return FactoryService.createIdempotent(ContainerService.class);
    }

    @Override
    public void handleMaintenance(Operation post) {
      super.handleMaintenance(post);
    }
}