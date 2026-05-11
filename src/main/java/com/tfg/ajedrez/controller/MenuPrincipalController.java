package com.tfg.ajedrez.controller;

import com.tfg.ajedrez.persistence.GamePersistenceService;
import com.tfg.ajedrez.persistence.GameRecord;
import com.tfg.ajedrez.persistence.GameStats;
import com.tfg.ajedrez.persistence.UserProfile;
import com.tfg.ajedrez.state.AppSession;
import com.tfg.ajedrez.state.ThemeManager;
import com.tfg.ajedrez.util.AvatarUtil;
import com.tfg.ajedrez.util.SceneManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.List;

/**
 * Clase controladora para la vista del Menú Principal.
 * 
 * ARQUITECTURA UI/UX:
 * - Integración Interfaz-Lógica: Actúa como puente entre la sesión del usuario (AppSession) 
 *   y la presentación de los datos (estadísticas, historial, avatar).
 * - Gestión de Navegación: Centraliza las transiciones hacia las distintas funcionalidades 
 *   del juego (Nueva Partida, Cargar Partida, Perfil) utilizando el SceneManager.
 * - Coherencia Visual: Controla el menú lateral desplegable y la tematización dinámica (CSS),
 *   asegurando una experiencia de usuario (UX) fluida e intuitiva sin pérdida de contexto.
 */
public class MenuPrincipalController {

    @FXML private VBox menuDesplegable;
    @FXML private Region RegionMenu;

    @FXML private Label lblVictorias;
    @FXML private Label lblDerrotas;
    @FXML private Label lblTablas;
    @FXML private Button btnAvatar;
    @FXML private Button btnAjustes;

    @FXML private VBox botonHistorial;
    @FXML private VBox botonCargarPartida;

    @FXML private Label sinPartidas;

    @FXML private HBox cardPartida1;
    @FXML private Label resultado1;
    @FXML private Label oponente1;
    @FXML private Label fecha1;
    @FXML private Separator sep1;

    @FXML private HBox cardPartida2;
    @FXML private Label resultado2;
    @FXML private Label oponente2;
    @FXML private Label fecha2;
    @FXML private Separator sep2;

    @FXML private HBox cardPartida3;
    @FXML private Label resultado3;
    @FXML private Label oponente3;
    @FXML private Label fecha3;

    private List<GameRecord> historialActual;

    @FXML
    private void initialize() {
        aplicarAvatarUsuario();
        actualizarTextoTema();
        cargarResumenUsuario();
    }

    /**
     * Cierra la sesión actual y redirige al usuario a la pantalla de autenticación.
     * 
     * Enrutamiento Seguro: Delega la transición en el SceneManager, asegurando 
     * que el estado de la vista previa se limpie correctamente.
     */
    @FXML
    public void onVolver(ActionEvent event) {
        SceneManager.navegarA("/com/tfg/ajedrez/vista/login.fxml");
    }

    /**
     * Gestiona la micro-interacción del menú lateral desplegable.
     * 
     * Diseño UX: En lugar de cargar una nueva escena para el menú, se superpone 
     * un panel interactivo (overlay) sobre la vista actual, manteniendo el contexto visual 
     * del usuario para una experiencia más fluida.
     */
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
    public void onNuevaPartida() {
        SceneManager.navegarA("/com/tfg/ajedrez/vista/nueva-partida.fxml");
    }

    @FXML
    public void onPerfil(ActionEvent event) {
        SceneManager.navegarA("/com/tfg/ajedrez/vista/perfil.fxml");
    }

    @FXML
    public void onHistorial(MouseEvent event) {
        List<GameRecord> historial = GamePersistenceService.cargarHistorial(AppSession.getCurrentUserId());

        if (historial.isEmpty()) {
            mostrarAviso("Historial", "Sin partidas anteriores", "Aún no hay partidas guardadas para este usuario.");
            return;
        }

        mostrarHistorialBonito(historial);
    }

    @FXML
    public void onCargarPartida(MouseEvent event) {
        if (!GamePersistenceService.existePartidaEnCurso(AppSession.getCurrentUserId())) {
            return;
        }

        AppSession.requestLoadSavedGame();
        SceneManager.navegarA("/com/tfg/ajedrez/vista/partida.fxml");
    }

    /**
     * Alterna el tema visual de la aplicación (e.g., Modo Claro / Modo Oscuro).
     * 
     * Sistema de Tematización CSS: Propaga el cambio de tema a la raíz de la escena 
     * a través del ThemeManager. Esto actualiza dinámicamente las variables CSS 
     * globales sin necesidad de recargar los componentes FXML, garantizando coherencia visual.
     */
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

