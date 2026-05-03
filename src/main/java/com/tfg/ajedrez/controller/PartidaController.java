package com.tfg.ajedrez.controller;

import com.tfg.ajedrez.clock.ChessClock;
import com.tfg.ajedrez.clock.GameState;
import com.tfg.ajedrez.persistence.GamePersistenceService;
import com.tfg.ajedrez.persistence.GameRecord;
import com.tfg.ajedrez.persistence.GameSnapshot;
import com.tfg.ajedrez.persistence.SavedGame;
import com.tfg.ajedrez.persistence.UserProfile;
import com.tfg.ajedrez.model.ColorPieza;
import com.tfg.ajedrez.model.MovimientoInfo;
import com.tfg.ajedrez.model.Pieza;
import com.tfg.ajedrez.model.Posicion;
import com.tfg.ajedrez.model.Tablero;
import com.tfg.ajedrez.model.TipoPieza;
import com.tfg.ajedrez.state.AppSession;
import com.tfg.ajedrez.state.NuevaPartidaSettings;
import com.tfg.ajedrez.state.ThemeManager;
import com.tfg.ajedrez.util.AvatarUtil;
import com.tfg.ajedrez.util.SceneManager;
import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.UUID;

/**
 * Controlador de la pantalla de partida.
 *
 * Integra la lógica de ajedrez (paquete model) con el tablero gráfico,
 * el reloj y el panel de historial.
 */
public class PartidaController implements Initializable {

    // ── Inyecciones FXML ─────────────────────────────────────────────────────

    @FXML private GridPane tablero;

    @FXML private Label avatarNegras;
    @FXML private Label nombreNegras;
    @FXML private Label timerNegras;

    @FXML private Label avatarBlancas;
    @FXML private Label nombreBlancas;
    @FXML private Label timerBlancas;
    @FXML private Label lblCuentaAtras;

    @FXML private GridPane historialMovimientos;
    @FXML private VBox panelRevision;
    @FXML private Label lblRevision;
    @FXML private Button btnRevisionAnterior;
    @FXML private Button btnRevisionSiguiente;
    @FXML private Button btnRevisionFinal;
    @FXML private VBox menuDesplegable;
    @FXML private Region RegionMenu;
    @FXML private Button btnAjustes;
    @FXML private Button btnPausa;

    // ── Constantes del tablero ────────────────────────────────────────────────

    private static final int    CELDA        = 56;
    private static final String COLOR_CLARA  = "#8ca2ad";
    private static final String COLOR_OSCURA = "#4a6f8a";
    private static final String COLOR_SELEC  = "#facc15";

    private static final int TIEMPO_INICIAL_SEG = 600; // 10 minutos por jugador
    private static final Random RANDOM = new Random();

    // ── Estado de la partida ─────────────────────────────────────────────────

    private Tablero modelo;
    private ColorPieza turnoActual = ColorPieza.BLANCA;

    private Integer filaSeleccionada = null;
    private Integer colSeleccionada  = null;
    private List<Posicion> movimientosPosibles = new ArrayList<>();

    private ChessClock reloj;
    private Timeline relojTick;

    private int contadorMovimientos = 0;
    private boolean partidaTerminada = false;
    private String userId;
    private NuevaPartidaSettings settings;
    private SavedGame partidaCargada;
    private GameRecord partidaRevision;
    private ColorPieza colorUsuario = ColorPieza.BLANCA;
    private List<String> historialTexto = new ArrayList<>();
    private List<GameSnapshot> snapshotsRevision = new ArrayList<>();
    private GameSnapshot snapshotDeshacer;
    private PauseTransition turnoIAPendiente;
    private boolean deshacerUsado = false;
    private boolean pausaActiva = false;
    private boolean modoRevision = false;
    private boolean resultadoRegistrado = false;
    private boolean relojIniciado = false;
    private boolean cuentaAtrasActiva = false;
    private int indiceRevision = 0;
    private Timeline cuentaAtrasTimeline;

    // ── Ciclo de vida ─────────────────────────────────────────────────────────

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        userId = AppSession.getCurrentUserId();
        partidaRevision = AppSession.consumeReviewGameRequest();
        if (partidaRevision != null) {
            inicializarRevisionDesdeHistorial();
            return;
        }

        settings = AppSession.getNuevaPartidaSettings().copy();
        partidaCargada = AppSession.consumeLoadSavedGameRequest()
                ? GamePersistenceService.cargarPartidaEnCurso(userId).orElse(null)
                : null;

        if (partidaCargada != null) {
            aplicarPartidaCargada();
        } else {
            colorUsuario = resolverColorUsuario(settings.getColorJugador());
        }

        modelo = new Tablero();
        if (partidaCargada != null && partidaCargada.boardState != null && !partidaCargada.boardState.isBlank()) {
            modelo.importarEstado(partidaCargada.boardState);
        }

