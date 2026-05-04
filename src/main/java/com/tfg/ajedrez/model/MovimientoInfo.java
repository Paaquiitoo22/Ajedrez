package com.tfg.ajedrez.model;

public class MovimientoInfo {

    private final TipoPieza tipoPieza;
    private final ColorPieza colorPieza;
    private final int filaOrigen;
    private final int colOrigen;
    private final int filaDestino;
    private final int colDestino;
    private final boolean captura;
    private final boolean enroque;
    private final boolean promocion;
    private final TipoPieza tipoPiezaCapturada;
    private final ColorPieza colorPiezaCapturada;
    private final TipoPieza tipoPromocion;

    public MovimientoInfo(TipoPieza tipoPieza, ColorPieza colorPieza,
                          int filaOrigen, int colOrigen,
                          int filaDestino, int colDestino,
                          boolean captura,
                          boolean enroque,
                          boolean promocion) {
        this(tipoPieza, colorPieza, filaOrigen, colOrigen, filaDestino, colDestino,
                captura, enroque, promocion, null, null, null);
    }

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
