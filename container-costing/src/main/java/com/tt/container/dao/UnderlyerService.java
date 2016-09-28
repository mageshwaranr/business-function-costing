package com.tt.container.dao;

import com.tt.container.entity.Underlyer;
import com.vmware.xenon.common.StatefulService;

/**
 * Created by mageshwaranr on 9/28/2016.
 */
public class UnderlyerService extends StatefulService {

  public UnderlyerService() {
    super(Underlyer.class);
  }
}