        configurarJugadores();
        dibujarTablero();
        prepararReloj();
        inicializarSnapshotsRevision();
        restaurarHistorialMovimientos();
        actualizarEstilosTimer();
        actualizarTextoTema();
        actualizarEstadoPausa();
        guardarPartidaEnCurso();
        if (partidaCargada != null) {
            iniciarCuentaAtrasCarga();
        } else if (esTurnoIA()) {
            programarTurnoIA();
        }
    }

    private void aplicarPartidaCargada() {
        if (partidaCargada.modoJuego != null) {
            settings.setModoJuego(partidaCargada.modoJuego);
        }
        if (partidaCargada.tipoPartida != null) {
            settings.setTipoPartida(partidaCargada.tipoPartida);
        }
        if (partidaCargada.colorJugador != null) {
            settings.setColorJugador(partidaCargada.colorJugador);
        }
        if (partidaCargada.tiempoInicialSegundos > 0) {
            settings.setTiempoSegundos(partidaCargada.tiempoInicialSegundos);
        }

        turnoActual = partidaCargada.whiteTurn ? ColorPieza.BLANCA : ColorPieza.NEGRA;
        contadorMovimientos = partidaCargada.contadorMovimientos;
        colorUsuario = resolverColorUsuario(settings.getColorJugador());
        historialTexto = partidaCargada.historialMovimientos == null
                ? new ArrayList<>()
                : new ArrayList<>(partidaCargada.historialMovimientos);
        deshacerUsado = partidaCargada.deshacerUsado;
        snapshotDeshacer = partidaCargada.snapshotDeshacer;
        relojIniciado = partidaCargada.relojIniciado || partidaCargada.contadorMovimientos > 0;
        snapshotsRevision = partidaCargada.snapshotsRevision == null
                ? new ArrayList<>()
                : new ArrayList<>(partidaCargada.snapshotsRevision);
    }

    private void inicializarRevisionDesdeHistorial() {
        settings = NuevaPartidaSettings.defaults();
        aplicarSettingsDesdeRecord(partidaRevision);
        colorUsuario = resolverColorUsuario(settings.getColorJugador());
        historialTexto = partidaRevision.historialMovimientos == null
                ? new ArrayList<>()
                : new ArrayList<>(partidaRevision.historialMovimientos);
        snapshotsRevision = partidaRevision.snapshotsRevision == null
                ? new ArrayList<>()
                : new ArrayList<>(partidaRevision.snapshotsRevision);

        if (snapshotsRevision.isEmpty() && partidaRevision.boardState != null && !partidaRevision.boardState.isBlank()) {
            GameSnapshot snapshot = new GameSnapshot();
            snapshot.boardState = partidaRevision.boardState;
            snapshot.whiteTurn = true;
            snapshot.whiteSeconds = settings.getTiempoSegundos();
            snapshot.blackSeconds = settings.getTiempoSegundos();
            snapshot.contadorMovimientos = historialTexto.size();
            snapshot.historialMovimientos = new ArrayList<>(historialTexto);
            snapshotsRevision.add(snapshot);
        }

        modelo = new Tablero();
        inicializarSnapshotsRevision();
        GameSnapshot snapshotFinal = snapshotsRevision.get(snapshotsRevision.size() - 1);
        modelo.importarEstado(snapshotFinal.boardState);
        turnoActual = snapshotFinal.whiteTurn ? ColorPieza.BLANCA : ColorPieza.NEGRA;
        contadorMovimientos = snapshotFinal.contadorMovimientos;
        if (historialTexto.isEmpty() && snapshotFinal.historialMovimientos != null) {
            historialTexto = new ArrayList<>(snapshotFinal.historialMovimientos);
        }

        configurarJugadores();
        dibujarTablero();
        prepararReloj();
        restaurarHistorialMovimientos();
        actualizarEstilosTimer();
        actualizarTextoTema();
        partidaTerminada = true;
        activarRevision();
    }

    private void aplicarSettingsDesdeRecord(GameRecord record) {
        if (record == null) {
            return;
        }

        if (record.tipoPartidaCodigo != null && !record.tipoPartidaCodigo.isBlank()) {
            settings.setTipoPartida(record.tipoPartidaCodigo);
        } else if ("Local".equalsIgnoreCase(record.tipoPartida)) {
            settings.setTipoPartida(NuevaPartidaSettings.TIPO_LOCAL);
        }

        if (record.modoJuegoCodigo != null && !record.modoJuegoCodigo.isBlank()) {
            settings.setModoJuego(record.modoJuegoCodigo);
        }

        if (record.colorJugadorCodigo != null && !record.colorJugadorCodigo.isBlank()) {
            settings.setColorJugador(record.colorJugadorCodigo);
        } else if ("Negras".equalsIgnoreCase(record.colorJugador)) {
            settings.setColorJugador(NuevaPartidaSettings.COLOR_NEGRAS);
        }

        if (record.tiempoInicialSegundos > 0) {
            settings.setTiempoSegundos(record.tiempoInicialSegundos);
        }
    }

    // ── Tablero ───────────────────────────────────────────────────────────────

    /** Limpia y vuelve a pintar las 64 casillas según el estado actual del modelo. */
    private void dibujarTablero() {
        tablero.getChildren().clear();

        for (int fila = 0; fila < 8; fila++) {
            for (int col = 0; col < 8; col++) {
                tablero.add(crearCasilla(fila, col), col, fila);
            }
        }
    }

    /**
     * Crea una casilla con su fondo, indicadores (selección, movimientos posibles)
     * y la pieza correspondiente del modelo.
     */
    private StackPane crearCasilla(int fila, int col) {
        StackPane casilla = new StackPane();
        casilla.setAlignment(Pos.CENTER);
        casilla.setPrefSize(CELDA, CELDA);
        casilla.setMinSize(CELDA, CELDA);
        casilla.setMaxSize(CELDA, CELDA);

        // Fondo (Rectangle para poder dibujarle un borde si está seleccionada)
        boolean esClara = (fila + col) % 2 == 0;
        Rectangle fondo = new Rectangle(CELDA, CELDA);
        fondo.setFill(Color.web(esClara ? COLOR_CLARA : COLOR_OSCURA));

        if (filaSeleccionada != null && colSeleccionada != null
                && fila == filaSeleccionada && col == colSeleccionada) {
            fondo.setStroke(Color.web(COLOR_SELEC));
            fondo.setStrokeWidth(3);
        }

        casilla.getChildren().add(fondo);

        // Indicador de movimiento posible (punto verde / aro rojo en captura)
        if (esMovimientoPosible(fila, col)) {
            Pieza piezaDestino = modelo.getPieza(fila, col);
            Circle indicador;

            if (piezaDestino != null) {
                indicador = new Circle(CELDA / 2.0 - 4);
                indicador.setFill(Color.TRANSPARENT);
                indicador.setStroke(Color.rgb(220, 38, 38, 0.65));
                indicador.setStrokeWidth(3);
            } else {
                indicador = new Circle(8);
                indicador.setFill(Color.rgb(34, 197, 94, 0.55));
            }
            casilla.getChildren().add(indicador);
        }

        // Pieza
        Pieza pieza = modelo.getPieza(fila, col);
        if (pieza != null) {
            ImageView iv = crearImagenPieza(nombreImagen(pieza));
            if (iv != null) casilla.getChildren().add(iv);
        }

        // Click handler
        final int f = fila;
        final int c = col;
        casilla.setOnMouseClicked(e -> manejarClick(f, c));

        return casilla;
    }

    /**
     * Carga un PNG de img/piezas/ y lo devuelve como ImageView listo para usar.
     */
    private ImageView crearImagenPieza(String nombreArchivo) {
        String ruta = "/com/tfg/ajedrez/img/piezas/" + nombreArchivo + ".png";
        try (InputStream stream = getClass().getResourceAsStream(ruta)) {
            if (stream == null) {
                System.err.println("[PIEZA] Archivo no encontrado: " + ruta);
                return null;
            }
            Image imagen = new Image(stream);
            ImageView iv = new ImageView(imagen);
            iv.setFitWidth(CELDA - 6);
            iv.setFitHeight(CELDA - 6);
            iv.setPreserveRatio(true);
            iv.setSmooth(true);
            StackPane.setAlignment(iv, Pos.CENTER);
            return iv;
        } catch (Exception e) {
            System.err.println("[PIEZA] Error cargando: " + ruta + " — " + e.getMessage());
            return null;
        }
    }

    /** Convierte (TipoPieza, ColorPieza) en el nombre de archivo PNG sin extensión. */
    private String nombreImagen(Pieza pieza) {
        String tipo = switch (pieza.getTipo()) {
            case REY     -> "rey";
            case DAMA    -> "reina";
            case TORRE   -> "torre";
            case ALFIL   -> "alfil";
            case CABALLO -> "caballo";
            case PEON    -> "peon";
        };
        String sufijo = pieza.getColor() == ColorPieza.BLANCA ? "B" : "N";
        return tipo + sufijo;
    }

    private boolean esMovimientoPosible(int fila, int col) {
        for (Posicion p : movimientosPosibles) {
            if (p.getFila() == fila && p.getColumna() == col) return true;
        }
        return false;
    }

    // ── Interacción ───────────────────────────────────────────────────────────

    private void manejarClick(int fila, int col) {
        if (partidaTerminada) return;
        if (pausaActiva || modoRevision || cuentaAtrasActiva) return;
        if (esTurnoIA()) return;

        Pieza piezaClicada = modelo.getPieza(fila, col);

        // Caso A: no hay nada seleccionado → seleccionar si es del color en turno
        if (filaSeleccionada == null) {
            if (piezaClicada != null && piezaClicada.getColor() == turnoActual) {
                seleccionar(fila, col);
            }
            return;
        }

        // Caso B: click en la misma casilla → deseleccionar
        if (filaSeleccionada == fila && colSeleccionada == col) {
            limpiarSeleccion();
            return;
        }

        // Caso C: click en otra pieza propia → cambiar de selección
        if (piezaClicada != null && piezaClicada.getColor() == turnoActual) {
            seleccionar(fila, col);
            return;
        }

        // Caso D: intento de movimiento al destino
        GameSnapshot snapshotAntesMovimiento = puedePrepararDeshacer() ? crearSnapshot() : null;
        MovimientoInfo mov = modelo.moverPieza(filaSeleccionada, colSeleccionada, fila, col);
        if (mov != null) {
            registrarMovimientoEnHistorial(mov);
            cambiarTurno();
            agregarSnapshotRevision();
            comprobarFinDePartida();
            limpiarSeleccion();

            if (!partidaTerminada && snapshotAntesMovimiento != null) {
                snapshotDeshacer = snapshotAntesMovimiento;
                actualizarEstadoPausa();
            }

            if (!partidaTerminada && esTurnoIA()) {
                programarTurnoIA();
            } else if (!partidaTerminada) {
                guardarPartidaEnCurso();
            }
            return;
        }
        limpiarSeleccion();
    }

    private void seleccionar(int fila, int col) {
        filaSeleccionada = fila;
        colSeleccionada  = col;
        movimientosPosibles = modelo.obtenerMovimientosValidos(fila, col);
        dibujarTablero();
    }

    private void limpiarSeleccion() {
        filaSeleccionada = null;
        colSeleccionada  = null;
        movimientosPosibles.clear();
        dibujarTablero();
    }

    private void cambiarTurno() {
        turnoActual = (turnoActual == ColorPieza.BLANCA) ? ColorPieza.NEGRA : ColorPieza.BLANCA;
        if (reloj != null) {
            reloj.switchTurn();
        }
        actualizarEstilosTimer();
    }

    private boolean esTurnoIA() {
        return NuevaPartidaSettings.TIPO_CONTRA_IA.equals(settings.getTipoPartida())
                && turnoActual != colorUsuario
                && !partidaTerminada;
    }

    private boolean puedePrepararDeshacer() {
        return NuevaPartidaSettings.TIPO_CONTRA_IA.equals(settings.getTipoPartida())
                && !deshacerUsado
                && !partidaTerminada;
    }

    private boolean puedeDeshacer() {
        return NuevaPartidaSettings.TIPO_CONTRA_IA.equals(settings.getTipoPartida())
                && !deshacerUsado
                && snapshotDeshacer != null
                && !partidaTerminada;
    }

    private void programarTurnoIA() {
        if (!esTurnoIA() || pausaActiva || modoRevision) {
            return;
        }

        guardarPartidaEnCurso();
        int segundos = 1 + RANDOM.nextInt(5);
        turnoIAPendiente = new PauseTransition(Duration.seconds(segundos));
        turnoIAPendiente.setOnFinished(event -> {
            turnoIAPendiente = null;
            if (esTurnoIA() && !pausaActiva && !modoRevision) {
                jugarTurnoIA();
            }
        });
        turnoIAPendiente.play();
    }

    private void cancelarTurnoIAPendiente() {
        if (turnoIAPendiente != null) {
            turnoIAPendiente.stop();
            turnoIAPendiente = null;
        }
    }

    private void jugarTurnoIA() {
        if (!esTurnoIA() || pausaActiva || modoRevision) {
            return;
        }

        List<int[]> movimientos = new ArrayList<>();

        for (int fila = 0; fila < 8; fila++) {
            for (int col = 0; col < 8; col++) {
                Pieza pieza = modelo.getPieza(fila, col);
                if (pieza == null || pieza.getColor() != turnoActual) {
                    continue;
                }

                for (Posicion destino : modelo.obtenerMovimientosValidos(fila, col)) {
                    movimientos.add(new int[]{fila, col, destino.getFila(), destino.getColumna()});
                }
            }
        }

        if (movimientos.isEmpty()) {
            comprobarFinDePartida();
            return;
        }

        int[] movimiento = movimientos.get(RANDOM.nextInt(movimientos.size()));
        MovimientoInfo mov = modelo.moverPieza(movimiento[0], movimiento[1], movimiento[2], movimiento[3]);
        if (mov == null) {
            return;
        }

        registrarMovimientoEnHistorial(mov);
        cambiarTurno();
        agregarSnapshotRevision();
        comprobarFinDePartida();
        limpiarSeleccion();

        if (!partidaTerminada) {
            guardarPartidaEnCurso();
        }
    }

    // ── Reloj ────────────────────────────────────────────────────────────────

    private void deshacerUltimoMovimiento() {
        if (!puedeDeshacer()) {
            return;
        }

        cancelarTurnoIAPendiente();
        GameSnapshot snapshot = snapshotDeshacer;
        modelo.importarEstado(snapshot.boardState);
        turnoActual = snapshot.whiteTurn ? ColorPieza.BLANCA : ColorPieza.NEGRA;
        contadorMovimientos = snapshot.contadorMovimientos;
        historialTexto = snapshot.historialMovimientos == null
                ? new ArrayList<>()
                : new ArrayList<>(snapshot.historialMovimientos);

        deshacerUsado = true;
        snapshotDeshacer = null;
        partidaTerminada = false;
        modoRevision = false;

        cargarRelojDesdeSnapshot(snapshot, true);
        recortarSnapshotsRevision();
        restaurarHistorialMovimientos();
        limpiarSeleccion();
        actualizarEstilosTimer();
        actualizarEstadoPausa();
        guardarPartidaEnCurso();
    }

    private void inicializarSnapshotsRevision() {
        if (snapshotsRevision == null) {
            snapshotsRevision = new ArrayList<>();
        }
        if (snapshotsRevision.isEmpty()) {
            snapshotsRevision.add(crearSnapshot());
        }
    }

    private void agregarSnapshotRevision() {
        inicializarSnapshotsRevision();
        snapshotsRevision.add(crearSnapshot());
    }

    private void recortarSnapshotsRevision() {
        inicializarSnapshotsRevision();
        int tamanoEsperado = Math.max(1, contadorMovimientos + 1);
        while (snapshotsRevision.size() > tamanoEsperado) {
            snapshotsRevision.remove(snapshotsRevision.size() - 1);
        }
        if (snapshotsRevision.isEmpty()) {
            snapshotsRevision.add(crearSnapshot());
        }
    }

    private GameSnapshot crearSnapshot() {
        GameSnapshot snapshot = new GameSnapshot();
        GameState state = reloj == null ? null : reloj.toGameState();

        snapshot.boardState = modelo == null ? "" : modelo.exportarEstado();
        snapshot.whiteTurn = turnoActual == ColorPieza.BLANCA;
        snapshot.whiteSeconds = state == null ? tiempoInicialConfigurado() : state.whiteSeconds;
        snapshot.blackSeconds = state == null ? tiempoInicialConfigurado() : state.blackSeconds;
        snapshot.contadorMovimientos = contadorMovimientos;
        snapshot.historialMovimientos = new ArrayList<>(historialTexto);
        return snapshot;
    }

    private void cargarRelojDesdeSnapshot(GameSnapshot snapshot, boolean pausado) {
        if (reloj == null || snapshot == null) {
            return;
        }

        GameState state = new GameState();
        state.whiteSeconds = snapshot.whiteSeconds;
        state.blackSeconds = snapshot.blackSeconds;
        state.whiteTurn = snapshot.whiteTurn;
        state.paused = pausado;
        state.finished = false;
        reloj.loadGameState(state);
        timerBlancas.setText(reloj.getWhiteTime());
        timerNegras.setText(reloj.getBlackTime());
    }

    private int tiempoInicialConfigurado() {
        return settings != null && settings.getTiempoSegundos() > 0
                ? settings.getTiempoSegundos()
                : TIEMPO_INICIAL_SEG;
    }

    private void prepararReloj() {
        int tiempoInicial = tiempoInicialConfigurado();
        reloj = new ChessClock(tiempoInicial);

        relojTick = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            timerBlancas.setText(reloj.getWhiteTime());
            timerNegras.setText(reloj.getBlackTime());

            if (reloj.isFinished() && !partidaTerminada) {
                finalizar("Fin por tiempo", reloj.getWinnerText());
            }
        }));
        relojTick.setCycleCount(Timeline.INDEFINITE);
        relojTick.play();

        if (partidaCargada != null) {
            GameState state = new GameState();
            state.whiteSeconds = partidaCargada.whiteSeconds;
            state.blackSeconds = partidaCargada.blackSeconds;
            state.whiteTurn = partidaCargada.whiteTurn;
            state.paused = partidaCargada.paused;
            state.finished = partidaCargada.finished;
            reloj.loadGameState(state);
        }

        timerBlancas.setText(reloj.getWhiteTime());
        timerNegras.setText(reloj.getBlackTime());

        if (partidaCargada == null) {
            // Arranca inmediatamente: las blancas empiezan a descontar tiempo
            // desde que se abre la pantalla (whiteTurn = true por defecto).
            reloj.start();
        }
    }

    /** Resalta el reloj del jugador en turno cambiando las clases CSS. */
    private void actualizarEstilosTimer() {
        timerBlancas.getStyleClass().removeAll("timer-jugador-activo", "timer-jugador-inactivo");
        timerNegras.getStyleClass().removeAll("timer-jugador-activo", "timer-jugador-inactivo");

        if (turnoActual == ColorPieza.BLANCA) {
            timerBlancas.getStyleClass().add("timer-jugador-activo");
            timerNegras.getStyleClass().add("timer-jugador-inactivo");
        } else {
            timerBlancas.getStyleClass().add("timer-jugador-inactivo");
            timerNegras.getStyleClass().add("timer-jugador-activo");
        }
    }

    // ── Historial ────────────────────────────────────────────────────────────

    /**
     * Estructura del GridPane: col 0 = nº de jugada, col 1 = blancas, col 2 = negras.
     */
    private void registrarMovimientoEnHistorial(MovimientoInfo mov) {
        contadorMovimientos++;
        String texto = formatearJugada(mov);
        historialTexto.add(texto);
        agregarMovimientoAlPanel(texto, contadorMovimientos);
    }

    private void restaurarHistorialMovimientos() {
        historialMovimientos.getChildren().clear();
        for (int i = 0; i < historialTexto.size(); i++) {
            agregarMovimientoAlPanel(historialTexto.get(i), i + 1);
        }
    }

    private void agregarMovimientoAlPanel(String texto, int numeroMovimiento) {
        Label label = new Label(texto);
        label.setStyle("-fx-text-fill: white; -fx-font-size: 12px;");

        if (numeroMovimiento % 2 == 1) {
            int numeroJugada = (numeroMovimiento + 1) / 2;

            Label numero = new Label(numeroJugada + ".");
            numero.setStyle("-fx-text-fill: #aaaaaa; -fx-font-size: 12px;");

            historialMovimientos.add(numero, 0, numeroJugada - 1);
            historialMovimientos.add(label,  1, numeroJugada - 1);
        } else {
            int numeroJugada = numeroMovimiento / 2;
            historialMovimientos.add(label, 2, numeroJugada - 1);
        }
    }

    private String formatearJugada(MovimientoInfo mov) {
        if (mov.isEnroque()) {
            return mov.getColDestino() == 6 ? "O-O" : "O-O-O";
        }
        String pieza = abreviaturaPieza(mov.getTipoPieza());
        String origen = casillaTexto(mov.getFilaOrigen(), mov.getColOrigen());
        String destino = casillaTexto(mov.getFilaDestino(), mov.getColDestino());
        String sep = mov.isCaptura() ? "x" : "-";
        String texto = pieza + origen + sep + destino;
        if (mov.isPromocion()) texto += "=D";
        return texto;
    }

    private String abreviaturaPieza(TipoPieza tipo) {
        return switch (tipo) {
            case REY     -> "R";
            case DAMA    -> "D";
            case TORRE   -> "T";
            case ALFIL   -> "A";
            case CABALLO -> "C";
            case PEON    -> "";
        };
    }

    private String casillaTexto(int fila, int col) {
        char letra = (char) ('a' + col);
        int numero = 8 - fila;
        return "" + letra + numero;
    }

    // ── Estado de fin de partida ─────────────────────────────────────────────

    private void comprobarFinDePartida() {
        boolean blancasJaque = modelo.estaEnJaque(ColorPieza.BLANCA);
        boolean negrasJaque  = modelo.estaEnJaque(ColorPieza.NEGRA);
        boolean blancasMov   = modelo.tieneMovimientosLegales(ColorPieza.BLANCA);
        boolean negrasMov    = modelo.tieneMovimientosLegales(ColorPieza.NEGRA);

        if (blancasJaque && !blancasMov) {
            finalizar("Jaque mate", "Ganan las negras");
        } else if (negrasJaque && !negrasMov) {
            finalizar("Jaque mate", "Ganan las blancas");
        } else if (!blancasJaque && !blancasMov) {
            finalizar("Tablas", "Ahogado: las blancas no tienen movimientos");
        } else if (!negrasJaque && !negrasMov) {
            finalizar("Tablas", "Ahogado: las negras no tienen movimientos");
        }
    }

    private void finalizar(String titulo, String mensaje) {
        partidaTerminada = true;
        cancelarTurnoIAPendiente();
        if (reloj != null)     reloj.pause();
        if (relojTick != null) relojTick.stop();
        registrarResultadoFinal(titulo, mensaje);
        actualizarEstadoPausa();
        mostrarFin(titulo, mensaje);
    }

    private void mostrarFin(String titulo, String mensaje) {
        ButtonType btnFinalizar = new ButtonType("Finalizar", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnReiniciar = new ButtonType("Reiniciar", ButtonBar.ButtonData.OTHER);
        ButtonType btnRevisar = new ButtonType("Revisar partida actual", ButtonBar.ButtonData.OTHER);

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, mensaje, btnFinalizar, btnReiniciar, btnRevisar);
        alert.setTitle("Fin de partida");
        alert.setHeaderText(titulo);
        Optional<ButtonType> respuesta = alert.showAndWait();

        if (respuesta.isPresent() && respuesta.get() == btnFinalizar) {
            irAInicio();
        } else if (respuesta.isPresent() && respuesta.get() == btnReiniciar) {
            reiniciarPartida();
        } else {
            activarRevision();
        }
    }

    // ── Jugadores ─────────────────────────────────────────────────────────────

    private void configurarJugadores() {
        String nombreUsuario = nombreUsuario();
        String inicialesUsuario = AppSession.getCurrentInitials();
        UserProfile profile = GamePersistenceService.cargarPerfil(userId);

        if (NuevaPartidaSettings.TIPO_CONTRA_IA.equals(settings.getTipoPartida())) {
            if (colorUsuario == ColorPieza.BLANCA) {
                nombreBlancas.setText(nombreUsuario);
                AvatarUtil.aplicarAvatar(avatarBlancas, inicialesUsuario, profile.photoPath, 34);
                nombreNegras.setText("IA");
                avatarNegras.setText("IA");
            } else {
                nombreNegras.setText(nombreUsuario);
                AvatarUtil.aplicarAvatar(avatarNegras, inicialesUsuario, profile.photoPath, 34);
                nombreBlancas.setText("IA");
                avatarBlancas.setText("IA");
            }
            return;
        }

        nombreNegras.setText(colorUsuario == ColorPieza.NEGRA ? nombreUsuario : "Jugador Negras");
        if (colorUsuario == ColorPieza.NEGRA) {
            AvatarUtil.aplicarAvatar(avatarNegras, inicialesUsuario, profile.photoPath, 34);
        } else {
            avatarNegras.setGraphic(null);
            avatarNegras.setText("JN");
        }

        nombreBlancas.setText(colorUsuario == ColorPieza.BLANCA ? nombreUsuario : "Jugador Blancas");
        if (colorUsuario == ColorPieza.BLANCA) {
            AvatarUtil.aplicarAvatar(avatarBlancas, inicialesUsuario, profile.photoPath, 34);
        } else {
            avatarBlancas.setGraphic(null);
            avatarBlancas.setText("JB");
        }
    }

    // ── Navegación ────────────────────────────────────────────────────────────

    private String nombreUsuario() {
        String displayName = AppSession.getCurrentDisplayName();
        if (displayName != null && !displayName.isBlank()) {
            return displayName;
        }

        String email = AppSession.getCurrentEmail();
        if (email != null && !email.isBlank()) {
            return email;
        }

        return "Jugador";
    }

    private ColorPieza resolverColorUsuario(String colorSeleccionado) {
        if (NuevaPartidaSettings.COLOR_NEGRAS.equals(colorSeleccionado)) {
            return ColorPieza.NEGRA;
        }
        if (NuevaPartidaSettings.COLOR_ALEATORIO.equals(colorSeleccionado)) {
            return Math.random() < 0.5 ? ColorPieza.BLANCA : ColorPieza.NEGRA;
        }
        return ColorPieza.BLANCA;
    }

    private String colorUsuarioSetting() {
        return colorUsuario == ColorPieza.NEGRA
                ? NuevaPartidaSettings.COLOR_NEGRAS
                : NuevaPartidaSettings.COLOR_BLANCAS;
    }

    private void guardarPartidaEnCurso() {
        guardarPartidaEnCurso(null, false);
    }

    private void guardarPartidaEnCurso(Boolean pausedOverride) {
        guardarPartidaEnCurso(pausedOverride, false);
    }

    private void guardarPartidaEnCurso(Boolean pausedOverride, boolean forzarGuardado) {
        if (settings == null || (!forzarGuardado && !settings.isGuardarAutomaticamente())
                || partidaTerminada || reloj == null || modelo == null) {
            return;
        }

        GameState state = reloj.toGameState();
        SavedGame savedGame = new SavedGame();
        savedGame.id = partidaCargada != null && partidaCargada.id != null ? partidaCargada.id : UUID.randomUUID().toString();
        savedGame.fechaIso = GamePersistenceService.nowIso();
        savedGame.boardState = modelo.exportarEstado();
        savedGame.whiteTurn = state.whiteTurn;
        savedGame.whiteSeconds = state.whiteSeconds;
        savedGame.blackSeconds = state.blackSeconds;
        savedGame.paused = pausedOverride == null ? state.paused : pausedOverride;
        savedGame.finished = state.finished;
        savedGame.contadorMovimientos = contadorMovimientos;
        savedGame.tipoPartida = settings.getTipoPartida();
        savedGame.modoJuego = settings.getModoJuego();
        savedGame.colorJugador = colorUsuarioSetting();
        savedGame.tiempoInicialSegundos = settings.getTiempoSegundos();
        savedGame.historialMovimientos = new ArrayList<>(historialTexto);
        savedGame.deshacerUsado = deshacerUsado;
        savedGame.snapshotDeshacer = snapshotDeshacer;
        savedGame.snapshotsRevision = new ArrayList<>(snapshotsRevision);

        GamePersistenceService.guardarPartidaEnCurso(userId, savedGame);
        partidaCargada = savedGame;
    }

    private void registrarResultadoFinal(String titulo, String mensaje) {
        if (resultadoRegistrado || settings == null || !settings.isGuardarAutomaticamente()) {
            return;
        }

        GameRecord record = new GameRecord();
        record.id = UUID.randomUUID().toString();
        record.fechaIso = GamePersistenceService.nowIso();
        record.resultado = resultadoDesdeMensaje(titulo, mensaje);
        record.oponente = oponenteActual();
        record.tipoPartida = settings.getTipoLabel();
        record.modoJuego = settings.getModoLabel();
        record.colorJugador = colorUsuario == ColorPieza.NEGRA ? "Negras" : "Blancas";
        record.resumen = mensaje;

        GamePersistenceService.registrarPartidaFinalizada(userId, record);
        resultadoRegistrado = true;
    }

    private String resultadoDesdeMensaje(String titulo, String mensaje) {
        String texto = ((titulo == null ? "" : titulo) + " " + (mensaje == null ? "" : mensaje)).toLowerCase(Locale.ROOT);
        if (texto.contains("tablas") || texto.contains("ahogado")) {
            return GameRecord.RESULTADO_TABLAS;
        }

        boolean gananBlancas = texto.contains("ganan las blancas") || texto.contains("ganan blancas");
        boolean gananNegras = texto.contains("ganan las negras") || texto.contains("ganan negras");

        if ((gananBlancas && colorUsuario == ColorPieza.BLANCA) || (gananNegras && colorUsuario == ColorPieza.NEGRA)) {
            return GameRecord.RESULTADO_VICTORIA;
        }

        return GameRecord.RESULTADO_DERROTA;
    }

    private String oponenteActual() {
        if (NuevaPartidaSettings.TIPO_CONTRA_IA.equals(settings.getTipoPartida())) {
            return "IA";
        }
        return colorUsuario == ColorPieza.BLANCA ? "Jugador Negras" : "Jugador Blancas";
    }

    @FXML
    private void onPausa() {
        if (partidaTerminada || modoRevision) {
            return;
        }

        pausarPartida();
        mostrarMenuPausa();
    }

    private void pausarPartida() {
        pausaActiva = true;
        limpiarSeleccion();
        if (reloj != null) {
            reloj.pause();
        }
        if (turnoIAPendiente != null) {
            turnoIAPendiente.pause();
        }
        actualizarEstadoPausa();
    }

    private void reanudarPartida() {
        if (partidaTerminada || modoRevision) {
            return;
        }

        pausaActiva = false;
        if (reloj != null) {
            reloj.start();
        }
        if (turnoIAPendiente != null) {
            turnoIAPendiente.play();
        } else if (esTurnoIA()) {
            programarTurnoIA();
        }
        actualizarEstadoPausa();
        guardarPartidaEnCurso();
    }

    private void mostrarMenuPausa() {
        ButtonType btnReanudar = new ButtonType("Reanudar", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnReiniciar = new ButtonType("Reiniciar", ButtonBar.ButtonData.OTHER);
        ButtonType btnDeshacer = new ButtonType("Deshacer movimiento", ButtonBar.ButtonData.OTHER);
        ButtonType btnSalir = new ButtonType("Salir", ButtonBar.ButtonData.CANCEL_CLOSE);

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                "La partida esta pausada.",
                btnReanudar,
                btnReiniciar,
                btnDeshacer,
                btnSalir);
        alert.setTitle("Pausa");
        alert.setHeaderText("Partida pausada");

        Button botonDeshacer = (Button) alert.getDialogPane().lookupButton(btnDeshacer);
        if (botonDeshacer != null) {
            botonDeshacer.setDisable(!puedeDeshacer());
        }

        Optional<ButtonType> respuesta = alert.showAndWait();
        if (respuesta.isEmpty() || respuesta.get() == btnReanudar) {
            reanudarPartida();
        } else if (respuesta.get() == btnReiniciar) {
            reiniciarPartida();
        } else if (respuesta.get() == btnDeshacer) {
            deshacerUltimoMovimiento();
            reanudarPartida();
        } else if (respuesta.get() == btnSalir) {
            salirDesdePausa();
        }
    }

    private void salirDesdePausa() {
        ButtonType btnSi = new ButtonType("Si", ButtonBar.ButtonData.YES);
        ButtonType btnNo = new ButtonType("No", ButtonBar.ButtonData.NO);

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                "Quieres guardar la partida en curso antes de salir?",
                btnSi,
                btnNo);
        alert.setTitle("Guardar partida");
        alert.setHeaderText("Guardar partida");

        Optional<ButtonType> respuesta = alert.showAndWait();
        if (respuesta.isPresent() && respuesta.get() == btnSi) {
            guardarPartidaEnCurso(false, true);
            detenerPartidaActual();
            irAInicio();
        } else if (respuesta.isPresent() && respuesta.get() == btnNo) {
            GamePersistenceService.eliminarPartidaEnCurso(userId);
            detenerPartidaActual();
            irAInicio();
        } else {
            reanudarPartida();
        }
    }

    private void activarRevision() {
        cancelarTurnoIAPendiente();
        modoRevision = true;
        pausaActiva = false;
        if (reloj != null) {
            reloj.pause();
        }
        if (panelRevision != null) {
            panelRevision.setManaged(true);
            panelRevision.setVisible(true);
        }
        if (btnPausa != null) {
            btnPausa.setDisable(true);
        }
        inicializarSnapshotsRevision();
        mostrarSnapshotRevision(snapshotsRevision.size() - 1);
    }

    @FXML
    private void onRevisionAnterior() {
        mostrarSnapshotRevision(indiceRevision - 1);
    }

    @FXML
    private void onRevisionSiguiente() {
        mostrarSnapshotRevision(indiceRevision + 1);
    }

    @FXML
    private void onRevisionFinal() {
        mostrarSnapshotRevision(snapshotsRevision.size() - 1);
    }

    private void mostrarSnapshotRevision(int indice) {
        if (snapshotsRevision == null || snapshotsRevision.isEmpty()) {
            return;
        }

        indiceRevision = Math.max(0, Math.min(indice, snapshotsRevision.size() - 1));
        GameSnapshot snapshot = snapshotsRevision.get(indiceRevision);
        modelo.importarEstado(snapshot.boardState);
        turnoActual = snapshot.whiteTurn ? ColorPieza.BLANCA : ColorPieza.NEGRA;
        dibujarTablero();
        actualizarEstilosTimer();
        actualizarControlesRevision();
    }

    private void actualizarControlesRevision() {
        int totalMovimientos = Math.max(0, snapshotsRevision.size() - 1);
        if (lblRevision != null) {
            lblRevision.setText(indiceRevision == 0
                    ? "Posicion inicial"
                    : "Movimiento " + indiceRevision + " de " + totalMovimientos);
        }
        if (btnRevisionAnterior != null) {
            btnRevisionAnterior.setDisable(indiceRevision <= 0);
        }
        if (btnRevisionSiguiente != null) {
            btnRevisionSiguiente.setDisable(indiceRevision >= snapshotsRevision.size() - 1);
        }
        if (btnRevisionFinal != null) {
            btnRevisionFinal.setDisable(indiceRevision >= snapshotsRevision.size() - 1);
        }
    }

    private void reiniciarPartida() {
        detenerPartidaActual();
        GamePersistenceService.eliminarPartidaEnCurso(userId);
        AppSession.setNuevaPartidaSettings(settings.copy());
        SceneManager.navegarA("/com/tfg/ajedrez/vista/partida.fxml");
    }

    private void detenerPartidaActual() {
        cancelarTurnoIAPendiente();
        if (relojTick != null) {
            relojTick.stop();
        }
        if (reloj != null) {
            reloj.pause();
        }
    }

    private void irAInicio() {
        SceneManager.navegarA("/com/tfg/ajedrez/vista/menu-principal.fxml");
    }

    private void actualizarEstadoPausa() {
        if (btnPausa != null) {
            btnPausa.setDisable(partidaTerminada || modoRevision);
        }
    }

    @FXML
    private void onDesplegable() {
        boolean visible = !menuDesplegable.isVisible();
        menuDesplegable.setVisible(visible);
        RegionMenu.setVisible(visible);
    }

    @FXML
    private void onCerrarMenu() {
        if (menuDesplegable.isVisible() && RegionMenu.isVisible()) {
            menuDesplegable.setVisible(false);
            RegionMenu.setVisible(false);
        }
    }

    @FXML
    private void onCambiarTema() {
        ThemeManager.toggleTheme();
        if (menuDesplegable.getScene() != null) {
            ThemeManager.applyTheme(menuDesplegable.getScene().getRoot());
        }
        actualizarTextoTema();
        onCerrarMenu();
    }

    @FXML
    private void onCerrarSesion() {
        guardarPartidaEnCurso(false);
        detenerPartidaActual();
        SceneManager.navegarA("/com/tfg/ajedrez/vista/login.fxml");
    }

    private void actualizarTextoTema() {
        btnAjustes.setText(ThemeManager.getMenuLabel());
    }

    @FXML
    private void onVolver() {
        if (partidaTerminada || modoRevision) {
            detenerPartidaActual();
            irAInicio();
            return;
        }

        pausarPartida();
        salirDesdePausa();
    }
}
