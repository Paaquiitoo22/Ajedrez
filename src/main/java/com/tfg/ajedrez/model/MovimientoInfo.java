package com.tfg.ajedrez.model;

/**
 * Clase que almacena la información de un movimiento.
 * Se utiliza para registrar jugadas, capturas,
 * promociones y enroques.
 */
public class MovimientoInfo {

    // Tipo y color de la pieza que se mueve
    private final TipoPieza tipoPieza;
    private final ColorPieza colorPieza;

    // Posición de origen
    private final int filaOrigen;
    private final int colOrigen;

    // Posición de destino
    private final int filaDestino;
    private final int colDestino;

    // Indica si el movimiento realiza una captura
    private final boolean captura;

    // Indica si el movimiento es un enroque
    private final boolean enroque;

    // Indica si existe promoción de peón
    private final boolean promocion;

    // Información de la pieza capturada
    private final TipoPieza tipoPiezaCapturada;
    private final ColorPieza colorPiezaCapturada;

    // Tipo de pieza obtenida tras la promoción
    private final TipoPieza tipoPromocion;

    /**
     * Constructor básico para movimientos normales.
     */
    public MovimientoInfo(TipoPieza tipoPieza, ColorPieza colorPieza,
                          int filaOrigen, int colOrigen,
                          int filaDestino, int colDestino,
                          boolean captura,
                          boolean enroque,
                          boolean promocion) {

        this(tipoPieza, colorPieza, filaOrigen, colOrigen,
                filaDestino, colDestino,
                captura, enroque, promocion,
                null, null, null);
    }

    /**
     * Constructor completo utilizado para movimientos especiales
     * como capturas o promociones.
     */
    public MovimientoInfo(TipoPieza tipoPieza, ColorPieza colorPieza,
                          int filaOrigen, int colOrigen,
                          int filaDestino, int colDestino,
                          boolean captura,
                          boolean enroque,
                          boolean promocion,
                          TipoPieza tipoPiezaCapturada,
                          ColorPieza colorPiezaCapturada,
                          TipoPieza tipoPromocion) {

        this.tipoPieza = tipoPieza;
        this.colorPieza = colorPieza;
        this.filaOrigen = filaOrigen;
        this.colOrigen = colOrigen;
        this.filaDestino = filaDestino;
        this.colDestino = colDestino;
        this.captura = captura;
        this.enroque = enroque;
        this.promocion = promocion;
        this.tipoPiezaCapturada = tipoPiezaCapturada;
        this.colorPiezaCapturada = colorPiezaCapturada;
        this.tipoPromocion = tipoPromocion;
    }

    public TipoPieza getTipoPieza() {
        return tipoPieza;
    }

    public ColorPieza getColorPieza() {
        return colorPieza;
    }

    public int getFilaOrigen() {
        return filaOrigen;
    }

    public int getColOrigen() {
        return colOrigen;
    }

    public int getFilaDestino() {
        return filaDestino;
    }

    public int getColDestino() {
        return colDestino;
    }

    public boolean isCaptura() {
        return captura;
    }

    public boolean isEnroque() {
        return enroque;
    }

    public boolean isPromocion() {
        return promocion;
    }

    public TipoPieza getTipoPiezaCapturada() {
        return tipoPiezaCapturada;
    }

    public ColorPieza getColorPiezaCapturada() {
        return colorPiezaCapturada;
    }

    public TipoPieza getTipoPromocion() {
        return tipoPromocion;
    }
}