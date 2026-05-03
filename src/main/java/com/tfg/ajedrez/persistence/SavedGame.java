package com.tfg.ajedrez.persistence;

import java.util.ArrayList;
import java.util.List;

public class SavedGame {

    public String id;
    public String fechaIso;
    public String boardState;
    public boolean whiteTurn;
    public int whiteSeconds;
    public int blackSeconds;
    public boolean paused;
    public boolean finished;
    public boolean relojIniciado;
    public int contadorMovimientos;
    public String tipoPartida;
    public String modoJuego;
    public String colorJugador;
    public int tiempoInicialSegundos;
    public List<String> historialMovimientos = new ArrayList<>();
    public boolean deshacerUsado;
    public GameSnapshot snapshotDeshacer;
    public List<GameSnapshot> snapshotsRevision = new ArrayList<>();

    public SavedGame() {
    }
}
