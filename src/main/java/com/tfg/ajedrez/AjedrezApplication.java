package com.tfg.ajedrez;

import com.tfg.ajedrez.util.SceneManager;
import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.InputStream;

/**
 * Clase principal de JavaFX, desde aquí arranca la app.
 * 
 * Se registra el stage en SceneManager para que los demás controladores
 * puedan cambiar de escena sin tener que recibir stage como parámetro.
 * 
 * De primeras se carga la primera vista, el login.
 */
public class AjedrezApplication extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        aplicarIcono(stage);
        SceneManager.init(stage);
        SceneManager.navegarA("/com/tfg/ajedrez/vista/login.fxml");
        stage.show();
    }

    /**
     * Carga el icono de la ventana.
     *
     * Se hace en un try-with-resources para que getResourceAsStream se cierre
     * automáticamente.
     * Si el recurso no existe se ignora. La aplicación sigue arrancando igualmente.
     */

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
