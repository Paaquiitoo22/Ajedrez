package com.tfg.ajedrez.controller;

import com.tfg.ajedrez.model.ConfiguracionPartida;
import com.tfg.ajedrez.service.ConfiguracionPartidaService;
import com.tfg.ajedrez.service.NavegacionService;
import com.tfg.ajedrez.service.PartidaSesionService;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.stage.Stage;

public class NuevaPartidaController {

    @FXML
    private ComboBox<String> cmbModo;

    @FXML
    private Spinner<Integer> spMinutos;

    @FXML
    public void initialize() {
        cmbModo.getItems().addAll("Jugador vs Jugador", "Jugador vs IA");
        cmbModo.setValue("Jugador vs Jugador");

        spMinutos.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 60, 10)
        );
    }

    @FXML
    public void empezarPartida() {
        ConfiguracionPartida config = new ConfiguracionPartida();
        config.setModoJuego(cmbModo.getValue());
        config.setTiempoInicialSegundos(spMinutos.getValue() * 60);

        ConfiguracionPartidaService.setConfiguracionActual(config);
        PartidaSesionService.limpiar();

        Stage stage = (Stage) cmbModo.getScene().getWindow();
        NavegacionService.cambiarEscena(stage, "/com/tfg/ajedrez/fxml/partida.fxml", "Partida");
    }

    @FXML
    public void volverMenu() {
        Stage stage = (Stage) cmbModo.getScene().getWindow();
        NavegacionService.cambiarEscena(stage, "/com/tfg/ajedrez/fxml/menu.fxml", "Menú principal");
    }
}