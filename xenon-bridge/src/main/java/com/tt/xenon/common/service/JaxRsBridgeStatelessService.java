package com.tt.xenon.common.service;

import com.tt.xenon.common.router.RequestRouterBuilder;
import com.vmware.xenon.common.OperationProcessingChain;
import com.vmware.xenon.common.RequestRouter;
import com.vmware.xenon.common.Service;
import com.vmware.xenon.common.StatelessService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by mageshwaranr on 9/16/2016.
 */
public class JaxRsBridgeStatelessService extends StatelessService {

  public JaxRsBridgeStatelessService(){
    super.toggleOption(Service.ServiceOption.URI_NAMESPACE_OWNER, true);
  }

  @Override
  public OperationProcessingChain getOperationProcessingChain() {
    if (super.getOperationProcessingChain() != null) {
      return super.getOperationProcessingChain();
    }
    final OperationProcessingChain opProcessingChain = new OperationProcessingChain(this);
    RequestRouter requestRouter = RequestRouterBuilder.parseJaxRsAnnotations(this);
    opProcessingChain.add(requestRouter);
    setOperationProcessingChain(opProcessingChain);
    return opProcessingChain;
  }

}