        historialActual = GamePersistenceService.cargarHistorial(userId);
        mostrarPartidasRecientes(historialActual);
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
            cardPartida1.setOnMouseClicked(e -> revisarPartida(historial.get(0)));
        }

        if (historial.size() > 1) {
            sep1.setVisible(true);
            sep1.setManaged(true);
            mostrarCard(cardPartida2, resultado2, oponente2, fecha2, historial.get(1));
            cardPartida2.setOnMouseClicked(e -> revisarPartida(historial.get(1)));
        }

        if (historial.size() > 2) {
            sep2.setVisible(true);
            sep2.setManaged(true);
            mostrarCard(cardPartida3, resultado3, oponente3, fecha3, historial.get(2));
            cardPartida3.setOnMouseClicked(e -> revisarPartida(historial.get(2)));
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

    private void ocultar(Node node) {
        node.setVisible(false);
        node.setManaged(false);
    }

    private void mostrarCard(HBox card, Label resultado, Label oponente, Label fecha, GameRecord record) {
        card.setVisible(true);
        card.setManaged(true);
        card.setStyle("-fx-cursor: hand;");

        resultado.getStyleClass().removeAll("resultado-victoria", "resultado-derrota", "resultado-tablas");
        resultado.getStyleClass().add(resultadoClass(record.resultado));
        resultado.setText(resultadoCorto(record.resultado));

        oponente.setText(record.oponente == null || record.oponente.isBlank() ? "Oponente" : record.oponente);
        fecha.setText(GamePersistenceService.fechaTexto(record.fechaIso));
    }

    private void mostrarHistorialBonito(List<GameRecord> historial) {
        Alert alert = new Alert(Alert.AlertType.NONE);
        alert.setTitle("Historial");
        alert.setHeaderText(null);

        VBox root = new VBox(16);
        root.setPadding(new Insets(22));
        root.setPrefWidth(520);
        root.getStyleClass().add("panel-historial-bonito");

        Label titulo = new Label("Historial de partidas");
        titulo.getStyleClass().add("historial-bonito-titulo");

        Label subtitulo = new Label("Selecciona una partida para revisar sus movimientos.");
        subtitulo.getStyleClass().add("historial-bonito-subtitulo");

        VBox lista = new VBox(10);

        for (GameRecord record : historial) {
            lista.getChildren().add(crearCardHistorial(record, alert));
        }

        ScrollPane scroll = new ScrollPane(lista);
        scroll.setFitToWidth(true);
        scroll.setPrefHeight(360);
        scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent; -fx-border-color: transparent;");

        Button cerrar = new Button("Cerrar");
        cerrar.getStyleClass().add("boton-fin-secundario");
        cerrar.setOnAction(e -> alert.close());

        HBox botones = new HBox(cerrar);
        botones.setAlignment(Pos.CENTER_RIGHT);

        root.getChildren().addAll(titulo, subtitulo, scroll, botones);

        alert.getDialogPane().setContent(root);
        alert.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        alert.getDialogPane().lookupButton(ButtonType.CLOSE).setVisible(false);

        alert.getDialogPane().getStylesheets().add(
                getClass().getResource("/com/tfg/ajedrez/css/styles.css").toExternalForm()
        );

        alert.showAndWait();
    }

    private HBox crearCardHistorial(GameRecord record, Alert alert) {
        HBox card = new HBox(14);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(12));
        card.getStyleClass().add("historial-card");

        Label badge = new Label(resultadoCorto(record.resultado));
        badge.setAlignment(Pos.CENTER);
        badge.setMinWidth(34);
        badge.setMinHeight(34);
        badge.getStyleClass().addAll("badge-resultado", resultadoClass(record.resultado));

        VBox textos = new VBox(4);
        textos.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(textos, javafx.scene.layout.Priority.ALWAYS);

        Label linea1 = new Label(resultadoTexto(record.resultado) + " contra " + textoSeguro(record.oponente, "Oponente"));
        linea1.getStyleClass().add("historial-card-titulo");

        Label linea2 = new Label(GamePersistenceService.fechaTexto(record.fechaIso)
                + " · " + textoSeguro(record.modoJuego, "Modo")
                + " · " + textoSeguro(record.colorJugador, "Color"));
        linea2.getStyleClass().add("historial-card-subtitulo");

        Label linea3 = new Label(textoSeguro(record.resumen, "Sin resumen"));
        linea3.getStyleClass().add("historial-card-resumen");
        linea3.setWrapText(true);

        textos.getChildren().addAll(linea1, linea2, linea3);

        Button revisar = new Button("Revisar");
        revisar.getStyleClass().add("boton-fin-principal");
        revisar.setDisable(!tieneDatosRevision(record));
        revisar.setOnAction(e -> {
            alert.close();
            revisarPartida(record);
        });

        card.getChildren().addAll(badge, textos, revisar);
        return card;
    }

    private void revisarPartida(GameRecord record) {
        if (!tieneDatosRevision(record)) {
            mostrarAviso("Historial", "Revisión no disponible", "Esta partida no tiene posiciones guardadas para revisión.");
            return;
        }

        AppSession.requestReviewGame(record);
        SceneManager.navegarA("/com/tfg/ajedrez/vista/partida.fxml");
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

    private void mostrarAviso(String titulo, String header, String texto) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(header);
        alert.setContentText(texto);
        alert.showAndWait();
    }

    private String textoSeguro(String texto, String defecto) {
        return texto == null || texto.isBlank() ? defecto : texto;
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