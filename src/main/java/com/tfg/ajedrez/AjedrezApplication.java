package com.tfg.ajedrez;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class AjedrezApplication extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        FXMLLoader loader=new FXMLLoader(AjedrezApplication.class.getResource("/com/tfg/ajedrez/vista/login.fxml"));
        Scene escena=new Scene(loader.load(),300,700);
        stage.setScene(escena);
        //stage.initStyle(StageStyle.UNDECORATED);
        stage.show();

    }
}
