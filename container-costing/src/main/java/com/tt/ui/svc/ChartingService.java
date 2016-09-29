package com.tt.ui.svc;

import com.tt.businesssvc.Node;
import com.tt.businesssvc.ServiceLineageContract;
import com.tt.ui.dto.CostResponse;
import com.tt.xenon.common.client.JaxRsServiceClient;
import com.tt.xenon.common.service.JaxRsBridgeStatelessService;
import com.vmware.xenon.common.*;

import javax.ws.rs.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.tt.CostServicesInfo.SERVICE_LINEAGE_SVC;
import static com.vmware.xenon.common.Utils.toJson;
import static java.util.stream.Collectors.toMap;

/**
 * Created by mageshwaranr on 9/28/2016.
 */
public class ChartingService extends JaxRsBridgeStatelessService {

  private ServiceLineageContract lineageSvc;

  public ChartingService() {
    super.toggleOption(Service.ServiceOption.URI_NAMESPACE_OWNER, true);
  }

  @Override
  public OperationProcessingChain getOperationProcessingChain() {
    if (lineageSvc == null) {
      lineageSvc = JaxRsServiceClient.newBuilder()
          .withHost(this.getHost())
          .withServiceInfo(SERVICE_LINEAGE_SVC)
          .withResourceInterface(ServiceLineageContract.class)
          .build();
    }
    return super.getOperationProcessingChain();
  }

  @Path("/lineage/all")
  @GET
  public CostResponse fetchLineage() {
    CostResponse overAll = new CostResponse();
    overAll.setName("ALL");
    List<CostResponse> childs = getLineage();
    overAll.setSize(childs.stream().mapToDouble(CostResponse::getSize).sum());
    overAll.setChildren(childs);
    return overAll;
  }

  private List<CostResponse> getLineage() {
    return lineageSvc.findAllFunctions().stream()
        .map( node -> {
          Node root = lineageSvc.findLineageOfAFunction(node.getLabel());
          CostResponse resp = new CostResponse(root.getLabel(), root.getCost());
          populateChilds(root,resp);
          return resp;
        }).collect(Collectors.toList());
  }

  void populateChilds(Node node, CostResponse response){
    node.getChilds().forEach( child -> {
      CostResponse childCost = new CostResponse(child.getLabel(), child.getCost());
      response.addChild(childCost);
      populateChilds(child,childCost);
    });
  }
}
