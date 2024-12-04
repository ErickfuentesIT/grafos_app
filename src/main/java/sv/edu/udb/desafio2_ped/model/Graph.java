package sv.edu.udb.desafio2_ped.model;

import java.util.*;

public class Graph {
    private Map<String, List<Edge>> adjacencyList = new HashMap<>();
    private String startNode; // Nodo origen

    // Método para agregar un vértice
    public void addVertex(String vertex) {
        adjacencyList.putIfAbsent(vertex, new ArrayList<>());
    }

    // Método para agregar una arista
    public void addEdge(String source, String destination, double distance) {
        // Asegúrate de que ambos vértices existen
        adjacencyList.putIfAbsent(source, new ArrayList<>());
        adjacencyList.putIfAbsent(destination, new ArrayList<>());

        // Agregar la arista
        Edge edge = new Edge(source, destination, distance);
        adjacencyList.get(source).add(edge);

        // Agregar la arista en la dirección opuesta
        Edge reverseEdge = new Edge(destination, source, distance);
        adjacencyList.get(destination).add(reverseEdge);
    }

    // Obtener el mapa de adyacencia
    public Map<String, List<Edge>> getAdjacencyList() {
        return adjacencyList;
    }

    // Obtener las aristas de un vértice
    public List<Edge> getEdges(String vertex) {
        return adjacencyList.getOrDefault(vertex, new ArrayList<>());
    }

    // Obtener todos los vértices
    public Set<String> getVertices() {
        return adjacencyList.keySet();
    }

    // Eliminar un vértice y sus aristas asociadas
    public void removeVertex(String vertex) {
        // Eliminar el vértice del mapa (aristas salientes)
        adjacencyList.remove(vertex);

        // Eliminar las aristas que tienen al vértice como destino
        for (String key : adjacencyList.keySet()) {
            adjacencyList.get(key).removeIf(edge -> edge.getDestination().equals(vertex));
        }

        // Si el nodo eliminado es el nodo origen, resetear el nodo origen
        if (vertex.equals(startNode)) {
            startNode = null;
        }
    }

    // Eliminar todas las aristas salientes de un vértice
    public void removeEdges(String vertex) {
        if (adjacencyList.containsKey(vertex)) {
            adjacencyList.get(vertex).clear();
        }
    }

    // Establecer el nodo origen
    public void setStartNode(String vertex) {
        if (adjacencyList.containsKey(vertex)) {
            this.startNode = vertex;
        } else {
            throw new IllegalArgumentException("El vértice especificado no existe en el grafo.");
        }
    }

    // Obtener el nodo origen
    public String getStartNode() {
        return this.startNode;
    }
}