package com.tfg.ajedrez.controller;

import com.tfg.ajedrez.clock.ChessClock;
import com.tfg.ajedrez.model.ColorPieza;
import com.tfg.ajedrez.model.MovimientoInfo;
import com.tfg.ajedrez.model.Pieza;
import com.tfg.ajedrez.model.Posicion;
import com.tfg.ajedrez.model.Tablero;
import com.tfg.ajedrez.model.TipoPieza;
import com.tfg.ajedrez.util.SceneManager;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

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

    @FXML private GridPane historialMovimientos;

    // ── Constantes del tablero ────────────────────────────────────────────────

    private static final int    CELDA        = 56;
    private static final String COLOR_CLARA  = "#8ca2ad";
    private static final String COLOR_OSCURA = "#4a6f8a";
    private static final String COLOR_SELEC  = "#facc15";

    private static final int TIEMPO_INICIAL_SEG = 600; // 10 minutos por jugador

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

    // ── Ciclo de vida ─────────────────────────────────────────────────────────

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        modelo = new Tablero();

        configurarJugadores();
        dibujarTablero();
        prepararReloj();
        actualizarEstilosTimer();
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
        MovimientoInfo mov = modelo.moverPieza(filaSeleccionada, colSeleccionada, fila, col);
        if (mov != null) {
            registrarMovimientoEnHistorial(mov);
            cambiarTurno();
            comprobarFinDePartida();
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

    // ── Reloj ────────────────────────────────────────────────────────────────

    private void prepararReloj() {
        reloj = new ChessClock(TIEMPO_INICIAL_SEG);

        relojTick = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            timerBlancas.setText(reloj.getWhiteTime());
            timerNegras.setText(reloj.getBlackTime());

            if (reloj.isFinished() && !partidaTerminada) {
                partidaTerminada = true;
                relojTick.stop();
                mostrarFin("Fin por tiempo", reloj.getWinnerText());
            }
        }));
        relojTick.setCycleCount(Timeline.INDEFINITE);
        relojTick.play();

        timerBlancas.setText(reloj.getWhiteTime());
        timerNegras.setText(reloj.getBlackTime());

        // Arranca inmediatamente: las blancas empiezan a descontar tiempo
        // desde que se abre la pantalla (whiteTurn = true por defecto).
        reloj.start();
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

        Label label = new Label(formatearJugada(mov));
        label.setStyle("-fx-text-fill: white; -fx-font-size: 12px;");

        if (mov.getColorPieza() == ColorPieza.BLANCA) {
            int numeroJugada = (contadorMovimientos + 1) / 2;

            Label numero = new Label(numeroJugada + ".");
            numero.setStyle("-fx-text-fill: #aaaaaa; -fx-font-size: 12px;");

            historialMovimientos.add(numero, 0, numeroJugada - 1);
            historialMovimientos.add(label,  1, numeroJugada - 1);
        } else {
            int numeroJugada = contadorMovimientos / 2;
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
        if (reloj != null)     reloj.pause();
        if (relojTick != null) relojTick.stop();
        mostrarFin(titulo, mensaje);
    }

    private void mostrarFin(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Fin de partida");
        alert.setHeaderText(titulo);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    // ── Jugadores ─────────────────────────────────────────────────────────────

    private void configurarJugadores() {
        nombreNegras.setText("Jugador Negras");
        avatarNegras.setText("JN");

        nombreBlancas.setText("Jugador Blancas");
        avatarBlancas.setText("JB");
    }

    // ── Navegación ────────────────────────────────────────────────────────────

    @FXML
    private void onVolver() {
        if (relojTick != null) relojTick.stop();
        if (reloj != null)     reloj.pause();
        SceneManager.navegarA("/com/tfg/ajedrez/vista/menu-principal.fxml");
    }
}
