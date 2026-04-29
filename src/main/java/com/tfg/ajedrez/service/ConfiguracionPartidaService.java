package com.tfg.ajedrez.service;

/**
 * "Memoria" estática compartida entre la pantalla de Nueva Partida y la pantalla
 * de Partida. Como nuestro SceneManager no permite pasar parámetros al cargar
 * un FXML, usamos esta clase como puente para llevar la configuración elegida
 * por el usuario hasta el PartidaController.
 *
 * Uso:
 *   - NuevaPartidaController escribe los valores antes de navegar.
 *   - PartidaController los lee en initialize().
 */
public class ConfiguracionPartidaService {

    /** true = Jugador vs IA, false = 2 jugadores. */
    public static boolean modoIA = false;

    /** Tiempo inicial por jugador en segundos (10 minutos por defecto). */
    public static int tiempoInicialSegundos = 600;
}
