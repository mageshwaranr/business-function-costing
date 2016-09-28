package com.tt.container.entity;

import com.vmware.xenon.common.ServiceDocument;
import com.vmware.xenon.common.ServiceDocumentDescription;

/**
 * Created by mageshwaranr on 9/28/2016.
 */
public class Underlyer extends ServiceDocument {


  @UsageOption(option = ServiceDocumentDescription.PropertyUsageOption.SINGLE_ASSIGNMENT)
  public String name, id;

  public double cost;


}
