package com.tfg.ajedrez.state;

import javafx.scene.Parent;

public final class ThemeManager {

    private static final String THEME_DARK = "theme-dark";
    private static final String THEME_LIGHT = "theme-light";

    private static boolean darkTheme = true;

    private ThemeManager() {
    }

    public static boolean isDarkTheme() {
        return darkTheme;
    }

    public static void toggleTheme() {
        darkTheme = !darkTheme;
    }

    public static void applyTheme(Parent root) {
        if (root == null) {
            return;
        }

        root.getStyleClass().removeAll(THEME_DARK, THEME_LIGHT);
        root.getStyleClass().add(darkTheme ? THEME_DARK : THEME_LIGHT);
    }

    public static String getMenuLabel() {
        return darkTheme ? "Tema claro" : "Tema oscuro";
    }
}
