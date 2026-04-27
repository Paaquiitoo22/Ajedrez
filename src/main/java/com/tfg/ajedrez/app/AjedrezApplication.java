package com.tfg.ajedrez.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class AjedrezApplication extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(
                AjedrezApplication.class.getResource("/com/tfg/ajedrez/fxml/login.fxml")
        );

        Scene scene = new Scene(loader.load());
        stage.setTitle("MiChess");
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}