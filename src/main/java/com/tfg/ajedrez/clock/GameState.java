package com.tfg.ajedrez.clock;

/**
 * Clase que almacena el estado de una partida.
 * Se utiliza para guardar y cargar el reloj del juego.
 */
public class GameState {

    // Tiempo restante de las blancas
    public int whiteSeconds;

    // Tiempo restante de las negras
    public int blackSeconds;

    // Indica de quién es el turno
    public boolean whiteTurn;

    // Indica si la partida está pausada
    public boolean paused;

    // Indica si la partida ha terminado
    public boolean finished;

    /**
     * Constructor vacío utilizado al crear
     * o restaurar un estado de partida.
     */
    public GameState() {
    }
}