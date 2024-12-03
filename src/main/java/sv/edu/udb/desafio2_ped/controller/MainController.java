package sv.edu.udb.desafio2_ped.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;

public class MainController {

    @FXML private ComboBox<String> countrySelector;
    @FXML private Button loadButton;

    @FXML
    public void initialize() {
        // Cargar los países disponibles en el ComboBox
        countrySelector.getItems().addAll("El Salvador", "Guatemala", "Honduras", "Nicaragua", "Costa Rica");
    }

    @FXML
    private void onLoadMap() {
        String selectedCountry = countrySelector.getValue();
        if (selectedCountry == null) {
            System.out.println("Por favor selecciona un país.");
        } else {
            System.out.println("Mapa cargado: " + selectedCountry);
            // Aquí puedes cargar la siguiente vista y los datos del grafo
        }
    }
}