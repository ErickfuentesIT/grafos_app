package sv.edu.udb.desafio2_ped.model;

import java.util.*;

public class GraphAlgorithms {

    // Recorrido en Anchura (BFS)
    public List<String> breadthFirstSearch(Graph graph, String startVertex) {
        List<String> result = new ArrayList<>();
        Queue<String> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>();

        queue.add(startVertex);
        visited.add(startVertex);

        while (!queue.isEmpty()) {
            String current = queue.poll();
            result.add(current);

            for (Edge edge : graph.getEdges(current)) {
                if (!visited.contains(edge.getDestination())) {
                    queue.add(edge.getDestination());
                    visited.add(edge.getDestination());
                }
            }
        }
        return result;
    }

    // Recorrido en Profundidad (DFS)
    public List<String> depthFirstSearch(Graph graph, String startVertex) {
        List<String> result = new ArrayList<>();
        Set<String> visited = new HashSet<>();
        Stack<String> stack = new Stack<>();

        stack.push(startVertex);

        while (!stack.isEmpty()) {
            String current = stack.pop();
            if (!visited.contains(current)) {
                visited.add(current);
                result.add(current);

                for (Edge edge : graph.getEdges(current)) {
                    if (!visited.contains(edge.getDestination())) {
                        stack.push(edge.getDestination());
                    }
                }
            }
        }
        return result;
    }
}