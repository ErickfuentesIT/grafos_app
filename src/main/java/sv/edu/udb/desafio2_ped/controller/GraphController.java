package sv.edu.udb.desafio2_ped.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.Pane;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;

import sv.edu.udb.desafio2_ped.model.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class GraphController {

    @FXML private ComboBox<String> mapSelector;
    @FXML private Button loadMapButton;
    @FXML private Button bfsButton;
    @FXML private Button dfsButton;
    @FXML private Pane graphPane;
    @FXML private Label statusLabel;
    private Map<String, Double[]> positions = new HashMap<>();


    private Graph currentGraph = new Graph();
    private GraphAlgorithms algorithms = new GraphAlgorithms();

    @FXML
    public void initialize() {
        // Cargar mapas predefinidos en el ComboBox
        mapSelector.getItems().addAll("El Salvador", "Centroamérica");
    }

    @FXML
    private void onLoadMap() {
        String selectedMap = mapSelector.getValue();
        if (selectedMap == null) {
            statusLabel.setText("Por favor, selecciona un mapa.");
            return;
        }

        // Cargar el mapa correspondiente (puedes conectarlo con JSON o mapas predefinidos)
        loadPredefinedMap(selectedMap);
        statusLabel.setText("Mapa cargado: " + selectedMap);
    }

    @FXML
    private void onBFS() {
        if (currentGraph.getVertices().isEmpty()) {
            statusLabel.setText("Por favor, carga un mapa primero.");
            return;
        }

        List<String> result = algorithms.breadthFirstSearch(currentGraph, "San Salvador");
        statusLabel.setText("Recorrido en anchura: " + String.join(" -> ", result));
    }

    @FXML
    private void onDFS() {
        if (currentGraph.getVertices().isEmpty()) {
            statusLabel.setText("Por favor, carga un mapa primero.");
            return;
        }

        List<String> result = algorithms.depthFirstSearch(currentGraph, "San Salvador");
        statusLabel.setText("Recorrido en profundidad: " + String.join(" -> ", result));
    }

    private void loadPredefinedMap(String mapName) {
        currentGraph = new Graph();
        if (mapName.equals("El Salvador")) {
            currentGraph.addVertex("San Salvador");
            currentGraph.addVertex("Santa Ana");
            currentGraph.addVertex("La Libertad");
            currentGraph.addEdge("San Salvador", "Santa Ana", 65);
            currentGraph.addEdge("San Salvador", "La Libertad", 30);
        } else if (mapName.equals("Centroamérica")) {
            currentGraph.addVertex("Guatemala");
            currentGraph.addVertex("El Salvador");
            currentGraph.addVertex("Honduras");
            currentGraph.addEdge("Guatemala", "El Salvador", 204);
            currentGraph.addEdge("El Salvador", "Honduras", 180);
        }

        // Dibujar el grafo en el Pane
        for (String vertex : currentGraph.getVertices()) {
            Circle newNode = new Circle(100, 100, 20, Color.LIGHTBLUE); // Coordenadas iniciales
            makeNodeDraggable(newNode);
            enableConnectionCreation(newNode, vertex);
            enableNodeRenaming(newNode, vertex);

            graphPane.getChildren().add(newNode);
            graphPane.getChildren().add(new Text(90, 105, vertex)); // Etiqueta del nodo
        }

        // Visualizar el grafo (implementar visualización en graphPane)
          visualizeGraph();

    }

    private void visualizeGraph() {
        graphPane.getChildren().clear(); // Limpia el contenido del Pane
        positions.clear(); // Limpia las posiciones antes de redibujar

        int x = 50; // Coordenada inicial X
        int y = 50; // Coordenada inicial Y

        for (String vertex : currentGraph.getVertices()) {
            Circle circle = new Circle(x, y, 20, Color.LIGHTBLUE);
            circle.setStroke(Color.BLACK);

            Text label = new Text(x - 10, y + 5, vertex);

            positions.put(vertex, new Double[]{(double) x, (double) y}); // Guardar posición del vértice

            graphPane.getChildren().addAll(circle, label);

            x += 100;
            if (x > graphPane.getWidth() - 50) {
                x = 50; // Salta a la siguiente fila
                y += 100;
            }
        }

        // Dibujar las aristas
        for (String vertex : currentGraph.getVertices()) {
            for (Edge edge : currentGraph.getEdges(vertex)) {
                Double[] startPos = positions.get(edge.getSource());
                Double[] endPos = positions.get(edge.getDestination());

                if (startPos != null && endPos != null) {
                    Line line = new Line(startPos[0], startPos[1], endPos[0], endPos[1]);
                    line.setStroke(Color.GRAY);

                    double midX = (startPos[0] + endPos[0]) / 2;
                    double midY = (startPos[1] + endPos[1]) / 2;
                    Text distanceLabel = new Text(midX, midY, String.valueOf(edge.getDistance()));

                    graphPane.getChildren().addAll(line, distanceLabel);
                }
            }
        }
    }


    private void makeNodeDraggable(Circle circle) {
        circle.setOnMousePressed(e -> {
            circle.setUserData(new double[]{e.getSceneX(), e.getSceneY()}); // Guardar la posición inicial
        });

        circle.setOnMouseDragged(e -> {
            double[] startPos = (double[]) circle.getUserData();
            double offsetX = e.getSceneX() - startPos[0];
            double offsetY = e.getSceneY() - startPos[1];
            circle.setCenterX(circle.getCenterX() + offsetX);
            circle.setCenterY(circle.getCenterY() + offsetY);

            circle.setUserData(new double[]{e.getSceneX(), e.getSceneY()}); // Actualizar posición
        });
    }

    private void enableConnectionCreation(Circle circle, String sourceVertex) {
        circle.setOnMousePressed(e -> {
            Line tempLine = new Line();
            tempLine.setStartX(circle.getCenterX());
            tempLine.setStartY(circle.getCenterY());
            graphPane.getChildren().add(tempLine);

            circle.setOnMouseDragged(dragEvent -> {
                tempLine.setEndX(dragEvent.getSceneX());
                tempLine.setEndY(dragEvent.getSceneY());
            });

            circle.setOnMouseReleased(releaseEvent -> {
                graphPane.getChildren().remove(tempLine); // Remover línea temporal

                for (var node : graphPane.getChildren()) {
                    if (node instanceof Circle targetCircle && targetCircle != circle) {
                        double distance = Math.hypot(
                                targetCircle.getCenterX() - releaseEvent.getSceneX(),
                                targetCircle.getCenterY() - releaseEvent.getSceneY()
                        );
                        if (distance < 20) { // Verificar si soltó sobre otro nodo
                            String targetVertex = getVertexNameFromCircle(targetCircle);
                            currentGraph.addEdge(sourceVertex, targetVertex, 1.0); // Peso por defecto
                            visualizeGraph(); // Actualizar la visualización
                            break;
                        }
                    }
                }
            });
        });
    }

    private void enableEdgeDeletion(Line line, String sourceVertex, String targetVertex) {
        line.setOnContextMenuRequested(e -> {
            currentGraph.getEdges(sourceVertex).removeIf(edge -> edge.getDestination().equals(targetVertex));
            visualizeGraph(); // Actualizar la visualización
        });
    }

    private void enableWeightEditing(Line line, Edge edge) {
        line.setOnContextMenuRequested(e -> {
            TextInputDialog dialog = new TextInputDialog(String.valueOf(edge.getDistance()));
            dialog.setTitle("Editar Peso");
            dialog.setHeaderText("Editar peso de la conexión entre " + edge.getSource() + " y " + edge.getDestination());
            dialog.setContentText("Nuevo peso:");

            Optional<String> result = dialog.showAndWait();
            result.ifPresent(weight -> {
                try {
                    edge.setDistance(Double.parseDouble(weight));
                    visualizeGraph(); // Actualizar visualización
                } catch (NumberFormatException ex) {
                    statusLabel.setText("Peso inválido.");
                }
            });
        });
    }

    private void enableNodeRenaming(Circle circle, String vertexName) {
        circle.setOnContextMenuRequested(e -> {
            TextInputDialog dialog = new TextInputDialog(vertexName);
            dialog.setTitle("Renombrar Nodo");
            dialog.setHeaderText("Renombrar nodo: " + vertexName);
            dialog.setContentText("Nuevo nombre:");

            Optional<String> result = dialog.showAndWait();
            result.ifPresent(newName -> {
                if (currentGraph.getVertices().contains(newName)) {
                    statusLabel.setText("El nombre ya existe.");
                } else {
                    currentGraph.getVertices().remove(vertexName);
                    currentGraph.addVertex(newName);
                    visualizeGraph(); // Actualizar visualización
                }
            });
        });
    }
    private String getVertexNameFromCircle(Circle circle) {
        for (Map.Entry<String, Double[]> entry : positions.entrySet()) {
            Double[] position = entry.getValue();
            if (position != null && circle.getCenterX() == position[0] && circle.getCenterY() == position[1]) {
                return entry.getKey();
            }
        }
        return null; // Si no se encuentra, retorna null
    }








}
