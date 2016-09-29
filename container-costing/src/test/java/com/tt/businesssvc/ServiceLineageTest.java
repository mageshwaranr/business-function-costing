package com.tt.businesssvc;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.GraphQuery;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.tg.TinkerGraph;
import com.tinkerpop.blueprints.impls.tg.TinkerGraphFactory;
import com.tt.ui.dto.CostResponse;
import com.vmware.xenon.common.Utils;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.tt.businesssvc.ServiceLineage.verticesToCostResponse;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.summingDouble;
import static org.junit.Assert.*;

/**
 * Created by mageshwaranr on 9/29/2016.
 */
public class ServiceLineageTest {

  @Test
  public void testTinkerPop() {

    TinkerGraph graph = ServiceLineageGraphFactory.createTinkerGraph();

    Iterable<Vertex> vertices = graph.getVertices("kind", "Service");
    System.out.println(Utils.toJson(verticesToCostResponse(vertices)));
//        tinkerGraph.query()
//        .has()

    findLineageOfAFuntion(graph, "Supply Management");
  }

  public void findLineageOfAFuntion(TinkerGraph graph, String functionName) {

    Iterable<Vertex> label = graph.getVertices("label", functionName);
    Vertex match = null;
    for (Vertex v : label) {
      if ("BusinessFunction".equals(v.getProperty("kind"))) {
        match = v;
        break;
      }
    }

    Node root = new Node(match.getProperty("label"), match.getId().toString(), match.getProperty("cost"));
    recursive(root, match);
//    Iterable<Vertex> vertices = match.query().direction(Direction.OUT).vertices();
    System.out.println(Utils.toJson(root));

  }

  void recursive(Node node, Vertex v) {
    Iterator<Vertex> vertices = v.query().direction(Direction.OUT)
        .vertices().iterator();
    while (vertices.hasNext()) {
      Vertex next = vertices.next();
      Node nextNode = new Node(next.getProperty("label"), next.getId().toString(), next.getProperty("cost"));
      node.getChilds().add(nextNode);
      recursive(nextNode, next);
    }
  }


//  public void findServicesOfAFunction(TinkerGraph graph, String functionName) {
//
//    Iterable<Vertex> label = graph.getVertices("label", functionName);
//    Vertex match = null;
//    for (Vertex v : label) {
//      if ("BusinessFunction".equals(v.getProperty("kind"))) {
//        match = v;
//        break;
//      }
//    }
//    Iterable<Vertex> vertices = match.query().direction(Direction.OUT).vertices();
//
//    System.out.println(Utils.toJson(verticesToCostResponse(vertices)));
//  }


}