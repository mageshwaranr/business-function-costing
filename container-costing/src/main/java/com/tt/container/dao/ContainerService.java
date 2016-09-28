package com.tt.container.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.spotify.docker.client.messages.Container;
import com.vmware.xenon.common.FactoryService;
import com.vmware.xenon.common.Service;
import com.vmware.xenon.common.ServiceDocument;
import com.vmware.xenon.common.StatefulService;
import com.vmware.xenon.services.common.ServiceUriPaths;

/**
 * Created by mageshwaranr on 9/28/2016.
 */
public class ContainerService extends StatefulService {
	
	private Logger log = LoggerFactory.getLogger(getClass());
	
    public ContainerService(Class<? extends ServiceDocument> stateType) {
		super(stateType);
	}

    public static final String FACTORY_LINK = ServiceUriPaths.SAMPLES + "/container-service";
  
    public static Service createFactory() {
        return FactoryService.create(ContainerService.class);
    }
  
    public static class ContainerServiceState extends ServiceDocument {
        public double balance;
    }
    
    
}