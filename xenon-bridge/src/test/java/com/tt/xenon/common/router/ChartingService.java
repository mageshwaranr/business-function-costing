package com.tt.xenon.common.router;

import com.vmware.xenon.common.OperationProcessingChain;
import com.vmware.xenon.common.RequestRouter;
import com.vmware.xenon.common.StatelessService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mageshwaranr on 9/28/2016.
 */
public class ChartingService extends StatelessService {


  public static final String SELF_LINK = "/business/function/costing/ui";

  private Logger log = LoggerFactory.getLogger(getClass());

//  private AsyncAccountServiceContract acctService;

  public ChartingService() {
    super.toggleOption(ServiceOption.URI_NAMESPACE_OWNER, true);
  }

  @Override
  public OperationProcessingChain getOperationProcessingChain() {
////    if (acctService == null) {
////      acctService = JaxRsServiceClient.newBuilder().withResourceInterface(AsyncAccountServiceContract.class).withBaseUri(this.getHost().getUri()).build();
////    }
//    return super.getOperationProcessingChain();
    if (super.getOperationProcessingChain() != null) {
      return super.getOperationProcessingChain();
    }
    final OperationProcessingChain opProcessingChain = new OperationProcessingChain(this);
    RequestRouter requestRouter = RequestRouterBuilder.parseJaxRsAnnotations(this);
    opProcessingChain.add(requestRouter);
    setOperationProcessingChain(opProcessingChain);
    return opProcessingChain;
  }

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

  public class CostResponse {

    private String name;

    // this refers to cost
    private double size;

    private List<CostResponse> children = new ArrayList<>();

    public CostResponse() {
    }

    public CostResponse(String name, double size) {
      this.name = name;
      this.size = size;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public double getSize() {
      return size;
    }

    public void setSize(double size) {
      this.size = size;
    }

    public List<CostResponse> getChildren() {
      return children;
    }

    public void setChildren(List<CostResponse> children) {
      this.children = children;
    }

    public void addChild(CostResponse childCost){
      this.children.add(childCost);
    }
  }



}
