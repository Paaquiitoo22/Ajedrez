package com.tfg.ajedrez.service;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class NavegacionService {

    public static void cambiarEscena(Stage stage, String rutaFXML, String titulo) {
        try {
            FXMLLoader loader = new FXMLLoader(NavegacionService.class.getResource(rutaFXML));
            Scene scene = new Scene(loader.load());

            stage.setTitle(titulo);
            stage.setScene(scene);
            stage.centerOnScreen();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
