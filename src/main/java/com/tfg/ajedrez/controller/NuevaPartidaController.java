package com.tfg.ajedrez.controller;

import com.tfg.ajedrez.util.SceneManager;
import javafx.fxml.FXML;

public class NuevaPartidaController {

    @FXML
    public void onVolver(){
        SceneManager.navegarA("/com/tfg/ajedrez/vista/menu-principal.fxml");
    }
}
