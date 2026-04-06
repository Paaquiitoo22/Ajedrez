package com.tfg.ajedrez.controller;

import com.tfg.ajedrez.util.SceneManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class MenuPrincipalController {

    @FXML
    public void onVolver(ActionEvent event) throws Exception {
        SceneManager.navegarA("/com/tfg/ajedrez/vista/login.fxml");
    }
}
