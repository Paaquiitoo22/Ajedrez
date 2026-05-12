package com.tfg.ajedrez.persistence;

import java.util.ArrayList;
import java.util.List;

public class GameRecord {
/**Esta clase se usa para guardar las partidas finalizadas en el historial */
    public static final String RESULTADO_VICTORIA = "VICTORIA";
    public static final String RESULTADO_DERROTA = "DERROTA";
    public static final String RESULTADO_TABLAS = "TABLAS";

    public String id;
    public String fechaIso;
    public String resultado;
    public String oponente;
    public String tipoPartida;
    public String modoJuego;
    public String colorJugador;
    public String resumen;
    public String boardState;
    public String tipoPartidaCodigo;
    public String modoJuegoCodigo;
    public String colorJugadorCodigo;
    public int tiempoInicialSegundos;
    public int puntosBlancas;
    public int puntosNegras;
    public List<String> historialMovimientos = new ArrayList<>();
    public List<GameSnapshot> snapshotsRevision = new ArrayList<>();

    public GameRecord() {
    }
}
