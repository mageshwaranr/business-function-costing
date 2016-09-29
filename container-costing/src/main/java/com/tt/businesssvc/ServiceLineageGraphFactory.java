package com.tt.businesssvc;

import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.tg.TinkerGraph;

/**
 * Created by mageshwaranr on 9/29/2016.
 */
public class ServiceLineageGraphFactory {

  public ServiceLineageGraphFactory() {
  }

  public static TinkerGraph createTinkerGraph() {
    TinkerGraph graph = new TinkerGraph();

    Vertex demandsFn = newBusinessFunction(graph, "Demands Management", 11000);
    Vertex catalogSvc = newService(graph, demandsFn, "Catalog Service", .45);
    Vertex customSvc = newService(graph, demandsFn, "Customer Service", .22);
    Vertex orderSvc = newService(graph, demandsFn, "Order Service", .33);

    linkFunctionToSvc(graph, demandsFn, catalogSvc);
    linkFunctionToSvc(graph, demandsFn, customSvc);
    linkFunctionToSvc(graph, demandsFn, orderSvc);

//    linkSvcToSvc(graph, catalogSvc, newService(graph, catalogSvc, "Load Balancer Service", .35));
//    linkSvcToSvc(graph, catalogSvc, newService(graph, catalogSvc, "Discovery Service", .15));
//    linkSvcToSvc(graph, catalogSvc, newService(graph, catalogSvc, "Monitoring Service", .15));
//    linkSvcToSvc(graph, catalogSvc, newService(graph, catalogSvc, "Self", .35));
//    linkSvcToSvc(graph, customSvc, newService(graph, customSvc, "Load Balancer Service", .1));
//    linkSvcToSvc(graph, customSvc, newService(graph, customSvc, "Monitoring Service", .15));
//    linkSvcToSvc(graph, customSvc, newService(graph, customSvc, "Self", .84));
    linkSvcToSvc(graph, orderSvc, newService(graph, orderSvc, "Catalog Service", .17));
    linkSvcToSvc(graph, orderSvc, newService(graph, orderSvc, "Customer Service", .13));
//    linkSvcToSvc(graph, orderSvc, newService(graph, orderSvc, "Monitoring Service", .21));
    linkSvcToSvc(graph, orderSvc, newService(graph, orderSvc, "Self", .7));


    Vertex supplyFn = newBusinessFunction(graph, "Supply Chain", 14000);
    Vertex warehouseSvc = newService(graph, supplyFn, "Warehouse Service", .53);
    Vertex logisticsSvc = newService(graph, supplyFn, "Logistics Service", .47);

    linkFunctionToSvc(graph, supplyFn, warehouseSvc);
    linkFunctionToSvc(graph, supplyFn, logisticsSvc);

    linkSvcToSvc(graph, warehouseSvc, newService(graph, warehouseSvc, "Order Service", .15));
    linkSvcToSvc(graph, warehouseSvc, newService(graph, warehouseSvc, "Self", .85));

//    linkSvcToSvc(graph, logisticsSvc, newService(graph, logisticsSvc, "Load Balancer Service", .17));
//    linkSvcToSvc(graph, logisticsSvc, newService(graph, logisticsSvc, "Monitoring Service", .21));
//    linkSvcToSvc(graph, logisticsSvc, newService(graph, logisticsSvc, "Self", .62));

    return graph;
  }

  static Vertex newBusinessFunction(Graph graph, String functionName, double cost) {
    Vertex vertex = graph.addVertex(functionName);
    vertex.setProperty("kind", "BusinessFunction");
    vertex.setProperty("label", functionName);
    vertex.setProperty("cost", cost);
    return vertex;
  }

  static Vertex newService(Graph graph, Vertex fn, String name, double factor) {
    Vertex vertex = graph.addVertex(fn.getId() + ":" + name);
    vertex.setProperty("kind", "Service");
    vertex.setProperty("label", name);
    vertex.setProperty("factor", factor);
    vertex.setProperty("cost", Double.parseDouble(fn.getProperty("cost").toString()) * factor);
    return vertex;
  }

  static void linkFunctionToSvc(Graph graph, Vertex fn, Vertex svc) {
    Edge uses = graph.addEdge(fn.getId() + "->" + svc.getId(), fn, svc, "dependsOn");
    uses.setProperty("kind", "FunctionToSvc");
    uses.setProperty("from", fn.getId());
    uses.setProperty("to", svc.getId());
    uses.setProperty("factor", svc.getProperty("factor"));
    uses.setProperty("relation", "dependsOn");
  }


  static void linkSvcToSvc(Graph graph, Vertex fromSvc, Vertex toSvc) {
    Edge uses = graph.addEdge(fromSvc.getId() + "->" + toSvc.getId(), fromSvc, toSvc, "uses");
    uses.setProperty("kind", "SvcToSvc");
    uses.setProperty("from", fromSvc.getId());
    uses.setProperty("to", toSvc.getId());
    uses.setProperty("factor", fromSvc.getProperty("factor"));
    uses.setProperty("relation", "uses");
  }


}
