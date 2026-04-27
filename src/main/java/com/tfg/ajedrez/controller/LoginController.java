package com.tfg.ajedrez.controller;

import com.tfg.ajedrez.service.NavegacionService;
import com.tfg.ajedrez.service.SesionService;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.event.ActionEvent;
import javafx.stage.Stage;

public class LoginController {

    @FXML
    public void iniciarSesion(ActionEvent event) {
        SesionService.entrarComoInvitado();

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        NavegacionService.cambiarEscena(stage, "/com/tfg/ajedrez/fxml/menu.fxml", "Menú principal");
    }

    @FXML
    public void entrarInvitado(ActionEvent event) {
        iniciarSesion(event);
    }

    @FXML
    public void registrarse(ActionEvent event) {
        iniciarSesion(event);
    }

    @FXML
    public void registrarUsuario(ActionEvent event) {
        iniciarSesion(event);
    }
}