package com.tfg.ajedrez.state;

import javafx.scene.Parent;

/**
 * Gestor central de los temas claro y oscuro de la aplicación.
 *
 * Para el cambio de tema se añade o se quita una clase CSS
 * "theme-dark"/"theme-light".
 * 
 * styles.css define todos los selectores con el prefijo
 * "theme-light" para sobrescribir los colores cuando esa clase
 * está activa, dejando el oscuro como tema por defecto.
 *
 * Las variables son estáticas porque es una característica global de la
 * aplicación.
 */
public final class ThemeManager {

    private static final String THEME_DARK = "theme-dark";
    private static final String THEME_LIGHT = "theme-light";

    /** El tema por defecto es oscuro */
    private static boolean darkTheme = true;

    private ThemeManager() {
    }

    public static boolean isDarkTheme() {
        return darkTheme;
    }

    /** Alterna entre tema claro y oscuro sin aplicar el cambio visual. */
    public static void toggleTheme() {
        darkTheme = !darkTheme;
    }

    /**
     * Aplica el tema actual al árbol de nodos pasado como raíz.
     */
    public static void applyTheme(Parent root) {
        if (root == null) {
            return;
        }

        // Se eliminan ambas clases antes de añadir la actual para evitar que
        // queden las dos a la vez si se invoca varias veces seguidas.
        root.getStyleClass().removeAll(THEME_DARK, THEME_LIGHT);
        root.getStyleClass().add(darkTheme ? THEME_DARK : THEME_LIGHT);
    }

    /**
     * Texto del botón de cambio de tema, ahí se indica la acción que
     * se aplicará al pulsarlo, no el estado actual.
     */
    public static String getMenuLabel() {
        return darkTheme ? "Tema claro" : "Tema oscuro";
    }
}
