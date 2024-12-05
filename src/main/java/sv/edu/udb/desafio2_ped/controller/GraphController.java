package sv.edu.udb.desafio2_ped.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.layout.Pane;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.*;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;

import sv.edu.udb.desafio2_ped.model.*;

import java.util.Map;

public class GraphController {

    @FXML private ComboBox<String> mapSelector;
    @FXML private Pane graphPane;
    @FXML private Label statusLabel;
    @FXML private ComboBox<String> startNodeSelector;

    private Map<String, Double[]> positions = new HashMap<>();


    private Graph currentGraph = new Graph();
    private GraphAlgorithms algorithms = new GraphAlgorithms();

    @FXML
    public void initialize() {
        // Cargar mapas predefinidos en el ComboBox
        mapSelector.getItems().addAll("El Salvador", "Centroamérica");
        addContextMenuToPane(50);
        // Configurar el ComboBox de nodos de inicio
        startNodeSelector.setPromptText("Seleccione el nodo origen");
        startNodeSelector.getItems().clear();

        // Agregar evento para capturar coordenadas al hacer clic en el Pane
        graphPane.setOnMouseClicked(event -> {
            double x = event.getX();
            double y = event.getY();
            System.out.println("Coordenadas: x = " + x + ", y = " + y);
        });

    }

    @FXML
    private void onLoadMap() {
        String selectedMap = mapSelector.getValue();
        if (selectedMap == null) {
            statusLabel.setText("Por favor, selecciona un mapa.");
            return;
        }

        // Limpiar nodos, aristas y datos actuales
        graphPane.getChildren().clear();
        positions.clear();
        currentGraph = new Graph();
        startNodeSelector.getItems().clear();

        // Cargar el mapa correspondiente (puedes conectarlo con JSON o mapas predefinidos)
        if (selectedMap.equals("El Salvador")) {
            loadJsonMap("src/main/resources/maps/grafo_elsalvador.json");
            statusLabel.setText("Mapa cargado: " + selectedMap);
        }
        if (selectedMap.equals("Centroamérica")) {
            loadJsonMap("src/main/resources/maps/grafo_centroamerica.json");
            statusLabel.setText("Mapa cargado: " + selectedMap);
        }
    }

    @FXML
    private void onBFS() {
        String startNode = startNodeSelector.getValue(); // Obtener el nodo seleccionado
        if (startNode == null) {
            statusLabel.setText("Por favor, seleccione un nodo origen.");
            return;
        }

        if (currentGraph.getVertices().isEmpty()) {
            statusLabel.setText("Por favor, carga un mapa primero.");
            return;
        }

        List<String> result = algorithms.breadthFirstSearch(currentGraph, startNode); // Usar el nodo seleccionado
        statusLabel.setText("Recorrido en anchura: " + String.join(" -> ", result));
    }

    @FXML
    private void onDFS() {
        String startNode = startNodeSelector.getValue(); // Obtener el nodo seleccionado
        if (startNode == null) {
            statusLabel.setText("Por favor, seleccione un nodo origen.");
            return;
        }

        if (currentGraph.getVertices().isEmpty()) {
            statusLabel.setText("Por favor, carga un mapa primero.");
            return;
        }

        List<String> result = algorithms.depthFirstSearch(currentGraph, startNode); // Usar el nodo seleccionado
        statusLabel.setText("Recorrido en profundidad: " + String.join(" -> ", result));
    }

