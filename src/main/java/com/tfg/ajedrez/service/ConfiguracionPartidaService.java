package com.tfg.ajedrez.service;

import com.tfg.ajedrez.model.ConfiguracionPartida;

public class ConfiguracionPartidaService {

    private static ConfiguracionPartida configuracionActual;

    public static void setConfiguracionActual(ConfiguracionPartida configuracion) {
        configuracionActual = configuracion;
    }

    public static ConfiguracionPartida getConfiguracionActual() {
        return configuracionActual;
    }

    public static void limpiar() {
        configuracionActual = null;
    }
}