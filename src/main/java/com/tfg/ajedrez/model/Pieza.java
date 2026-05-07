package com.tfg.ajedrez.model;

public class Pieza {
    private TipoPieza tipo;
    private ColorPieza color;
    private Posicion posicion;
    private boolean movida;

    public Pieza(TipoPieza tipo, ColorPieza color, Posicion posicion) {
        this.tipo = tipo;
        this.color = color;
        this.posicion = posicion;
        this.movida = false;
    }

    public TipoPieza getTipo() {
        return tipo;
    }

    public void setTipo(TipoPieza tipo) {
        this.tipo = tipo;
    }

    public ColorPieza getColor() {
        return color;
    }

    public Posicion getPosicion() {
        return posicion;
    }

    public void setPosicion(Posicion posicion) {
        this.posicion = posicion;
    }

    public boolean isMovida() {
        return movida;
    }

    public void setMovida(boolean movida) {
        this.movida = movida;
    }
}

