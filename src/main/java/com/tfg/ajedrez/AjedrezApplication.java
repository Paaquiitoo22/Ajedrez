package com.tfg.ajedrez;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class AjedrezApplication extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        FXMLLoader loader=new FXMLLoader(AjedrezApplication.class.getResource("/com/tfg/ajedrez/vista/bienvenida.fxml"));
        Scene escena=new Scene(loader.load(),800,800);
        stage.setScene(escena);
        stage.show();

    }
}
