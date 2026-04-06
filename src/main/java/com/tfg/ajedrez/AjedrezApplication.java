package com.tfg.ajedrez;

import com.tfg.ajedrez.util.SceneManager;
import javafx.application.Application;
import javafx.stage.Stage;

public class AjedrezApplication extends Application {

    /**
     * Escenario principal de la aplicación, sobre el que se mostrarán las escenas
     * JavaFX.
     */
    private Stage primaryStage;

    /**
     * Devuelve el escenario (ventana) principal de la aplicación.
     *
     * @return {@link Stage} principal de la aplicación
     */
    public Stage getPrimaryStage() {
        return primaryStage;
    }

    @Override
    public void start(Stage stage) throws Exception {

        this.primaryStage = stage;
        SceneManager.init(stage);
        SceneManager.navegarA("/com/tfg/ajedrez/vista/login.fxml");

        primaryStage.show();

    }

}
