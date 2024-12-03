import sv.edu.udb.desafio2_ped.model.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class test {

    @Test
    public void testBreadthFirstSearch() {
        Graph graph = new Graph();
        graph.addVertex("A");
        graph.addVertex("B");
        graph.addVertex("C");
        graph.addEdge("A", "B", 1);
        graph.addEdge("A", "C", 1);
        graph.addEdge("B", "C", 1);

        GraphAlgorithms algorithms = new GraphAlgorithms();
        List<String> result = algorithms.breadthFirstSearch(graph, "A");

        assertEquals(List.of("A", "B", "C"), result);
    }

    @Test
    public void testDepthFirstSearch() {
        Graph graph = new Graph();
        graph.addVertex("A");
        graph.addVertex("B");
        graph.addVertex("C");
        graph.addEdge("A", "B", 1);
        graph.addEdge("A", "C", 1);
        graph.addEdge("B", "C", 1);

        GraphAlgorithms algorithms = new GraphAlgorithms();
        List<String> result = algorithms.depthFirstSearch(graph, "A");

        assertEquals(List.of("A", "C", "B"), result); // Puede variar el orden dependiendo de la implementación
    }
}