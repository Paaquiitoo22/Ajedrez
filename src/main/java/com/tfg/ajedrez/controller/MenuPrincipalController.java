package com.tfg.ajedrez.controller;

import com.tfg.ajedrez.util.SceneManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class MenuPrincipalController {

    @FXML VBox menuDesplegable;
    @FXML Region RegionMenu;

    // ── Partidas recientes ────────────────────────────────────────────────────
    // TODO: llamar a cargarPartidasRecientes() desde initialize() cuando el
    //       backend SQL esté disponible. Las tarjetas están ocultas (visible=false,
    //       managed=false) hasta que lleguen datos; sinPartidas se oculta al mostrarlas.

    @FXML private Label sinPartidas;

    @FXML private HBox cardPartida1;
    @FXML private Label resultado1;
    @FXML private Label oponente1;
    @FXML private Label fecha1;

    @FXML private HBox cardPartida2;
    @FXML private Label resultado2;
    @FXML private Label oponente2;
    @FXML private Label fecha2;

    @FXML private HBox cardPartida3;
    @FXML private Label resultado3;
    @FXML private Label oponente3;
    @FXML private Label fecha3;

    // ── Menú desplegable ─────────────────────────────────────────────────────

    /**
     * Cierra la sesión actual y redirige al usuario a la pantalla de autenticación.
     * 
     * Enrutamiento Seguro: Delega la transición en el SceneManager, asegurando 
     * que el estado de la vista previa se limpie correctamente.
     */
    @FXML
    public void onVolver(ActionEvent event) throws Exception {
        SceneManager.navegarA("/com/tfg/ajedrez/vista/login.fxml");
    }

    /** Abre/cierra el menú desplegable. */
    @FXML
    public void onDesplegable(ActionEvent event) throws Exception {
        if (menuDesplegable.isVisible()) {
            menuDesplegable.setVisible(false);
            RegionMenu.setVisible(false);
        }
    }

    /** Cierra el menú al pulsar fuera de él. */
    @FXML
    public void onCerrarMenu() {
        if (menuDesplegable.isVisible() && RegionMenu.isVisible()) {
            menuDesplegable.setVisible(false);
            RegionMenu.setVisible(false);
        }

        GameRecord record = historial.get(indice);
        if (!tieneDatosRevision(record)) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Historial");
            alert.setHeaderText("Revision no disponible");
            alert.setContentText("Esta partida no tiene posiciones guardadas para revision.");
            alert.showAndWait();
            return;
        }

        AppSession.requestReviewGame(record);
        SceneManager.navegarA("/com/tfg/ajedrez/vista/partida.fxml");
    }

    @FXML
    public void onCargarPartida(MouseEvent event) {
        if (!GamePersistenceService.existePartidaEnCurso(AppSession.getCurrentUserId())) {
            return;
        }

        AppSession.requestLoadSavedGame();
        SceneManager.navegarA("/com/tfg/ajedrez/vista/partida.fxml");
    }

    /** Navega a la pantalla de nueva partida. */
    @FXML
    public void onCambiarTema(ActionEvent event) {
        ThemeManager.toggleTheme();
        if (menuDesplegable.getScene() != null) {
            ThemeManager.applyTheme(menuDesplegable.getScene().getRoot());
        }
        actualizarTextoTema();
        onCerrarMenu();
    }

    private void cargarResumenUsuario() {
        String userId = AppSession.getCurrentUserId();

        GameStats stats = GamePersistenceService.calcularEstadisticas(userId);
        lblVictorias.setText(String.valueOf(stats.victorias()));
        lblDerrotas.setText(String.valueOf(stats.derrotas()));
        lblTablas.setText(String.valueOf(stats.tablas()));

        mostrarPartidasRecientes(GamePersistenceService.cargarHistorial(userId));
        configurarBotonCargar(GamePersistenceService.existePartidaEnCurso(userId));
    }

    /**
     * Integra la información de la sesión actual con los componentes visuales.
     * 
     * Personalización UI: Extrae la imagen o las iniciales del perfil del usuario logueado 
     * y las inyecta en el componente gráfico del avatar, mejorando la inmersión del jugador.
     */
    private void aplicarAvatarUsuario() {
        UserProfile profile = GamePersistenceService.cargarPerfil(AppSession.getCurrentUserId());
        AvatarUtil.aplicarAvatar(btnAvatar, AppSession.getCurrentInitials(), profile.photoPath, 36);
    }

    private void mostrarPartidasRecientes(List<GameRecord> historial) {
        ocultarPartidasRecientes();

        if (historial.isEmpty()) {
            sinPartidas.setVisible(true);
            sinPartidas.setManaged(true);
            return;
        }

        sinPartidas.setVisible(false);
        sinPartidas.setManaged(false);

        if (!historial.isEmpty()) {
            mostrarCard(cardPartida1, resultado1, oponente1, fecha1, historial.get(0));
        }
        if (historial.size() > 1) {
            sep1.setVisible(true);
            sep1.setManaged(true);
            mostrarCard(cardPartida2, resultado2, oponente2, fecha2, historial.get(1));
        }
        if (historial.size() > 2) {
            sep2.setVisible(true);
            sep2.setManaged(true);
            mostrarCard(cardPartida3, resultado3, oponente3, fecha3, historial.get(2));
        }
    }

    private void ocultarPartidasRecientes() {
        sinPartidas.setVisible(false);
        sinPartidas.setManaged(false);

        ocultar(cardPartida1);
        ocultar(cardPartida2);
        ocultar(cardPartida3);
        ocultar(sep1);
        ocultar(sep2);
    }

    private void ocultar(javafx.scene.Node node) {
        node.setVisible(false);
        node.setManaged(false);
    }

    private void mostrarCard(HBox card, Label resultado, Label oponente, Label fecha, GameRecord record) {
        card.setVisible(true);
        card.setManaged(true);

        resultado.getStyleClass().removeAll("resultado-victoria", "resultado-derrota", "resultado-tablas");
        resultado.getStyleClass().add(resultadoClass(record.resultado));
        resultado.setText(resultadoCorto(record.resultado));

        oponente.setText(record.oponente == null || record.oponente.isBlank() ? "Oponente" : record.oponente);
        fecha.setText(GamePersistenceService.fechaTexto(record.fechaIso));
    }

    private void configurarBotonCargar(boolean hayPartida) {
        botonCargarPartida.setDisable(!hayPartida);
        botonCargarPartida.getStyleClass().remove("deshabilitado");
        if (!hayPartida) {
            botonCargarPartida.getStyleClass().add("deshabilitado");
        }
    }

    private void actualizarTextoTema() {
        btnAjustes.setText(ThemeManager.getMenuLabel());
    }

    private boolean tieneDatosRevision(GameRecord record) {
        if (record == null) {
            return false;
        }
        if (record.snapshotsRevision != null && !record.snapshotsRevision.isEmpty()) {
            return true;
        }
        return record.boardState != null && !record.boardState.isBlank();
    }

    private String formatearPartida(GameRecord record) {
        return GamePersistenceService.fechaTexto(record.fechaIso)
                + " - " + resultadoTexto(record.resultado)
                + " - " + (record.oponente == null ? "Oponente" : record.oponente)
                + " - " + (record.modoJuego == null ? "" : record.modoJuego);
    }

    private String resultadoTexto(String resultado) {
        return switch (resultado) {
            case GameRecord.RESULTADO_VICTORIA -> "Victoria";
            case GameRecord.RESULTADO_DERROTA -> "Derrota";
            case GameRecord.RESULTADO_TABLAS -> "Tablas";
            default -> "Resultado desconocido";
        };
    }

    private String resultadoCorto(String resultado) {
        return switch (resultado) {
            case GameRecord.RESULTADO_DERROTA -> "D";
            case GameRecord.RESULTADO_TABLAS -> "T";
            default -> "V";
        };
    }

    private String resultadoClass(String resultado) {
        return switch (resultado) {
            case GameRecord.RESULTADO_DERROTA -> "resultado-derrota";
            case GameRecord.RESULTADO_TABLAS -> "resultado-tablas";
            default -> "resultado-victoria";
        };
    }
}
