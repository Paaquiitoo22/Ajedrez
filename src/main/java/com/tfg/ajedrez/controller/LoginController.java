package com.tfg.ajedrez.controller;

import com.google.gson.JsonObject;
import com.tfg.ajedrez.auth.FirebaseAuthService;
import com.tfg.ajedrez.auth.GoogleAuthService;
import com.tfg.ajedrez.state.AppSession;
import com.tfg.ajedrez.state.ThemeManager;
import com.tfg.ajedrez.util.SceneManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * Controller de la vista del login.
 * 
 * Uso del patrón Modelo-Vista-Controlador (MVC).
 * La interfaz está separada de la presentación visual (FXML) de la lógica de
 * esta (el controlador).
 * Las transiciones entre pantallas se realizan a través de SceneManager,
 * garantizando un ciclo de vida óptimo de las escenas JavaFX.
 * 
 * SISTEMA DE AUTENTICACIÓN:
 * Actúa como nexo para el flujo de autenticación federada, delegando la
 * identidad
 * mediante Google OAuth 2.0 e integrando estas credenciales con Firebase
 * Identity Toolkit.
 */
public class LoginController {

    @FXML
    private VBox menuDesplegable;

    @FXML
    private Region RegionMenu;

    @FXML
    private Button btnAjustes;

    @FXML
    public void initialize() {
        actualizarTextoTema();
    }

    /**
     * Te permite entrar directamente a la app como invitado
     */
    @FXML
    public void onEntrar(ActionEvent event) throws Exception {
        AppSession.setGuestUser();
        SceneManager.navegarA("/com/tfg/ajedrez/vista/menu-principal.fxml");
    }

    /**
     * Ejecuta el flujo completo de autenticación segura.
     * 
     * 1. Delegación mediante Google OAuth 2.0: Obtiene un Token de Identidad
     * garantizando un estándar de seguridad de la industria sin manejar contraseñas
     * locales.
     * 
     * 2. Integración con Firebase: Intercambia el ID Token de Google en la API de
     * Firebase Identity Toolkit para obtener una sesión válida en la base de datos
     * del juego.
     * 
     * 3. Sincronización con la Interfaz: Navega al menú principal tras el éxito del
     * inicio de sesión.
     */
    @FXML
    public void onAutenticar() {
        try {
            System.out.println("[AUTH] Iniciando flujo de autenticación con Google...");

            // Primero se obtiene el ID Token desde Google
            String identifierToken = GoogleAuthService.authenticate();

            if (identifierToken != null) {
                System.out.println("[AUTH] Autenticación con Google exitosa. Sincronizando con Firebase...");

                // Validación e intercambio en Firebase
                JsonObject firebaseResponse = FirebaseAuthService.authenticateWithGoogle(identifierToken);
                AppSession.setFirebaseUser(firebaseResponse);

                String userDisplayName = AppSession.getCurrentDisplayName();
                System.out.println("[AUTH] Bienvenida/o: " + userDisplayName);

                // Navegación al menú principal
                SceneManager.navegarA("/com/tfg/ajedrez/vista/menu-principal.fxml");
            }

        } catch (Exception e) {
            System.err.println("[ERROR] Error durante el proceso de autenticación: " + e.getMessage());
            e.printStackTrace();
            mostrarAlertaError("Fallo de Autenticación", "No se pudo iniciar sesión con Google.");
        }
    }

    /**
     * Menú desplegable
     */
    @FXML
    public void onDesplegable(ActionEvent event) {
        menuDesplegable.setVisible(true);
        RegionMenu.setVisible(true);
    }

    @FXML
    public void onCerrarMenu() {
        menuDesplegable.setVisible(false);
        RegionMenu.setVisible(false);
    }

    /**
     * Alterna el tema visual (oscuro/claro)de la aplicación.
     * 
     * Sistema de Estilos y tematización: los estilos de la app están gobernados
     * por una hoja de estilos CSS controlada por el ThemeManager.
     * 
     */
    @FXML
    public void onCambiarTema(ActionEvent event) {
        ThemeManager.toggleTheme();
        ThemeManager.applyTheme(menuDesplegable.getScene().getRoot());
        actualizarTextoTema(); // pone el tema contrario
        onCerrarMenu();
    }

    private void mostrarAlertaError(String titulo, String contenido) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle("Error de Sistema");
        alerta.setHeaderText(titulo);
        alerta.setContentText(contenido);
        alerta.showAndWait();
    }

    /**
     * Actualiza el texto del botón de ajustes (tema)
     */
    private void actualizarTextoTema() {
        if (btnAjustes != null) {
            btnAjustes.setText(ThemeManager.getMenuLabel());
        }
    }
}
