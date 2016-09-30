package com.tt.businesssvc;

import com.google.common.collect.Iterables;

import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.tg.TinkerGraph;
import com.tt.businesssvc.zipkin.Dependency;
import com.tt.businesssvc.zipkin.ZipkinQueryService;
import com.tt.xenon.common.client.JaxRsServiceClient;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by mageshwaranr on 9/29/2016.
 */
public class ServiceLineageGraphFactoryFromZipkin {

    public static TinkerGraph createTinkerGraph() {
        com.tt.businesssvc.zipkin.ZipkinQueryService svc;

        svc = JaxRsServiceClient.newBuilder()
                .withBaseUri("http://10.112.80.92:9411")
                .withResourceInterface(ZipkinQueryService.class)
                .build();

        TinkerGraph graph = new TinkerGraph();
        List<Dependency> dependency = svc.findDependency(System.currentTimeMillis());

        Set<String> businessFunctions = findBusinessFunctions(dependency);

        dependency.forEach(name -> {

            if (businessFunctions.contains(name.getParent())) {

                Vertex parentVertex = findVertex(graph, name.getParent());
                ;
                if (parentVertex == null) {
                    parentVertex = newBusinessFunction(graph, name.getParent(), name.getCallCount());
                }
                Vertex childVertex = findVertex(graph, name.getChild());
                if (childVertex == null) {
                    childVertex = newService(graph, parentVertex, name.getChild(), name.getCallCount());
                }
                linkFunctionToSvc(graph, parentVertex, childVertex, name.getCallCount());
            } else {
                Vertex parentVertex = findVertex(graph, name.getParent());
                Vertex childVertex = findVertex(graph, name.getChild());
                if (childVertex == null) {
                    childVertex = newService(graph, parentVertex, name.getChild(), name.getCallCount());
                }
                linkSvcToSvc(graph, parentVertex, childVertex, name.getCallCount());
            }

        });
//    linkSvcToSvc(graph, logisticsSvc, newService(graph, logisticsSvc, "Load Balancer Service", .17));
//    linkSvcToSvc(graph, logisticsSvc, newService(graph, logisticsSvc, "Monitoring Service", .21));
//    linkSvcToSvc(graph, logisticsSvc, newService(graph, logisticsSvc, "Self", .62));

        return graph;
    }

    private static Vertex findVertex(Graph graph, String name) {

        Iterable<Vertex> label = graph.getVertices("label", name);

        if(Iterables.size(label) > 0) {
            return label.iterator().next();
        }
        return null;
    }

    private static Set<String> findBusinessFunctions(List<Dependency> dependency) {

        Set<String> parent = new HashSet<>();
        Set<String> child = new HashSet<>();
        for (Dependency dependency1 : dependency) {
            parent.add(dependency1.getParent());
            child.add(dependency1.getChild());
        }

        for (String input : child) {
            parent.remove(input);
        }
        return parent;
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

    static void linkFunctionToSvc(Graph graph, Vertex fn, Vertex svc, double count) {
        Edge uses = graph.addEdge(fn.getId() + "->" + svc.getId(), fn, svc, "dependsOn");
        uses.setProperty("kind", "FunctionToSvc");
        uses.setProperty("from", fn.getId());
        uses.setProperty("to", svc.getId());
        uses.setProperty("relation", "dependsOn");
        uses.setProperty("count", count);
        Iterable<Edge> edges = graph.getEdges("to", svc.getId());
        if (Iterables.size(edges) > 1) {
            int total = Iterables.size(edges);
            double sum =  0;
            for (Iterator iterator = edges.iterator(); iterator.hasNext();) {
                Edge edge = (Edge) iterator.next();
                sum+=(double) edge.getProperty("count");
            }

            for (Iterator iterator = edges.iterator(); iterator.hasNext();) {
                Edge edge = (Edge) iterator.next();
                edge.removeProperty("factor");
                edge.setProperty("factor", ((double) edge.getProperty("count") / sum));
            }

        } else {
            uses.setProperty("factor", 1);
        }
    }


    static void linkSvcToSvc(Graph graph, Vertex fromSvc, Vertex toSvc, double count) {
        Edge uses = graph.addEdge(fromSvc.getId() + "->" + toSvc.getId(), fromSvc, toSvc, "uses");
        uses.setProperty("kind", "SvcToSvc");
        uses.setProperty("from", fromSvc.getId());
        uses.setProperty("to", toSvc.getId());
        uses.setProperty("relation", "uses");
        uses.setProperty("count", count);
        Iterable<Edge> edges = graph.getEdges("to", toSvc.getId());
        if (Iterables.size(edges) > 1) {
            int total = Iterables.size(edges);
            double sum =  0;
            for (Iterator iterator = edges.iterator(); iterator.hasNext();) {
                Edge edge = (Edge) iterator.next();
                sum+=(double) edge.getProperty("count");
            }

            for (Iterator iterator = edges.iterator(); iterator.hasNext();) {
                Edge edge = (Edge) iterator.next();
                edge.removeProperty("factor");
                edge.setProperty("factor", ((double) edge.getProperty("count") / sum));
            }

        } else {
            uses.setProperty("factor", 1);
        }
    }


}
