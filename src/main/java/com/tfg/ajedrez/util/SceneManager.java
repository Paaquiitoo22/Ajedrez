package com.tfg.ajedrez.util;

import com.tfg.ajedrez.AjedrezApplication;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;


public class SceneManager {
    private static Stage primaryStage;
   public static void init(Stage stage){
       primaryStage=stage;
   }
    public static void navegarA(String ventana)  {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(AjedrezApplication.class.getResource(ventana));
            Parent root =  fxmlLoader.load();
            Scene scene = new Scene(root, 300,700);
            primaryStage.setScene(scene);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}

