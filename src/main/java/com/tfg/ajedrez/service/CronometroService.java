package com.tfg.ajedrez.service;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

public class CronometroService {

    private int tiempoBlancas;
    private int tiempoNegras;
    private boolean turnoBlancas = true;
    private boolean pausado = false;

    private final Runnable onTick;
    private final Runnable onFinTiempo;

    private final Timeline timeline;

    public CronometroService(int segundosIniciales, Runnable onTick, Runnable onFinTiempo) {
        this.tiempoBlancas = segundosIniciales;
        this.tiempoNegras = segundosIniciales;
        this.onTick = onTick;
        this.onFinTiempo = onFinTiempo;

        this.timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> actualizar()));
        this.timeline.setCycleCount(Timeline.INDEFINITE);
    }

    private void actualizar() {
        if (pausado) {
            return;
        }

        if (turnoBlancas) {
            tiempoBlancas--;
            if (tiempoBlancas <= 0) {
                tiempoBlancas = 0;
                detener();
                if (onTick != null) onTick.run();
                if (onFinTiempo != null) onFinTiempo.run();
                return;
            }
        } else {
            tiempoNegras--;
            if (tiempoNegras <= 0) {
                tiempoNegras = 0;
                detener();
                if (onTick != null) onTick.run();
                if (onFinTiempo != null) onFinTiempo.run();
                return;
            }
        }

        if (onTick != null) {
            onTick.run();
        }
    }

    public void iniciar() {
        timeline.play();
    }

    public void detener() {
        timeline.stop();
    }

    public void pausar() {
        pausado = true;
    }

    public void reanudar() {
        pausado = false;
    }

    public void cambiarTurno() {
        turnoBlancas = !turnoBlancas;
    }

    public boolean isTurnoBlancas() {
        return turnoBlancas;
    }

    public boolean isPausado() {
        return pausado;
    }

    public int getTiempoBlancas() {
        return tiempoBlancas;
    }

    public int getTiempoNegras() {
        return tiempoNegras;
    }

    public void setTiempoBlancas(int tiempoBlancas) {
        this.tiempoBlancas = tiempoBlancas;
    }

    public void setTiempoNegras(int tiempoNegras) {
        this.tiempoNegras = tiempoNegras;
    }

    public String getTiempoBlancasFormateado() {
        return formatear(tiempoBlancas);
    }

    public String getTiempoNegrasFormateado() {
        return formatear(tiempoNegras);
    }

    private String formatear(int segundos) {
        int min = segundos / 60;
        int seg = segundos % 60;
        return String.format("%02d:%02d", min, seg);
    }
}
