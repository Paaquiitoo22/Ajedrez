package com.tfg.ajedrez.util;

import com.tfg.ajedrez.AjedrezApplication;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;


/**
 * Componente utilitario de gestión de flujo de escenas (Scene Management).
 * 
 * Centraliza la manipulación del escenario principal (Stage) de la aplicación, 
 * encargándose de la carga dinámica de recursos FXML y la conmutación de contextos 
 * gráficos mediante el desacoplamiento de la lógica de navegación de los controladores.
 */
public class SceneManager {
    /**
     * Referencia estática al escenario principal gestionado por la JVM.
     */
    private static Stage primaryStage;

    /**
     * Inicializa el gestor de escenas asignando el stage principal de la aplicación.
     * 
     * @param stage Escenario principal instanciado en el arranque de la aplicación.
     */
   public static void init(Stage stage){
       primaryStage=stage;
   }

    /**
     * Gestiona la transición entre vistas dentro del ecosistema de la aplicación.
     * 
     * Este método encapsula la lógica de carga de recursos FXML, la resolución de 
     * la jerarquía de nodos XML y la actualización del escenario principal (Stage).
     * 
     * @param ventana Localizador de recursos (Ruta) del archivo FXML de destino.
     */
    public static void navegarA(String ventana) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(AjedrezApplication.class.getResource(ventana));
            Parent root = fxmlLoader.load();
            
            // La escena se crea sin dimensiones fijas para respetar el tamaño definido en el FXML.
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            // Redimensiona el Stage para ajustarse al tamaño preferido del nuevo FXML
            primaryStage.sizeToScene();

        } catch (IOException e) {
            System.err.println("[SCENE-ERROR] No se pudo cargar la vista: " + ventana);
            e.printStackTrace();
            mostrarAlertaError("Error de Navegación", "No se pudo cargar la interfaz: " + ventana);
        }
    }

    /**
     * Muestra una alerta gráfica de error en caso de fallo crítico de carga.
     */
    private static void mostrarAlertaError(String titulo, String contenido) {
        javafx.scene.control.Alert alerta = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
        alerta.setTitle("Error Grave");
        alerta.setHeaderText(titulo);
        alerta.setContentText(contenido);
        alerta.showAndWait();
    }
}

