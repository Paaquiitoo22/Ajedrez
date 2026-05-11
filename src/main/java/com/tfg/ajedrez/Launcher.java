package com.tfg.ajedrez;

import javafx.application.Application;

/**
 * Punto de entrada principal de la aplicación.
 *
 * Funciona como una clase puente para arrancar JavaFX de forma segura.
 * Si intentáramos arrancar directamente desde una clase que hereda de
 * Application, el ejecutable (JAR) fallaría al no encontrar los módulos de
 * JavaFX a tiempo.
 */
public class Launcher {

    /**
     * Lanza la aplicación JavaFX delegando en AjedrezApplication.
     */
    public static void main(String[] args) {
        Application.launch(AjedrezApplication.class, args);
    }
}
