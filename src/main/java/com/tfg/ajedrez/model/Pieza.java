package com.tfg.ajedrez.model;

/**
 * Clase que representa una pieza del tablero.
 * Almacena su tipo, color y posición actual.
 */
public class Pieza {

    // Tipo de pieza (rey, reina, torre, etc.)
    private TipoPieza tipo;

    // Color de la pieza
    private ColorPieza color;

    // Posición actual dentro del tablero
    private Posicion posicion;

    // Indica si la pieza ya se ha movido
    private boolean movida;

    /**
     * Constructor de la pieza.
     */
    public Pieza(TipoPieza tipo, ColorPieza color, Posicion posicion) {

        this.tipo = tipo;
        this.color = color;
        this.posicion = posicion;

        // Inicialmente ninguna pieza se ha movido
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

    /**
     * Actualiza la posición de la pieza.
     */
    public void setPosicion(Posicion posicion) {
        this.posicion = posicion;
    }

    public boolean isMovida() {
        return movida;
    }

    /**
     * Marca si la pieza ya realizó un movimiento.
     * Se utiliza para reglas especiales como el enroque.
     */
    public void setMovida(boolean movida) {
        this.movida = movida;
    }
}