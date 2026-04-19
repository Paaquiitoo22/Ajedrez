package com.tfg.ajedrez.controller;

import com.tfg.ajedrez.AjedrezApplication;
import com.tfg.ajedrez.util.SceneManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;

public class MenuPrincipalController {


    @FXML
    VBox menuDesplegable;

    @FXML
    public void onVolver(ActionEvent event) throws Exception {
        SceneManager.navegarA("/com/tfg/ajedrez/vista/login.fxml");
    }

    @FXML
    public void onDesplegable(ActionEvent event)throws Exception{

       if (menuDesplegable.isVisible()){
           menuDesplegable.setVisible(false);
       }
        else{
            menuDesplegable.setVisible(true);
       }
    }

    /**
     * Método que hace que al pulsar el botón de Nueva partida te lleve
     * a la ventana de nueva partida
     */

    @FXML
    public void onNuevaPartida(){
        SceneManager.navegarA("/com/tfg/ajedrez/vista/nueva-partida.fxml");
    }
}
