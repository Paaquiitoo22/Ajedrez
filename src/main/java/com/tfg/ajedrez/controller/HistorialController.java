package com.tfg.ajedrez.controller;

import com.tfg.ajedrez.dao.PartidaDAO;
import com.tfg.ajedrez.model.PartidaGuardada;
import com.tfg.ajedrez.service.NavegacionService;
import com.tfg.ajedrez.service.PartidaSesionService;
import com.tfg.ajedrez.service.SesionService;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;

public class HistorialController {

    @FXML
    private VBox contenedorPartidas;

    @FXML
    private Label lblTitulo;

    private final PartidaDAO partidaDAO = new PartidaDAO();

    @FXML
    public void initialize() {
        if (SesionService.isInvitado()) {
            lblTitulo.setText("Historial de partidas (Invitado)");
        } else if (SesionService.getUsuarioActual() != null) {
            lblTitulo.setText("Historial de " + SesionService.getUsuarioActual().getNombre());
        } else {
            lblTitulo.setText("Historial de partidas");
        }

        cargarPartidas();
    }

    private void cargarPartidas() {
        contenedorPartidas.getChildren().clear();

        Integer usuarioId = null;
        if (!SesionService.isInvitado() && SesionService.getUsuarioActual() != null) {
            usuarioId = SesionService.getUsuarioActual().getId();
        }

        List<PartidaGuardada> partidas = partidaDAO.obtenerTodasLasPartidas(usuarioId);

        if (partidas.isEmpty()) {
            Label vacio = new Label("No hay partidas guardadas.");
            vacio.getStyleClass().add("move-item");
            contenedorPartidas.getChildren().add(vacio);
            return;
        }

        for (PartidaGuardada partida : partidas) {
            VBox tarjeta = new VBox(8);
            tarjeta.setPadding(new Insets(12));
            tarjeta.setStyle("-fx-background-color: rgba(255,255,255,0.08); -fx-background-radius: 14;");

            Label lblId = new Label("Partida #" + partida.getId());
            lblId.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");

            Label lblFecha = new Label("Fecha: " + partida.getFecha());
            lblFecha.getStyleClass().add("move-item");

            Label lblTurno = new Label("Turno guardado: " + partida.getTurno());
            lblTurno.getStyleClass().add("move-item");

            Label lblTiempo = new Label("Tiempo B/N: "
                    + formatear(partida.getTiempoBlancas()) + " / "
                    + formatear(partida.getTiempoNegras()));
            lblTiempo.getStyleClass().add("move-item");

            Label lblResultado = new Label("Estado: " + partida.getResultado());
            lblResultado.getStyleClass().add("move-item");

            HBox botones = new HBox(10);

            Button btnCargar = new Button("Cargar");
            btnCargar.getStyleClass().add("secondary-button");
            btnCargar.setPrefWidth(120);
            btnCargar.setOnAction(e -> cargarPartida(partida.getId()));

            Button btnBorrar = new Button("Borrar");
            btnBorrar.getStyleClass().add("ghost-button");
            btnBorrar.setPrefWidth(120);
            btnBorrar.setOnAction(e -> borrarPartida(partida.getId()));

            botones.getChildren().addAll(btnCargar, btnBorrar);

            tarjeta.getChildren().addAll(lblId, lblFecha, lblTurno, lblTiempo, lblResultado, botones);
            contenedorPartidas.getChildren().add(tarjeta);
        }
    }

    private void cargarPartida(int id) {
        PartidaGuardada partida = partidaDAO.obtenerPartidaPorId(id);

        if (partida == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("No se ha podido cargar la partida.");
            alert.showAndWait();
            return;
        }

        PartidaSesionService.setPartidaActual(partida);

        Stage stage = (Stage) contenedorPartidas.getScene().getWindow();
        NavegacionService.cambiarEscena(stage, "/com/tfg/ajedrez/fxml/partida.fxml", "Cargar partida");
    }

    private void borrarPartida(int id) {
        boolean ok = partidaDAO.borrarPartida(id);

        Alert alert = new Alert(ok ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR);
        alert.setHeaderText(null);
        alert.setContentText(ok ? "Partida borrada." : "No se ha podido borrar.");
        alert.showAndWait();

        if (ok) {
            cargarPartidas();
        }
    }

    @FXML
    public void volverMenu() {
        Stage stage = (Stage) contenedorPartidas.getScene().getWindow();
        NavegacionService.cambiarEscena(stage, "/com/tfg/ajedrez/fxml/menu.fxml", "Menú principal");
    }

    private String formatear(int segundos) {
        int min = segundos / 60;
        int seg = segundos % 60;
        return String.format("%02d:%02d", min, seg);
    }
}
