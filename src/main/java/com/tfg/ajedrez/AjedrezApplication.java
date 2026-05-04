package com.tfg.ajedrez;

import com.tfg.ajedrez.util.SceneManager;
import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.InputStream;

public class AjedrezApplication extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        aplicarIcono(stage);
        SceneManager.init(stage);
        SceneManager.navegarA("/com/tfg/ajedrez/vista/login.fxml");
        stage.show();
    }

    private void aplicarIcono(Stage stage) {
        try (InputStream icon = getClass().getResourceAsStream("/com/tfg/ajedrez/img/i32.png")) {
            if (icon != null) {
                stage.getIcons().add(new Image(icon));
            }
        } catch (Exception e) {
            System.err.println("[APP] No se pudo cargar el icono: " + e.getMessage());
        }
    }
}
