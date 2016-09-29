package com.tt.businesssvc;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;
import com.tt.ui.dto.CostResponse;
import com.tt.xenon.common.service.JaxRsBridgeStatelessService;
import com.vmware.xenon.common.OperationProcessingChain;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.summingDouble;

/**
 * Created by mageshwaranr on 9/29/2016.
 */
public class ServiceLineage extends JaxRsBridgeStatelessService {

  public static final String SELF_LINK = "/business/function/query";


  private Graph graph;

  @Override
  public OperationProcessingChain getOperationProcessingChain() {
    if (graph == null) {
      graph = ServiceLineageGraphFactory.createTinkerGraph();
    }
    return super.getOperationProcessingChain();
  }

  @Path("/all")
  @GET
  public List<Node> findAllFunctions() {
    Iterable<Vertex> vertices = graph.getVertices("kind", "BusinessFunction");
    List<Node> responses = new ArrayList<>();
    vertices.forEach(vertex -> {
      responses.add(new Node(vertex.getProperty("label"), vertex.getId().toString(), vertex.getProperty("cost")));
    });
    return responses;
  }

  @Path("/services/all")
  @GET
  public List<Node> findAllServices() {
    Iterable<Vertex> vertices = graph.getVertices("kind", "Service");
    return verticesToNode(vertices);
  }

  static List<Node> verticesToNode(Iterable<Vertex> vertices) {
    List<Node> result = new ArrayList<>();
    vertices.forEach(vertex -> result.add(new Node(vertex.getProperty("label"), vertex.getId().toString(), vertex.getProperty("cost"))));
    return   result.stream().collect(groupingBy(Node::getLabel, summingDouble(Node::getCost)))
        .entrySet().stream().map(kv -> new Node(kv.getKey(), null, kv.getValue()))
        .collect(Collectors.toList());
  }

  static List<CostResponse> verticesToCostResponse(Iterable<Vertex> vertices) {
    List<CostResponse> responses = new ArrayList<>();
    vertices.forEach(vertex -> responses.add(new CostResponse(vertex.getProperty("label"), vertex.getProperty("cost"))));
    return responses.stream()
        .collect(groupingBy(CostResponse::getName, summingDouble(CostResponse::getSize)))
        .entrySet().stream().map(kv -> new CostResponse(kv.getKey(), kv.getValue()))
        .collect(Collectors.toList());
  }

  @Path("/lineage/function/{functionName}")
  @GET
  public Node findLineageOfAFunction(@PathParam("functionName") String functionName) {
    Iterable<Vertex> label = graph.getVertices("label", functionName);
    Vertex match = null;
    Node root = null;
    for (Vertex v : label) {
      if ("BusinessFunction".equals(v.getProperty("kind"))) {
        match = v;
        break;
      }
    }
    if(match != null) {
      root = new Node(match.getProperty("label"), match.getId().toString(), match.getProperty("cost"));
      buildChildRecursively(root,match);
    }
    return root;
  }

  void buildChildRecursively(Node node, Vertex v) {
    for (Vertex next : v.query().direction(Direction.OUT).vertices()) {
      Node nextNode = new Node(next.getProperty("label"), next.getId().toString(), next.getProperty("cost"));
      node.getChilds().add(nextNode);
      buildChildRecursively(nextNode, next);
    }
  }

}
