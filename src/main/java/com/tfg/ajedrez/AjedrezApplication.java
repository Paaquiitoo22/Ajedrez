package com.tfg.ajedrez;

import com.tfg.ajedrez.util.SceneManager;
import javafx.application.Application;
import javafx.stage.Stage;

public class AjedrezApplication extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        SceneManager.init(stage);
        SceneManager.navegarA("/com/tfg/ajedrez/vista/login.fxml");
        stage.show();
    }

}
