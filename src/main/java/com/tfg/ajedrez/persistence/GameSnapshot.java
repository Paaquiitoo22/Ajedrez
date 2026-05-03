package com.tfg.ajedrez.persistence;

import java.util.ArrayList;
import java.util.List;

public class GameSnapshot {

    public String boardState;
    public boolean whiteTurn;
    public int whiteSeconds;
    public int blackSeconds;
    public int contadorMovimientos;
    public boolean relojIniciado;
    public List<String> historialMovimientos = new ArrayList<>();

    public GameSnapshot() {
    }
}
