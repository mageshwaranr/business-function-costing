package com.tt.container.dao;

import com.tt.container.entity.Container;
import com.vmware.xenon.common.StatefulService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by mageshwaranr on 9/28/2016.
 */
public class ContainerService extends StatefulService {

  private Logger log = LoggerFactory.getLogger(getClass());

  public ContainerService() {
    super(Container.class);
  }
}
