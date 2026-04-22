package com.tfg.ajedrez.controller;

import com.tfg.ajedrez.util.SceneManager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

import java.io.InputStream;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controlador de la pantalla de partida.
 *
 * Responsabilidades actuales:
 *  - Inicializar el tablero 8×8 con colores alternos
 *  - Colocar las piezas en posición inicial mediante PNGs
 *  - Configurar los paneles de jugador con datos de placeholder
 *
 *  (Falta):
 *  - Lógica de selección y movimiento de piezas
 *  - Validación de movimientos legales
 *  - Integración con Firebase Realtime DB para modo multijugador
 *  - Sincronización con datos reales del backend (nombres, avatares)
 *  - Lógica de temporizadores
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

    /**
     * Posición inicial del tablero (FEN: rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR).
     *
     * Cada celda contiene el nombre base de la pieza en español (sin sufijo de color),
     * o null si la casilla está vacía.
     * El sufijo "B" (Blancas) o "N" (Negras) se añade al construir el nombre del archivo PNG.
     *
     * Ejemplo: fila 0, col 0 → "torre" + "N" → "torraN.png"
     *          fila 7, col 4 → "rey"   + "B" → "reyB.png"
     *
     * Disposición de columnas: a=0, b=1, c=2, d=3, e=4, f=5, g=6, h=7
     * Disposición de filas:    rank 8 = fila 0  |  rank 1 = fila 7
     */
    private static final String[][] POSICION_INICIAL = {
        // fila 0 — rank 8 — piezas negras
        { "torre", "caballo", "alfil", "reina", "rey", "alfil", "caballo", "torre" },
        // fila 1 — rank 7 — peones negros
        { "peon", "peon", "peon", "peon", "peon", "peon", "peon", "peon" },
        // filas 2-5 — vacías
        { null, null, null, null, null, null, null, null },
        { null, null, null, null, null, null, null, null },
        { null, null, null, null, null, null, null, null },
        { null, null, null, null, null, null, null, null },
        // fila 6 — rank 2 — peones blancos
        { "peon", "peon", "peon", "peon", "peon", "peon", "peon", "peon" },
        // fila 7 — rank 1 — piezas blancas
        { "torre", "caballo", "alfil", "reina", "rey", "alfil", "caballo", "torre" }
    };

    // ── Ciclo de vida ─────────────────────────────────────────────────────────

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        inicializarTablero();
        configurarJugadores();
    }

    // ── Tablero ───────────────────────────────────────────────────────────────

    /** Rellena el GridPane con 64 casillas y las piezas en posición inicial. */
    private void inicializarTablero() {
        for (int fila = 0; fila < 8; fila++) {
            for (int col = 0; col < 8; col++) {
                tablero.add(crearCasilla(fila, col), col, fila);
            }
        }
    }

    /**
     * Crea una casilla del tablero con su color y pieza correspondiente.
     *
     * @param fila fila del tablero (0 = rank 8, 7 = rank 1)
     * @param col  columna (0 = a, 7 = h)
     */
    private StackPane crearCasilla(int fila, int col) {
        StackPane casilla = new StackPane();
        casilla.setPrefSize(CELDA, CELDA);
        casilla.setMinSize(CELDA, CELDA);
        casilla.setMaxSize(CELDA, CELDA);

        boolean esClara = (fila + col) % 2 == 0;
        casilla.setStyle("-fx-background-color: " + (esClara ? COLOR_CLARA : COLOR_OSCURA) + ";");

        String pieza = POSICION_INICIAL[fila][col];
        if (pieza != null) {
            String sufijo = (fila < 2) ? "N" : "B";
            ImageView iv = crearImagenPieza(pieza + sufijo);
            if (iv != null) casilla.getChildren().add(iv);
        }

        return casilla;
    }

    /**
     * Carga un PNG de img/piezas/ y lo devuelve como ImageView listo para usar.
     * Devuelve null si el archivo no existe (loguea aviso en consola).
     *
     * @param nombreArchivo nombre sin extensión (ej: "alfilN", "reyB")
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

    // ── Jugadores ─────────────────────────────────────────────────────────────

    /**
     * Configura los paneles de jugador con datos de placeholder.
     * Se sustituirá por datos reales del backend en próximas iteraciones.
     */
    private void configurarJugadores() {
        nombreNegras.setText("Jugador Negras");
        avatarNegras.setText("JN");
        timerNegras.setText("10:00");

        nombreBlancas.setText("Jugador Blancas");
        avatarBlancas.setText("JB");
        timerBlancas.setText("10:00");
    }

    // ── Navegación ────────────────────────────────────────────────────────────

    @FXML
    private void onVolver() {
        SceneManager.navegarA("/com/tfg/ajedrez/vista/menu-principal.fxml");
    }
}
