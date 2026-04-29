package com.tfg.ajedrez.controller;

import com.tfg.ajedrez.util.SceneManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class MenuPrincipalController {

    @FXML VBox menuDesplegable;
    @FXML Region RegionMenu;

    // ── Partidas recientes ────────────────────────────────────────────────────
    // TODO: llamar a cargarPartidasRecientes() desde initialize() cuando el
    //       backend SQL esté disponible. Las tarjetas están ocultas (visible=false,
    //       managed=false) hasta que lleguen datos; sinPartidas se oculta al mostrarlas.

    @FXML private Label sinPartidas;

    @FXML private HBox cardPartida1;
    @FXML private Label resultado1;
    @FXML private Label oponente1;
    @FXML private Label fecha1;

    @FXML private HBox cardPartida2;
    @FXML private Label resultado2;
    @FXML private Label oponente2;
    @FXML private Label fecha2;

    @FXML private HBox cardPartida3;
    @FXML private Label resultado3;
    @FXML private Label oponente3;
    @FXML private Label fecha3;

    // ── Menú desplegable ─────────────────────────────────────────────────────

    @FXML
    public void onVolver(ActionEvent event) throws Exception {
        SceneManager.navegarA("/com/tfg/ajedrez/vista/login.fxml");
    }

    /** Abre/cierra el menú desplegable. */
    @FXML
    public void onDesplegable(ActionEvent event) throws Exception {
        if (menuDesplegable.isVisible()) {
            menuDesplegable.setVisible(false);
            RegionMenu.setVisible(false);
        } else {
            menuDesplegable.setVisible(true);
            RegionMenu.setVisible(true);
        }
    }

    /** Cierra el menú al pulsar fuera de él. */
    @FXML
    public void onCerrarMenu() {
        if (menuDesplegable.isVisible() && RegionMenu.isVisible()) {
            menuDesplegable.setVisible(false);
            RegionMenu.setVisible(false);
        }
    }

    /** Navega a la pantalla de nueva partida. */
    @FXML
    public void onNuevaPartida() {
        SceneManager.navegarA("/com/tfg/ajedrez/vista/nueva-partida.fxml");
    }
}
