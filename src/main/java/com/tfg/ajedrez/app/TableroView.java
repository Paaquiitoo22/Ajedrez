package com.tfg.ajedrez.app;

import com.tfg.ajedrez.model.ColorPieza;
import com.tfg.ajedrez.model.MovimientoInfo;
import com.tfg.ajedrez.model.Pieza;
import com.tfg.ajedrez.model.Posicion;
import com.tfg.ajedrez.model.Tablero;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class TableroView extends GridPane {

    private static final int SIZE = 68;

    private final Tablero tablero;
    private ColorPieza turnoActual = ColorPieza.BLANCA;

    private Integer filaSeleccionada = null;
    private Integer colSeleccionada = null;

    private List<Posicion> movimientosPosibles = new ArrayList<>();

    private Consumer<String> onCambioTurno;
    private Runnable onMovimientoRealizado;
    private Consumer<MovimientoInfo> onMovimientoInfo;

    public TableroView() {
        this.tablero = new Tablero();
        setAlignment(Pos.CENTER);
        setHgap(0);
        setVgap(0);
        setStyle("-fx-background-color: #2b2b2b; -fx-padding: 8; -fx-background-radius: 8;");
        dibujarTablero();
    }

    public void setOnCambioTurno(Consumer<String> onCambioTurno) {
        this.onCambioTurno = onCambioTurno;
        notificarTurno();
    }

    public void setOnMovimientoRealizado(Runnable onMovimientoRealizado) {
        this.onMovimientoRealizado = onMovimientoRealizado;
    }

    public void setOnMovimientoInfo(Consumer<MovimientoInfo> onMovimientoInfo) {
        this.onMovimientoInfo = onMovimientoInfo;
    }

    public String getTurnoActualTexto() {
        return turnoActual == ColorPieza.BLANCA ? "BLANCAS" : "NEGRAS";
    }

    public ColorPieza getTurnoActual() {
        return turnoActual;
    }

    public Tablero getTablero() {
        return tablero;
    }

    public void setTurnoActualDesdeTexto(String turno) {
        if ("NEGRAS".equalsIgnoreCase(turno)) {
            turnoActual = ColorPieza.NEGRA;
        } else {
            turnoActual = ColorPieza.BLANCA;
        }
        notificarTurno();
    }

    public String exportarEstadoTablero() {
        return tablero.exportarEstado();
    }

    public void cargarEstadoTablero(String estado) {
        tablero.importarEstado(estado);
        limpiarSeleccion();
        dibujarTablero();
    }

    public boolean estanBlancasEnJaque() {
        return tablero.estaEnJaque(ColorPieza.BLANCA);
    }

    public boolean estanNegrasEnJaque() {
        return tablero.estaEnJaque(ColorPieza.NEGRA);
    }

    public boolean tieneMovimientosLegalesBlancas() {
        return tablero.tieneMovimientosLegales(ColorPieza.BLANCA);
    }

    public boolean tieneMovimientosLegalesNegras() {
        return tablero.tieneMovimientosLegales(ColorPieza.NEGRA);
    }

    public MovimientoInfo moverDesdeFuera(int filaOrigen, int colOrigen, int filaDestino, int colDestino) {
        Pieza pieza = tablero.getPieza(filaOrigen, colOrigen);
        if (pieza == null || pieza.getColor() != turnoActual) {
            return null;
        }

        MovimientoInfo mov = tablero.moverPieza(filaOrigen, colOrigen, filaDestino, colDestino);
        if (mov != null) {
            cambiarTurno();

            if (onMovimientoInfo != null) {
                onMovimientoInfo.accept(mov);
            }

            if (onMovimientoRealizado != null) {
                onMovimientoRealizado.run();
            }

            limpiarSeleccion();
        }

        return mov;
    }

    private void notificarTurno() {
        if (onCambioTurno != null) {
            onCambioTurno.accept("Turno: " + (turnoActual == ColorPieza.BLANCA ? "Blancas" : "Negras"));
        }
    }

    private void dibujarTablero() {
        getChildren().clear();

        for (int fila = 0; fila < 8; fila++) {
            for (int col = 0; col < 8; col++) {

                StackPane casilla = new StackPane();
                casilla.setAlignment(Pos.CENTER);
                casilla.setPrefSize(SIZE, SIZE);
                casilla.setMinSize(SIZE, SIZE);
                casilla.setMaxSize(SIZE, SIZE);

                Rectangle fondo = new Rectangle(SIZE, SIZE);

                if ((fila + col) % 2 == 0) {
                    fondo.setFill(Color.web("#f0d9b5"));
                } else {
                    fondo.setFill(Color.web("#b58863"));
                }

                if (filaSeleccionada != null && colSeleccionada != null
                        && fila == filaSeleccionada && col == colSeleccionada) {
                    fondo.setStroke(Color.web("#facc15"));
                    fondo.setStrokeWidth(3);
                }

                casilla.getChildren().add(fondo);

                if (esMovimientoPosible(fila, col)) {
                    Circle punto = new Circle(9);
                    punto.setFill(Color.rgb(34, 197, 94, 0.45));
                    casilla.getChildren().add(punto);
                }

                Pieza pieza = tablero.getPieza(fila, col);
                if (pieza != null) {
                    if (!cargarImagenPieza(casilla, pieza)) {
                        Label label = new Label(pieza.getSimbolo());
                        label.setStyle("-fx-font-size: 30px; -fx-font-family: 'Segoe UI Symbol';");
                        casilla.getChildren().add(label);
                    }
                }

                final int f = fila;
                final int c = col;
                casilla.setOnMouseClicked(e -> manejarClick(f, c));

                add(casilla, col, fila);
            }
        }
    }

    private boolean cargarImagenPieza(StackPane casilla, Pieza pieza) {
        try {
            String ruta = obtenerRutaImagen(pieza);
            InputStream is = getClass().getResourceAsStream(ruta);

            if (is == null) {
                return false;
            }

            Image image = new Image(is);
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(44);
            imageView.setFitHeight(44);
            imageView.setPreserveRatio(true);

            casilla.getChildren().add(imageView);
            return true;

        } catch (Exception e) {
            return false;
        }
    }

    private String obtenerRutaImagen(Pieza pieza) {
        String color = pieza.getColor() == ColorPieza.BLANCA ? "white" : "black";
        String tipo = switch (pieza.getTipo()) {
            case REY -> "king";
            case DAMA -> "queen";
            case TORRE -> "rook";
            case ALFIL -> "bishop";
            case CABALLO -> "knight";
            case PEON -> "pawn";
        };

        return "/com/tfg/ajedrez/images/pieces/" + color + "_" + tipo + ".png";
    }

    private boolean esMovimientoPosible(int fila, int col) {
        for (Posicion p : movimientosPosibles) {
            if (p.getFila() == fila && p.getColumna() == col) {
                return true;
            }
        }
        return false;
    }

    private void manejarClick(int fila, int col) {
        Pieza piezaClicada = tablero.getPieza(fila, col);

        if (filaSeleccionada == null) {
            if (piezaClicada != null && piezaClicada.getColor() == turnoActual) {
                filaSeleccionada = fila;
                colSeleccionada = col;
                movimientosPosibles = tablero.obtenerMovimientosValidos(fila, col);
                dibujarTablero();
            }
            return;
        }

        if (filaSeleccionada == fila && colSeleccionada == col) {
            limpiarSeleccion();
            return;
        }

        Pieza piezaSeleccionada = tablero.getPieza(filaSeleccionada, colSeleccionada);

        if (piezaClicada != null && piezaClicada.getColor() == turnoActual) {
            filaSeleccionada = fila;
            colSeleccionada = col;
            movimientosPosibles = tablero.obtenerMovimientosValidos(fila, col);
            dibujarTablero();
            return;
        }

        if (piezaSeleccionada != null) {
            MovimientoInfo mov = tablero.moverPieza(filaSeleccionada, colSeleccionada, fila, col);
            if (mov != null) {
                cambiarTurno();

                if (onMovimientoInfo != null) {
                    onMovimientoInfo.accept(mov);
                }

                if (onMovimientoRealizado != null) {
                    onMovimientoRealizado.run();
                }
            }
        }

        limpiarSeleccion();
    }

    private void limpiarSeleccion() {
        filaSeleccionada = null;
        colSeleccionada = null;
        movimientosPosibles.clear();
        dibujarTablero();
    }

    private void cambiarTurno() {
        turnoActual = (turnoActual == ColorPieza.BLANCA) ? ColorPieza.NEGRA : ColorPieza.BLANCA;
        notificarTurno();
    }
}