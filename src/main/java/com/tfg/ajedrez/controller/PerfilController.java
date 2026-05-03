package com.tfg.ajedrez.controller;

import com.tfg.ajedrez.persistence.GamePersistenceService;
import com.tfg.ajedrez.persistence.UserProfile;
import com.tfg.ajedrez.state.AppSession;
import com.tfg.ajedrez.state.ThemeManager;
import com.tfg.ajedrez.util.AvatarUtil;
import com.tfg.ajedrez.util.SceneManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class PerfilController {

    @FXML private VBox menuDesplegable;
    @FXML private Region RegionMenu;
    @FXML private Button btnAjustes;

    @FXML private Label avatarPerfil;
    @FXML private Label lblNombre;
    @FXML private Label lblEmail;

    @FXML
    private void initialize() {
        lblNombre.setText(nombreUsuario());
        lblEmail.setText(AppSession.getCurrentEmail() == null ? "Sin email disponible" : AppSession.getCurrentEmail());
        actualizarTextoTema();
        aplicarAvatar();
    }

    @FXML
    public void onVolver() {
        SceneManager.navegarA("/com/tfg/ajedrez/vista/menu-principal.fxml");
    }

    @FXML
    public void onSubirFoto(ActionEvent event) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Seleccionar foto de perfil");
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Imagenes", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        Stage stage = (Stage) avatarPerfil.getScene().getWindow();
        File selected = chooser.showOpenDialog(stage);
        if (selected == null) {
            return;
        }

        GamePersistenceService.guardarFotoPerfil(AppSession.getCurrentUserId(), selected.toPath());
        aplicarAvatar();
    }

    @FXML
    public void onQuitarFoto(ActionEvent event) {
        GamePersistenceService.eliminarFotoPerfil(AppSession.getCurrentUserId());
        aplicarAvatar();
    }

    @FXML
    public void onDesplegable(ActionEvent event) {
        boolean visible = !menuDesplegable.isVisible();
        menuDesplegable.setVisible(visible);
        RegionMenu.setVisible(visible);
    }

    @FXML
    public void onCerrarMenu() {
        if (menuDesplegable.isVisible() && RegionMenu.isVisible()) {
            menuDesplegable.setVisible(false);
            RegionMenu.setVisible(false);
        }
    }

    @FXML
    public void onCambiarTema(ActionEvent event) {
        ThemeManager.toggleTheme();
        if (menuDesplegable.getScene() != null) {
            ThemeManager.applyTheme(menuDesplegable.getScene().getRoot());
        }
        actualizarTextoTema();
        onCerrarMenu();
    }

    @FXML
    public void onCerrarSesion(ActionEvent event) {
        SceneManager.navegarA("/com/tfg/ajedrez/vista/login.fxml");
    }

    private void aplicarAvatar() {
        UserProfile profile = GamePersistenceService.cargarPerfil(AppSession.getCurrentUserId());
        AvatarUtil.aplicarAvatar(avatarPerfil, AppSession.getCurrentInitials(), profile.photoPath, 96);
    }

    private void actualizarTextoTema() {
        btnAjustes.setText(ThemeManager.getMenuLabel());
    }

    private String nombreUsuario() {
        String displayName = AppSession.getCurrentDisplayName();
        if (displayName != null && !displayName.isBlank()) {
            return displayName;
        }

        String email = AppSession.getCurrentEmail();
        if (email != null && !email.isBlank()) {
            return email;
        }

        return "Usuario";
    }
}
