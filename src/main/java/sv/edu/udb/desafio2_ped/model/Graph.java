package sv.edu.udb.desafio2_ped.model;

import java.util.*;

public class Graph {
    private Map<String, List<Edge>> adjacencyList = new HashMap<>();

    public void addVertex(String vertex) {
        adjacencyList.putIfAbsent(vertex, new ArrayList<>());
    }

    public void addEdge(String source, String destination, double distance) {
        Edge edge = new Edge(source, destination, distance);
        adjacencyList.get(source).add(edge);
    }

    public Map<String, List<Edge>> getAdjacencyList() {
        return adjacencyList;
    }

    public List<Edge> getEdges(String vertex) {
        return adjacencyList.getOrDefault(vertex, new ArrayList<>());
    }

    public Set<String> getVertices() {
        return adjacencyList.keySet();
    }
}