    private void visualizeGraph() {
        graphPane.getChildren().clear(); // Limpia el contenido del Pane

        // Dibujar los nodos
        for (String vertex : currentGraph.getVertices()) {
            Double[] currentPos = positions.get(vertex);

            // Verificar si la posición ya existe
            double x = currentPos != null ? currentPos[0] : 50;
            double y = currentPos != null ? currentPos[1] : 50;

            // Crear el círculo para el nodo
            Circle circle = new Circle(x, y, 20, Color.LIGHTBLUE);
            circle.setStroke(Color.BLACK);

            // Agregar funcionalidad al nodo
            makeNodeDraggable(circle);
            addContextMenuToNode(circle, vertex);

            // Etiqueta para el nodo
            Text label = new Text(x - 10, y + 5, vertex);

            // Guardar la posición del nodo (si es nueva)
            positions.putIfAbsent(vertex, new Double[]{x, y});

            // Agregar el nodo y su etiqueta al Pane
            graphPane.getChildren().addAll(circle, label);
        }

        // Dibujar las aristas del grafo evitando duplicados
        Set<String> visitedEdges = new HashSet<>();
        for (String vertex : currentGraph.getVertices()) {
            for (Edge edge : currentGraph.getEdges(vertex)) {
                String edgeKey = vertex + "-" + edge.getDestination();

                // Evitar duplicados en las aristas bidireccionales
                if (!visitedEdges.contains(edgeKey)) {
                    Double[] startPos = positions.get(edge.getSource());
                    Double[] endPos = positions.get(edge.getDestination());

                    if (startPos != null && endPos != null) {
                        // Dibujar línea entre nodos
                        Line line = new Line(startPos[0], startPos[1], endPos[0], endPos[1]);
                        line.setStroke(Color.GRAY);

                        // Etiqueta con la distancia (peso de la arista)
                        double midX = (startPos[0] + endPos[0]) / 2;
                        double midY = (startPos[1] + endPos[1]) / 2;
                        Text distanceLabel = new Text(midX, midY, String.valueOf(edge.getDistance()));

                        // Agregar línea y etiqueta al Pane
                        graphPane.getChildren().addAll(line, distanceLabel);

                        // Marcar ambas direcciones como visitadas
                        visitedEdges.add(edgeKey);
                        visitedEdges.add(edge.getDestination() + "-" + vertex);
                    }
                }
            }
        }
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

    private void addContextMenuToNode(Circle circle, String vertex) {
        // Evita crear un nuevo ContextMenu cada vez
        ContextMenu contextMenu = new ContextMenu();

        // Crear opciones del menú
        MenuItem deleteVertex = new MenuItem("Eliminar Vértice");
        MenuItem deleteEdge = new MenuItem("Eliminar Arco");

        // Configurar acciones
        deleteVertex.setOnAction(e -> {
            currentGraph.removeVertex(vertex);
            visualizeGraph();
        });

        deleteEdge.setOnAction(e -> {
            currentGraph.removeEdges(vertex);
            visualizeGraph();
        });

        // Agregar opciones al menú
        contextMenu.getItems().addAll(deleteVertex, deleteEdge);

        // Asociar el menú al nodo una sola vez
        circle.setOnContextMenuRequested(e -> {
            if (contextMenu.isShowing()) {
                contextMenu.hide();
            }
            contextMenu.show(circle, e.getScreenX(), e.getScreenY());
        });
    }

    private void addContextMenuToPane(double areaSize) {
        // Crear el menú contextual
        ContextMenu contextMenu = new ContextMenu();

        // Crear opción de menú para agregar nodo
        MenuItem addVertex = new MenuItem("Nuevo Vértice");
        addVertex.setOnAction(e -> {
            // Obtener la posición donde se hizo clic derecho
            ContextMenuEvent event = (ContextMenuEvent) contextMenu.getProperties().get("lastEvent");
            if (event != null) {
                double x = event.getX();
                double y = event.getY();

                // Solicitar nombre del nodo
                TextInputDialog dialog = new TextInputDialog();
                dialog.setTitle("Crear Nodo");
                dialog.setHeaderText("Ingrese el nombre del nodo:");
                dialog.setContentText("Nombre:");

                Optional<String> result = dialog.showAndWait();
                result.ifPresent(name -> {
                    if (!name.isEmpty() && !currentGraph.getVertices().contains(name)) {
                        // Crear el nodo si el nombre es válido y único
                        currentGraph.addVertex(name);
                        startNodeSelector.getItems().add(name); // Agregar al ComboBox

                        // Crear el círculo visual del nodo
                        Circle newNode = new Circle(x, y, 20, Color.LIGHTBLUE);
                        newNode.setStroke(Color.BLACK);
                        makeNodeDraggable(newNode);
                        addContextMenuToNode(newNode, name);

                        // Etiqueta del nodo
                        Text label = new Text(x - 10, y + 5, name);
                        graphPane.getChildren().addAll(newNode, label);

                        // Guardar posición
                        positions.put(name, new Double[]{x, y});
                    } else {
                        statusLabel.setText("El nombre del nodo es inválido o ya existe.");
                    }
                });
            }
        });

        contextMenu.getItems().add(addVertex);

        // Asignar el evento de clic derecho al `Pane`
        graphPane.setOnContextMenuRequested(e -> {
            // Detectar si el clic ocurrió cerca de un nodo existente
            boolean clickedNearNode = false;
            Circle targetNode = null;

            for (var node : graphPane.getChildren()) {
                if (node instanceof Circle circle) {
                    double distance = Math.hypot(circle.getCenterX() - e.getX(), circle.getCenterY() - e.getY());
                    if (distance <= areaSize) {
                        clickedNearNode = true;
                        targetNode = circle;
                        break;
                    }
                }
            }

            if (clickedNearNode) {
                // Mostrar el menú contextual para el nodo
                ContextMenu nodeContextMenu = createNodeContextMenu(targetNode);
                nodeContextMenu.show(graphPane, e.getScreenX(), e.getScreenY());
            } else {
                // Mostrar el menú contextual general
                contextMenu.getProperties().put("lastEvent", e);
                contextMenu.show(graphPane, e.getScreenX(), e.getScreenY());
            }
        });

        // Ocultar el menú contextual si se hace clic fuera
        graphPane.setOnMouseClicked(e -> {
            if (e.getButton() == javafx.scene.input.MouseButton.PRIMARY) {
                contextMenu.hide();
            }
        });
    }

    private ContextMenu createNodeContextMenu(Circle circle) {
        ContextMenu contextMenu = new ContextMenu();

        String vertex = getVertexNameFromCircle(circle);

        MenuItem addEdge = new MenuItem("Agregar Arco");
        addEdge.setOnAction(e -> {
            initiateEdgeCreation(circle, vertex);
        });
        contextMenu.getItems().add(addEdge);


        // Opción para eliminar vértice
        MenuItem deleteVertex = new MenuItem("Eliminar Vértice");
        deleteVertex.setOnAction(e -> {
            currentGraph.removeVertex(vertex); // Eliminar el vértice del grafo
            startNodeSelector.getItems().remove(vertex); // Eliminar del ComboBox
            visualizeGraph(); // Redibujar el grafo
        });

        // Opción para eliminar arista
        MenuItem deleteEdge = new MenuItem("Eliminar Arco");
        deleteEdge.setOnAction(e -> {
            currentGraph.removeEdges(vertex); // Eliminar todas las aristas salientes
            visualizeGraph(); // Redibujar el grafo
        });

        contextMenu.getItems().addAll(deleteVertex, deleteEdge);
        return contextMenu;
    }

    private void makeNodeDraggable(Circle circle) {
        circle.setOnMousePressed(e -> {
            circle.setUserData(new double[]{e.getSceneX(), e.getSceneY()}); // Guardar la posición inicial
        });

        circle.setOnMouseDragged(e -> {
            double[] startPos = (double[]) circle.getUserData();
            double offsetX = e.getSceneX() - startPos[0];
            double offsetY = e.getSceneY() - startPos[1];

            // Nueva posición del nodo
            double newX = circle.getCenterX() + offsetX;
            double newY = circle.getCenterY() + offsetY;

            // Restringir movimiento dentro del pane
            if (newX - circle.getRadius() >= 0 && newX + circle.getRadius() <= graphPane.getWidth()) {
                circle.setCenterX(newX);
            }
            if (newY - circle.getRadius() >= 0 && newY + circle.getRadius() <= graphPane.getHeight()) {
                circle.setCenterY(newY);
            }

            // Actualizar la posición inicial para el siguiente movimiento
            circle.setUserData(new double[]{e.getSceneX(), e.getSceneY()});

            // Actualizar la posición en el mapa
            String vertex = getVertexNameFromCircle(circle);
            if (vertex != null) {
                positions.put(vertex, new Double[]{circle.getCenterX(), circle.getCenterY()});
            }

            // Redibujar las aristas
            visualizeGraph();
        });
    }

    private void initiateEdgeCreation(Circle sourceCircle, String sourceVertex) {
        Line tempLine = new Line();
        tempLine.setStartX(sourceCircle.getCenterX());
        tempLine.setStartY(sourceCircle.getCenterY());

        // Hacer que la línea siga el cursor
        graphPane.setOnMouseMoved(e -> {
            tempLine.setEndX(e.getX());
            tempLine.setEndY(e.getY());
        });

        // Agregar la línea al Pane
        graphPane.getChildren().add(tempLine);

        // Asignar evento de clic en los nodos para completar la conexión
        graphPane.setOnMouseClicked(e -> {
            // Detectar si se hizo clic en un nodo
            for (var node : graphPane.getChildren()) {
                if (node instanceof Circle targetCircle && targetCircle != sourceCircle) {
                    double distance = Math.hypot(
                            targetCircle.getCenterX() - e.getX(),
                            targetCircle.getCenterY() - e.getY()
                    );

                    if (distance < targetCircle.getRadius()) {
                        String targetVertex = getVertexNameFromCircle(targetCircle);

                        // Pedir el peso del arco
                        TextInputDialog dialog = new TextInputDialog("1.0");
                        dialog.setTitle("Agregar Peso");
                        dialog.setHeaderText("Define el peso del arco entre " + sourceVertex + " y " + targetVertex);
                        dialog.setContentText("Peso:");

                        Optional<String> result = dialog.showAndWait();
                        result.ifPresent(weight -> {
                            try {
                                double edgeWeight = Double.parseDouble(weight);

                                // Agregar el arco al grafo
                                currentGraph.addEdge(sourceVertex, targetVertex, edgeWeight);

                                // Dibujar la línea final
                                Line finalLine = new Line(
                                        sourceCircle.getCenterX(),
                                        sourceCircle.getCenterY(),
                                        targetCircle.getCenterX(),
                                        targetCircle.getCenterY()
                                );
                                finalLine.setStroke(Color.BLACK);
                                graphPane.getChildren().add(finalLine);

                                // Agregar la etiqueta del peso
                                double midX = (sourceCircle.getCenterX() + targetCircle.getCenterX()) / 2;
                                double midY = (sourceCircle.getCenterY() + targetCircle.getCenterY()) / 2;
                                Text weightLabel = new Text(midX, midY, String.valueOf(edgeWeight));
                                graphPane.getChildren().add(weightLabel);

                            } catch (NumberFormatException ex) {
                                statusLabel.setText("Peso inválido.");
                            }
                        });

                        // Limpiar eventos temporales
                        graphPane.setOnMouseMoved(null);
                        graphPane.setOnMouseClicked(null);
                        graphPane.getChildren().remove(tempLine);

                        return; // Salir después de agregar el arco
                    }
                }
            }
        });
    }


    // Método para cargar el archivo JSON
    private void loadJsonMap(String filePath) {
        try (FileReader reader = new FileReader(filePath)) {
            Gson gson = new Gson();

            // Definir el tipo de datos esperados en el JSON
            Type mapType = new TypeToken<Map<String, Object>>() {}.getType();
            Map<String, Object> jsonMap = gson.fromJson(reader, mapType);

            // Cargar el fondo (si está definido en el JSON)
            String backgroundPath = (String) jsonMap.get("background");
            if (backgroundPath != null) {
                setPaneBackground(backgroundPath);
            }

            // Procesar los departamentos
            List<Map<String, Object>> departamentos = (List<Map<String, Object>>) jsonMap.get("nodos");
            for (Map<String, Object> departamento : departamentos) {
                String nombre = (String) departamento.get("nombre");
                double x = ((Number) departamento.get("x")).doubleValue();
                double y = ((Number) departamento.get("y")).doubleValue();

                currentGraph.addVertex(nombre);
                positions.put(nombre, new Double[]{x, y});
            }

            // Procesar las conexiones
            List<Map<String, Object>> conexiones = (List<Map<String, Object>>) jsonMap.get("conexiones");
            for (Map<String, Object> conexion : conexiones) {
                String origen = (String) conexion.get("origen");
                String destino = (String) conexion.get("destino");
                double peso = ((Number) conexion.get("peso")).doubleValue();

                currentGraph.addEdge(origen, destino, peso);
            }

            visualizeGraph(); // Dibujar el grafo
            updateStartNodeSelector(); // Actualizar los nodos en el ComboBox

        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setText("Error al cargar el mapa desde JSON.");
        }
    }

    private void updateStartNodeSelector() {
        // Limpiar las opciones actuales del ComboBox
        startNodeSelector.getItems().clear();

        // Agregar los vértices actuales del grafo como opciones
        startNodeSelector.getItems().addAll(currentGraph.getVertices());
    }

    private void setPaneBackground(String imagePath) {
        try {
            // Crear una imagen desde el archivo proporcionado
            javafx.scene.image.Image image = new javafx.scene.image.Image(imagePath);

            // Crear un tamaño de fondo que se ajuste dinámicamente al tamaño del Pane
            javafx.scene.layout.BackgroundSize backgroundSize = new javafx.scene.layout.BackgroundSize(
                    javafx.scene.layout.BackgroundSize.AUTO, // Auto ajusta el ancho
                    javafx.scene.layout.BackgroundSize.AUTO, // Auto ajusta el alto
                    false, // No escalar más allá del tamaño
                    false,
                    true, // Mantener proporciones
                    false // No recortar
            );

            // Crear un BackgroundImage que incluye la imagen ajustada al tamaño del Pane
            javafx.scene.layout.BackgroundImage backgroundImage = new javafx.scene.layout.BackgroundImage(
                    image,
                    javafx.scene.layout.BackgroundRepeat.NO_REPEAT, // No repetir la imagen
                    javafx.scene.layout.BackgroundRepeat.NO_REPEAT,
                    javafx.scene.layout.BackgroundPosition.CENTER, // Centrar la imagen
                    backgroundSize
            );

            // Establecer la imagen de fondo en el Pane
            graphPane.setBackground(new javafx.scene.layout.Background(backgroundImage));
        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setText("Error al cargar la imagen de fondo.");
        }
    }



}
