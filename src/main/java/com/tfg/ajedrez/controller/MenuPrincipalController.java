package com.tfg.ajedrez.controller;

import com.tfg.ajedrez.AjedrezApplication;
import com.tfg.ajedrez.util.SceneManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.sql.Struct;

public class MenuPrincipalController {

    @FXML
    VBox menuDesplegable;

    @FXML
    Region RegionMenu;

    @FXML
    public void onVolver(ActionEvent event) throws Exception {
        SceneManager.navegarA("/com/tfg/ajedrez/vista/login.fxml");
    }

    /**
     * Método que cierra y abre el menú desplegable cuando pulsas en el boton del
     * menú
     *
     */
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

    /**
     * Método que cierra el menú desplegable cuando se pulsa en cualquier
     * parte de la pantalla del menú principal
     */

    @FXML
    public void onCerrarMenu() {

        if (menuDesplegable.isVisible() && RegionMenu.isVisible()) {
            menuDesplegable.setVisible(false);
            RegionMenu.setVisible(false);
        }
    }

    /**
     * Método que hace que al pulsar el botón de Nueva partida te lleve
     * a la ventana de nueva partida
     */

    @FXML
    public void onNuevaPartida() {
        SceneManager.navegarA("/com/tfg/ajedrez/vista/nueva-partida.fxml");
    }
}
