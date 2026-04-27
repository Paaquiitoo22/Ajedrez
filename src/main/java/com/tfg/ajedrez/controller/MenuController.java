package com.tfg.ajedrez.controller;

import com.tfg.ajedrez.dao.PartidaDAO;
import com.tfg.ajedrez.model.PartidaGuardada;
import com.tfg.ajedrez.service.ConfiguracionPartidaService;
import com.tfg.ajedrez.service.NavegacionService;
import com.tfg.ajedrez.service.PartidaSesionService;
import com.tfg.ajedrez.service.SesionService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class MenuController {

    @FXML
    private Button btnNuevaPartida;

    private final PartidaDAO partidaDAO = new PartidaDAO();

    @FXML
    public void nuevaPartida() {
        PartidaSesionService.limpiar();
        ConfiguracionPartidaService.limpiar();

        Stage stage = (Stage) btnNuevaPartida.getScene().getWindow();
        NavegacionService.cambiarEscena(stage, "/com/tfg/ajedrez/fxml/nueva-partida.fxml", "Nueva partida");
    }

    @FXML
    public void cargarPartida() {
        Integer usuarioId = null;

        if (!SesionService.isInvitado() && SesionService.getUsuarioActual() != null) {
            usuarioId = SesionService.getUsuarioActual().getId();
        }

        PartidaGuardada partida = partidaDAO.obtenerUltimaPartida(usuarioId);

        if (partida == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText(null);
            alert.setContentText("No hay partidas guardadas.");
            alert.showAndWait();
            return;
        }

        PartidaSesionService.setPartidaActual(partida);

        Stage stage = (Stage) btnNuevaPartida.getScene().getWindow();
        NavegacionService.cambiarEscena(stage, "/com/tfg/ajedrez/fxml/partida.fxml", "Cargar partida");
    }

    @FXML
    public void historial() {
        Stage stage = (Stage) btnNuevaPartida.getScene().getWindow();
        NavegacionService.cambiarEscena(stage, "/com/tfg/ajedrez/fxml/historial.fxml", "Historial");
    }

    @FXML
    public void estadisticas() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText("Estadísticas pendiente.");
        alert.showAndWait();
    }

    @FXML
    public void comoJugar() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText("Cómo jugar");
        alert.setContentText("Selecciona una pieza y luego una casilla válida para mover.");
        alert.showAndWait();
    }

    @FXML
    public void cerrarSesion() {
        SesionService.cerrarSesion();
        PartidaSesionService.limpiar();
        ConfiguracionPartidaService.limpiar();

        Stage stage = (Stage) btnNuevaPartida.getScene().getWindow();
        NavegacionService.cambiarEscena(stage, "/com/tfg/ajedrez/fxml/login.fxml", "Login");
    }
}
