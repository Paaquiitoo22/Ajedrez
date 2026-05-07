package com.tfg.ajedrez.state;

public class NuevaPartidaSettings {

    public static final String MODO_CLASICA = "CLASICA";
    public static final String MODO_BLITZ = "BLITZ";
    public static final String MODO_RAPIDA = "RAPIDA";

    public static final String TIPO_CONTRA_IA = "CONTRA_IA";
    public static final String TIPO_LOCAL = "LOCAL";

    public static final String COLOR_BLANCAS = "BLANCAS";
    public static final String COLOR_NEGRAS = "NEGRAS";
    public static final String COLOR_ALEATORIO = "ALEATORIO";

    private String modoJuego;
    private int tiempoSegundos;
    private String tipoPartida;
    private String colorJugador;
    private DificultadIA dificultadIA;
    private int sonido;
    private boolean resaltarUltimoMovimiento;
    private boolean guardarAutomaticamente;

    public static NuevaPartidaSettings defaults() {
        NuevaPartidaSettings settings = new NuevaPartidaSettings();
        settings.modoJuego = MODO_CLASICA;
        settings.tiempoSegundos = 30 * 60;
        settings.tipoPartida = TIPO_CONTRA_IA;
        settings.colorJugador = COLOR_BLANCAS;
        settings.dificultadIA = DificultadIA.NORMAL;
        settings.sonido = 70;
        settings.resaltarUltimoMovimiento = true;
        settings.guardarAutomaticamente = true;
        return settings;
    }

    public NuevaPartidaSettings copy() {
        NuevaPartidaSettings copy = new NuevaPartidaSettings();
        copy.modoJuego = modoJuego;
        copy.tiempoSegundos = tiempoSegundos;
        copy.tipoPartida = tipoPartida;
        copy.colorJugador = colorJugador;
        copy.dificultadIA = dificultadIA;
        copy.sonido = sonido;
        copy.resaltarUltimoMovimiento = resaltarUltimoMovimiento;
        copy.guardarAutomaticamente = guardarAutomaticamente;
        return copy;
    }

    public String getModoLabel() {
        return switch (modoJuego) {
            case MODO_BLITZ -> "Blitz";
            case MODO_RAPIDA -> "Rapida";
            default -> "Clasica";
        };
    }

    public String getTipoLabel() {
        return TIPO_LOCAL.equals(tipoPartida) ? "Local" : "vs IA";
    }

    public String getColorLabel() {
        return switch (colorJugador) {
            case COLOR_NEGRAS -> "Negras";
            case COLOR_ALEATORIO -> "Aleatorio";
            default -> "Blancas";
        };
    }

    public String getDificultadLabel() {
        return switch (dificultadIA) {
            case FACIL -> "Fácil";
            case DIFICIL -> "Difícil";
            default -> "Normal";
        };
    }

    public String getTiempoLabel() {
        int minutos = tiempoSegundos / 60;
        return minutos + " min";
    }

    public String getModoJuego() {
        return modoJuego;
    }

    public void setModoJuego(String modoJuego) {
        this.modoJuego = modoJuego;
    }

    public int getTiempoSegundos() {
        return tiempoSegundos;
    }

    public void setTiempoSegundos(int tiempoSegundos) {
        this.tiempoSegundos = tiempoSegundos;
    }

    public String getTipoPartida() {
        return tipoPartida;
    }

    public void setTipoPartida(String tipoPartida) {
        this.tipoPartida = tipoPartida;
    }

    public String getColorJugador() {
        return colorJugador;
    }

    public void setColorJugador(String colorJugador) {
        this.colorJugador = colorJugador;
    }

    public DificultadIA getDificultadIA() {
        return dificultadIA;
    }

    public void setDificultadIA(DificultadIA dificultadIA) {
        this.dificultadIA = dificultadIA == null ? DificultadIA.NORMAL : dificultadIA;
    }

    public int getSonido() {
        return sonido;
    }

    public void setSonido(int sonido) {
        this.sonido = Math.max(0, Math.min(100, sonido));
    }

    public boolean isResaltarUltimoMovimiento() {
        return resaltarUltimoMovimiento;
    }

    public void setResaltarUltimoMovimiento(boolean resaltarUltimoMovimiento) {
        this.resaltarUltimoMovimiento = resaltarUltimoMovimiento;
    }

    public boolean isGuardarAutomaticamente() {
        return guardarAutomaticamente;
    }

    public void setGuardarAutomaticamente(boolean guardarAutomaticamente) {
        this.guardarAutomaticamente = guardarAutomaticamente;
    }
}