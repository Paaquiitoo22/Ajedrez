package com.tfg.ajedrez.clock;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

/**
 * Clase encargada de gestionar el reloj de ajedrez.
 * Controla el tiempo de ambos jugadores y el turno actual.
 */
public class ChessClock {

    // Tiempo restante de las blancas en segundos
    private int whiteSeconds;

    // Tiempo restante de las negras en segundos
    private int blackSeconds;

    // Indica de quién es el turno
    private boolean whiteTurn = true;

    // Indica si el reloj está pausado
    private boolean paused = true;

    // Indica si la partida ha terminado
    private boolean finished = false;

    // Temporizador que ejecuta el conteo cada segundo
    private Timeline timeline;

    /**
     * Constructor del reloj.
     * Inicializa el tiempo de ambos jugadores.
     */
    public ChessClock(int initialSeconds) {

        this.whiteSeconds = initialSeconds;
        this.blackSeconds = initialSeconds;

        // Ejecuta el método tick() cada segundo
        timeline = new Timeline(
                new KeyFrame(Duration.seconds(1), e -> tick())
        );

        timeline.setCycleCount(Timeline.INDEFINITE);
    }

    /**
     * Reduce el tiempo del jugador actual.
     * Si el tiempo llega a cero, finaliza la partida.
     */
    private void tick() {

        // No hace nada si el reloj está pausado
        // o la partida ya terminó
        if (paused || finished) return;

        // Turno de las blancas
        if (whiteTurn) {

            if (whiteSeconds > 0) {
                whiteSeconds--;
            }

            // Finaliza la partida por tiempo
            if (whiteSeconds == 0) {
                finished = true;
                paused = true;
                timeline.stop();
            }

        } else {

            // Turno de las negras
            if (blackSeconds > 0) {
                blackSeconds--;
            }

            if (blackSeconds == 0) {
                finished = true;
                paused = true;
                timeline.stop();
            }
        }
    }

    /**
     * Inicia o reanuda el reloj.
     */
    public void start() {

        // Solo inicia si la partida sigue activa
        if (!finished) {
            paused = false;
            timeline.play();
        }
    }

    /**
     * Pausa el reloj.
     */
    public void pause() {
        paused = true;
    }

    /**
     * Reinicia el reloj con un nuevo tiempo.
     */
    public void reset(int initialSeconds) {

        pause();

        // Restablece el tiempo de ambos jugadores
        whiteSeconds = initialSeconds;
        blackSeconds = initialSeconds;

        // Reinicia el estado de la partida
        whiteTurn = true;
        finished = false;
    }

    /**
     * Cambia el turno entre blancas y negras.
     */
    public void switchTurn() {

        if (!finished) {
            whiteTurn = !whiteTurn;
        }
    }

    /**
     * Devuelve el tiempo restante de las blancas.
     */
    public String getWhiteTime() {
        return format(whiteSeconds);
    }

    /**
     * Devuelve el tiempo restante de las negras.
     */
    public String getBlackTime() {
        return format(blackSeconds);
    }

    public boolean isWhiteTurn() {
        return whiteTurn;
    }

    public boolean isPaused() {
        return paused;
    }

    public boolean isFinished() {
        return finished;
    }

    /**
     * Devuelve el ganador por tiempo.
     */
    public String getWinnerText() {

        if (!finished) return "";

        return whiteSeconds == 0
                ? "Ganan negras por tiempo"
                : "Ganan blancas por tiempo";
    }

    /**
     * Convierte el estado actual del reloj
     * en un objeto GameState para guardarlo.
     */
    public GameState toGameState() {

        GameState state = new GameState();

        // Guarda todos los valores del reloj
        state.whiteSeconds = this.whiteSeconds;
        state.blackSeconds = this.blackSeconds;
        state.whiteTurn = this.whiteTurn;
        state.paused = this.paused;
        state.finished = this.finished;

        return state;
    }

    /**
     * Restaura una partida previamente guardada.
     */
    public void loadGameState(GameState state) {

        pause();

        // Recupera el estado guardado
        this.whiteSeconds = state.whiteSeconds;
        this.blackSeconds = state.blackSeconds;
        this.whiteTurn = state.whiteTurn;
        this.paused = state.paused;
        this.finished = state.finished;

        // Reanuda el reloj si la partida sigue activa
        if (!paused && !finished) {
            timeline.play();
        }
    }

    /**
     * Convierte segundos al formato MM:SS.
     */
    private String format(int total) {

        int min = total / 60;
        int sec = total % 60;

        return String.format("%02d:%02d", min, sec);
    }
}