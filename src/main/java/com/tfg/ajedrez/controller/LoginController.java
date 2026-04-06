package com.tfg.ajedrez.controller;

import com.tfg.ajedrez.util.SceneManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class LoginController {

    @FXML
    public void onEntrar(ActionEvent event) throws Exception{
        SceneManager.navegarA("/com/tfg/ajedrez/vista/menu-principal.fxml");
    }

}
