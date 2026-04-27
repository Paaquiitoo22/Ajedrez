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

    public String getSimbolo() {
        return switch (tipo) {
            case REY -> color == ColorPieza.BLANCA ? "\u2654" : "\u265A";
            case DAMA -> color == ColorPieza.BLANCA ? "\u2655" : "\u265B";
            case TORRE -> color == ColorPieza.BLANCA ? "\u2656" : "\u265C";
            case ALFIL -> color == ColorPieza.BLANCA ? "\u2657" : "\u265D";
            case CABALLO -> color == ColorPieza.BLANCA ? "\u2658" : "\u265E";
            case PEON -> color == ColorPieza.BLANCA ? "\u2659" : "\u265F";
        };
    }
}

