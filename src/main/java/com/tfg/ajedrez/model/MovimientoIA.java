package com.tfg.ajedrez.model;

public class MovimientoIA {

    private final int filaOrigen;
    private final int colOrigen;
    private final int filaDestino;
    private final int colDestino;
    private final boolean captura;

    public MovimientoIA(int filaOrigen, int colOrigen, int filaDestino, int colDestino, boolean captura) {
        this.filaOrigen = filaOrigen;
        this.colOrigen = colOrigen;
        this.filaDestino = filaDestino;
        this.colDestino = colDestino;
        this.captura = captura;
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
}
