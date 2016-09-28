package com.tt.ui.svc;

import com.tt.ui.dto.CostResponse;
import com.tt.xenon.common.annotations.OperationBody;
import com.tt.xenon.common.annotations.PATCH;
import com.tt.xenon.common.router.RequestRouterBuilder;
import com.vmware.xenon.common.*;

import javax.ws.rs.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.vmware.xenon.common.Utils.toJson;

/**
 * Created by mageshwaranr on 9/28/2016.
 */
public class ChartingService extends StatelessService {


  public static final String SELF_LINK = "/business/function/costing";

  public ChartingService() {
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


//  @Override
//  public OperationProcessingChain getOperationProcessingChain() {
//////    if (acctService == null) {
//////      acctService = JaxRsServiceClient.newBuilder().withResourceInterface(AsyncAccountServiceContract.class).withBaseUri(this.getHost().getUri()).build();
//////    }
////    return super.getOperationProcessingChain();
//    if (super.getOperationProcessingChain() != null) {
//      return super.getOperationProcessingChain();
//    }
//    final OperationProcessingChain opProcessingChain = new OperationProcessingChain(this);
//    RequestRouter requestRouter = RequestRouterBuilder.parseJaxRsAnnotations(this);
//    opProcessingChain.add(requestRouter);
//    setOperationProcessingChain(opProcessingChain);
//    return opProcessingChain;
//  }

  @Path("/consolidated")
  @GET
  public CostResponse fetchCostResponse(){
    CostResponse overAll = new CostResponse();
    overAll.setName("Over All Cost");
    List<CostResponse> childs = mockedData();
    overAll.setSize(childs.stream().mapToDouble(CostResponse::getSize).sum());
    overAll.setChildren(childs);
    return overAll;
  }


  private List<CostResponse> mockedData() {
    List<CostResponse> responses = new ArrayList<>();

    CostResponse orderMgmt = new CostResponse("Demands Management", 5000);
    responses.add(orderMgmt);

    CostResponse catalogSvc = new CostResponse("Catalog Service", 1750);
    CostResponse customerSvc = new CostResponse("Customer Service", 250);
    CostResponse orderSvc = new CostResponse("Order Service", 3000);
    orderMgmt.addChild(catalogSvc);
    orderMgmt.addChild(customerSvc);
    orderMgmt.addChild(orderSvc);
    addInfrastructureSplitUp(catalogSvc, 30);
    addInfrastructureSplitUp(customerSvc, 50);
    addInfrastructureSplitUp(orderSvc, 40);


    CostResponse supplyMgmt = new CostResponse("Supply Management", 4500);
    responses.add(supplyMgmt);

    catalogSvc = new CostResponse("Catalog Service", 250);
    CostResponse warehouseSvc = new CostResponse("Warehouse Service", 2000);
    CostResponse logisticsSvc = new CostResponse("Logistics Service", 2250);
    supplyMgmt.addChild(catalogSvc);
    supplyMgmt.addChild(warehouseSvc);
    supplyMgmt.addChild(logisticsSvc);
    addInfrastructureSplitUp(catalogSvc, 30);
    addInfrastructureSplitUp(warehouseSvc, 50);
    addInfrastructureSplitUp(logisticsSvc, 40);

    return responses;
  }

  private void addInfrastructureSplitUp(CostResponse resp, int percentFactor){
    CostResponse loadBalancer = new CostResponse("Load Balancer", resp.getSize() * percentFactor * 0.15 * 0.01);
    CostResponse discovery = new CostResponse("Discovery Service", resp.getSize() * percentFactor * 0.25 * 0.01);
    CostResponse metrics = new CostResponse("Monitoring Service", resp.getSize() * percentFactor * 0.6 * 0.01);
    resp.addChild(loadBalancer);
    resp.addChild(loadBalancer);
    resp.addChild(metrics);
    resp.addChild(discovery);
  }



}
