package com.tfg.ajedrez.util;

import javax.sound.sampled.*;

public class SoundService {

    private static int volumen = 70;

    private SoundService() {}

    public static void setVolumen(int nuevoVolumen) {
        volumen = Math.max(0, Math.min(100, nuevoVolumen));
    }

    public static int getVolumen() {
        return volumen;
    }

    public static void reproducirMovimiento() {
        reproducirTonoSuave(600, 80);
    }

    public static void reproducirCaptura() {
        reproducirTonoSuave(300, 120);
    }

    public static void reproducirJaque() {
        reproducirTonoSuave(900, 150);
    }

    private static void reproducirTono(int frecuencia, int duracionMs) {
        if (volumen <= 0) return;

        new Thread(() -> {
            try {
                float sampleRate = 44100;
                byte[] buffer = new byte[(int) (duracionMs * sampleRate / 1000)];

                for (int i = 0; i < buffer.length; i++) {
                    double angle = 2.0 * Math.PI * i * frecuencia / sampleRate;
                    buffer[i] = (byte) (Math.sin(angle) * 127 * (volumen / 100.0));
                }

                AudioFormat format = new AudioFormat(sampleRate, 8, 1, true, false);
                SourceDataLine line = AudioSystem.getSourceDataLine(format);

                line.open(format);
                line.start();
                line.write(buffer, 0, buffer.length);
                line.drain();
                line.stop();
                line.close();

            } catch (Exception e) {
                System.err.println("[SONIDO] No se pudo reproducir: " + e.getMessage());
            }
        }).start();
    }
    private static void reproducirTonoSuave(int frecuencia, int duracionMs) {
        if (volumen <= 0) return;

        new Thread(() -> {
            try {
                float sampleRate = 44100;
                byte[] buffer = new byte[(int) (duracionMs * sampleRate / 1000)];

                for (int i = 0; i < buffer.length; i++) {
                    double angle = 2.0 * Math.PI * i * frecuencia / sampleRate;

                    // Fade in + fade out (esto lo hace sonar menos "pitido")
                    double envelope = Math.sin(Math.PI * i / buffer.length);

                    buffer[i] = (byte) (Math.sin(angle) * 127 * envelope * (volumen / 100.0));
                }

                AudioFormat format = new AudioFormat(sampleRate, 8, 1, true, false);
                SourceDataLine line = AudioSystem.getSourceDataLine(format);

                line.open(format);
                line.start();
                line.write(buffer, 0, buffer.length);
                line.drain();
                line.stop();
                line.close();

            } catch (Exception e) {
                System.err.println("[SONIDO] Error: " + e.getMessage());
            }
        }).start();
    }
}