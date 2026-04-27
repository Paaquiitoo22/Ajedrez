package com.tfg.ajedrez.model;

public class ConfiguracionPartida {

    private String modoJuego;
    private int tiempoInicialSegundos;

    public ConfiguracionPartida() {
    }

    public ConfiguracionPartida(String modoJuego, int tiempoInicialSegundos) {
        this.modoJuego = modoJuego;
        this.tiempoInicialSegundos = tiempoInicialSegundos;
    }

    public String getModoJuego() {
        return modoJuego;
    }

    public void setModoJuego(String modoJuego) {
        this.modoJuego = modoJuego;
    }

    public int getTiempoInicialSegundos() {
        return tiempoInicialSegundos;
    }

    public void setTiempoInicialSegundos(int tiempoInicialSegundos) {
        this.tiempoInicialSegundos = tiempoInicialSegundos;
    }
}