package com.tfg.ajedrez.model;

/**
 * Clase que representa una posición dentro del tablero.
 * Cada posición está formada por una fila y una columna.
 */
public class Posicion {

    // Fila de la posición
    private int fila;

    // Columna de la posición
    private int columna;

    /**
     * Constructor de la posición.
     */
    public Posicion(int fila, int columna) {

        this.fila = fila;
        this.columna = columna;
    }

    public int getFila() {
        return fila;
    }

    public int getColumna() {
        return columna;
    }
}