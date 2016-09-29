package com.tt.container.entity;

import com.vmware.xenon.common.ServiceDocument;
import com.vmware.xenon.common.ServiceDocumentDescription;

/**
 * Created by mageshwaranr on 9/28/2016.
 */
public class Container extends ServiceDocument{

  @UsageOption(option = ServiceDocumentDescription.PropertyUsageOption.SINGLE_ASSIGNMENT)
  public String name, image, ip;

  @UsageOption(option = ServiceDocumentDescription.PropertyUsageOption.AUTO_MERGE_IF_NOT_NULL)
  public Double cost;

  @UsageOption(option = ServiceDocumentDescription.PropertyUsageOption.LINK)
  public String underlyer;
 
}
