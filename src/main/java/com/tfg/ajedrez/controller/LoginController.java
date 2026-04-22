package com.tfg.ajedrez.controller;

import com.google.gson.JsonObject;
import com.tfg.ajedrez.auth.FirebaseAuthService;
import com.tfg.ajedrez.auth.GoogleAuthService;
import com.tfg.ajedrez.util.SceneManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;

/**
 * Clase controladora vinculada a la capa de vista del módulo de inicio de sesión (Login).
 * 
 * Implementa el patrón Modelo-Vista-Controlador (MVC) para la gestión del flujo 
 * de autenticación federada. Actúa como el nexo entre los eventos de la interfaz 
 * de usuario en JavaFX y los servicios de infraestructura de Google y Firebase.
 */
public class LoginController {

    /**
     * Gestiona la interrupción de eventos para el acceso directo a la aplicación 
     * en modo invitado o bypass de seguridad.
     * 
     * @param event Instancia del evento de acción capturado por el agente de usuario.
     * @throws Exception Si ocurre una anomalía en la transición hacia la escena de destino.
     */
    @FXML
    public void onEntrar(ActionEvent event) throws Exception {
        SceneManager.navegarA("/com/tfg/ajedrez/vista/menu-principal.fxml");
    }

    /**
     * Ejecuta el flujo completo de autenticación de terceros.
     * 1. Solicita autorización a Google.
     * 2. Intercambia el ID Token de Google por una sesión en Firebase.
     * 3. Navega al menú principal tras el éxito.
     */
    @FXML
    public void onAutenticar() {
        try {
            System.out.println("[AUTH] Iniciando flujo de autenticación con Google...");
            
            // Paso 1: Obtención del ID Token desde Google
            String identifierToken = GoogleAuthService.authenticate();
            
            if (identifierToken != null) {
                System.out.println("[AUTH] Autenticación con Google exitosa. Sincronizando con Firebase...");
                
                // Paso 2: Validación e intercambio en Firebase
                JsonObject firebaseResponse = FirebaseAuthService.authenticateWithGoogle(identifierToken);
                
                String userDisplayName = firebaseResponse.get("displayName").getAsString();
                System.out.println("[AUTH] Bienvenida/o: " + userDisplayName);

                // Paso 3: Navegación al entorno principal
                SceneManager.navegarA("/com/tfg/ajedrez/vista/menu-principal.fxml");
            }

        } catch (Exception e) {
            System.err.println("[ERROR] Error durante el proceso de autenticación: " + e.getMessage());
            e.printStackTrace();
            mostrarAlertaError("Fallo de Autenticación", "No se pudo iniciar sesión con Google.");
        }
    }

    /**
     * Presenta un diálogo de error al usuario en la interfaz gráfica.
     */
    private void mostrarAlertaError(String titulo, String contenido) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle("Error de Sistema");
        alerta.setHeaderText(titulo);
        alerta.setContentText(contenido);
        alerta.showAndWait();
    }
}
