package com.tfg.ajedrez.controller;

import com.tfg.ajedrez.util.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

import javafx.event.ActionEvent;

public class NuevaPartidaController {

    @FXML private Button btnClasica, btnBlitz, btnRapida, btnPersonalizada;
    @FXML private Button btnContraIA, btnDosJugadores;
    @FXML private Button btnBlancas, btnNegras, btnAleatorio;


    @FXML
    public void onModoJuego(ActionEvent e) {
        // 1. Quitar la marca dorada a todos los del grupo
        btnClasica.getStyleClass().remove("seleccionado");
        btnBlitz.getStyleClass().remove("seleccionado");
        btnRapida.getStyleClass().remove("seleccionado");
        btnPersonalizada.getStyleClass().remove("seleccionado");

        // 2. Pintar de dorado el que se acaba de pulsar
        Button pulsado = (Button) e.getSource();
        pulsado.getStyleClass().add("seleccionado");
    }

    @FXML
    public void onTipoPartida(ActionEvent e) {
        btnContraIA.getStyleClass().remove("seleccionado");
        btnDosJugadores.getStyleClass().remove("seleccionado");

        Button pulsado = (Button) e.getSource();
        pulsado.getStyleClass().add("seleccionado");
    }

    @FXML
    public void onJugarCon(ActionEvent e) {
        btnBlancas.getStyleClass().remove("seleccionado");
        btnNegras.getStyleClass().remove("seleccionado");
        btnAleatorio.getStyleClass().remove("seleccionado");

        Button pulsado = (Button) e.getSource();
        pulsado.getStyleClass().add("seleccionado");
    }

    @FXML
    public void onVolver(){
        SceneManager.navegarA("/com/tfg/ajedrez/vista/menu-principal.fxml");
    }

    @FXML
    public void onPartida(){
        SceneManager.navegarA("/com/tfg/ajedrez/vista/partida.fxml");
    }

    @FXML
    public void onTiempoPartida(){
        SceneManager.navegarA("/com/tfg/ajedrez/vista/tiempo-partida.fxml");
    }
}
