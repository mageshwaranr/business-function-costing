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

import static com.vmware.xenon.common.Utils.toJson;
import static java.util.stream.Collectors.toMap;

/**
 * Created by mageshwaranr on 9/28/2016.
 */
public class ChartingService extends JaxRsBridgeStatelessService {


  public static final String SELF_LINK = "/business/function/costing";

  private ServiceLineageContract lineageSvc;

  public ChartingService() {
    super.toggleOption(Service.ServiceOption.URI_NAMESPACE_OWNER, true);
  }

  @Override
  public OperationProcessingChain getOperationProcessingChain() {

    if (lineageSvc == null) {
      lineageSvc = JaxRsServiceClient.newBuilder()
          .withHost(this.getHost())
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


//
//  @Path("/consolidated")
//  @GET
//  public CostResponse fetchCostResponse() {
//    CostResponse overAll = new CostResponse();
//    overAll.setName("Over All Cost");
//    List<CostResponse> childs = getLineage();
//    overAll.setSize(childs.stream().mapToDouble(CostResponse::getSize).sum());
//    overAll.setChildren(childs);
//    return overAll;
//  }
//
//
//  private List<CostResponse> mockedData() {
//    List<CostResponse> responses = new ArrayList<>();
//
//    CostResponse orderMgmt = new CostResponse("Demands Management", 5000);
//    responses.add(orderMgmt);
//
//    CostResponse catalogSvc = new CostResponse("Catalog Service", 1750);
//    CostResponse customerSvc = new CostResponse("Customer Service", 250);
//    CostResponse orderSvc = new CostResponse("Order Service", 3000);
//    orderMgmt.addChild(catalogSvc);
//    orderMgmt.addChild(customerSvc);
//    orderMgmt.addChild(orderSvc);
//    addInfrastructureSplitUp(catalogSvc, 30);
//    addInfrastructureSplitUp(customerSvc, 50);
//    addInfrastructureSplitUp(orderSvc, 40);
//
//
//    CostResponse supplyMgmt = new CostResponse("Supply Management", 4500);
//    responses.add(supplyMgmt);
//
//    catalogSvc = new CostResponse("Catalog Service", 250);
//    CostResponse warehouseSvc = new CostResponse("Warehouse Service", 2000);
//    CostResponse logisticsSvc = new CostResponse("Logistics Service", 2250);
//    supplyMgmt.addChild(catalogSvc);
//    supplyMgmt.addChild(warehouseSvc);
//    supplyMgmt.addChild(logisticsSvc);
//    addInfrastructureSplitUp(catalogSvc, 30);
//    addInfrastructureSplitUp(warehouseSvc, 50);
//    addInfrastructureSplitUp(logisticsSvc, 40);
//
//    return responses;
//  }
//
//  private void addInfrastructureSplitUp(CostResponse resp, int percentFactor) {
//    CostResponse loadBalancer = new CostResponse("Load Balancer", resp.getSize() * percentFactor * 0.15 * 0.01);
//    CostResponse discovery = new CostResponse("Discovery Service", resp.getSize() * percentFactor * 0.25 * 0.01);
//    CostResponse metrics = new CostResponse("Monitoring Service", resp.getSize() * percentFactor * 0.6 * 0.01);
//    resp.addChild(loadBalancer);
//    resp.addChild(loadBalancer);
//    resp.addChild(metrics);
//    resp.addChild(discovery);
//  }


}
