package com.tfg.ajedrez.service;

import com.tfg.ajedrez.model.PartidaGuardada;

public class PartidaSesionService {

    private static PartidaGuardada partidaActual;

    public static void setPartidaActual(PartidaGuardada partida) {
        partidaActual = partida;
    }

    public static PartidaGuardada getPartidaActual() {
        return partidaActual;
    }

    public static void limpiar() {
        partidaActual = null;
    }
}
