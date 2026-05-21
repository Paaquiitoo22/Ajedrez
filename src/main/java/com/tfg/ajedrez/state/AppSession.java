package com.tfg.ajedrez.state;

import com.google.gson.JsonObject;
import com.tfg.ajedrez.persistence.GameRecord;

import java.util.Locale;

/**
 * Estado global de la aplicación en memoria durante una ejecución.
 *
 * Centraliza tres responsabilidades que necesitan ser accesibles desde
 * cualquier controlador sin tener que propagarlas como parámetros:
 * 
 * 1.La sesión del usuario autenticado o invitado.
 * 
 * 2.La configuración elegida en la pantalla "Nueva partida", para que
 * PartidaController pueda leerla al cargar el tablero.
 * 
 * 3."Cargar partida" en curso y "revisar partida del historial"
 * son peticiones que se activan desde un controlador y se
 * consumen una sola vez en el siguiente, evitando tener que pasar
 * datos por constructor entre escenas FXML.
 * 
 *
 * El estado se mantiene en variables estáticas porque JavaFX instancia los
 * controladores de cada FXML de forma independiente y no hay un contenedor
 * de inyección de dependencias disponible. La clase es final y tiene
 * constructor privado para impedir instanciación.
 */

public final class AppSession {

    private static final String GUEST_USER_ID = "guest";

    private static UserSession currentUser = UserSession.guest();
    private static NuevaPartidaSettings nuevaPartidaSettings = NuevaPartidaSettings.defaults();
    private static boolean loadSavedGameRequested = false;
    private static GameRecord reviewGameRequested = null;

    private AppSession() {
    }

    /** Restablece la sesión al modo invitado y limpia las peticiones diferidas. */
    public static void setGuestUser() {
        currentUser = UserSession.guest();
        loadSavedGameRequested = false;
        reviewGameRequested = null;
    }

    /**
     * Inicializa la sesión a partir de la respuesta JSON devuelta por Firebase
     * Identity Toolkit tras un login federado correcto.
     *
     * Si Firebase devuelve un {@code localId} válido se usa como identificador
     * del usuario; en caso contrario se cae a un identificador derivado del
     * email para no perder la asociación de partidas guardadas en disco.
     */
    public static void setFirebaseUser(JsonObject firebaseResponse) {
        if (firebaseResponse == null) {
            setGuestUser();
            return;
        }

        String email = getString(firebaseResponse, "email");
        String displayName = getString(firebaseResponse, "displayName");
        String localId = getString(firebaseResponse, "localId");

        currentUser = new UserSession(
                blankToDefault(localId, safeUserId(email)),
                blankToNull(email),
                blankToNull(displayName));
        loadSavedGameRequested = false;
        reviewGameRequested = null;
    }

    public static UserSession getCurrentUser() {
        return currentUser;
    }

    public static String getCurrentUserId() {
        return currentUser.userId();
    }

    public static String getCurrentEmail() {
        return currentUser.email();
    }

    public static String getCurrentDisplayName() {
        return currentUser.displayName();
    }

    public static String getCurrentInitials() {
        return initialsFromEmail(currentUser.email(), currentUser.displayName());
    }

    public static NuevaPartidaSettings getNuevaPartidaSettings() {
        return nuevaPartidaSettings;
    }

    public static void setNuevaPartidaSettings(NuevaPartidaSettings settings) {
        nuevaPartidaSettings = settings == null ? NuevaPartidaSettings.defaults() : settings;
    }

    // --- Peticiones diferidas entre escenas -------------------------------
    // Las dos parejas request/consume implementan un patrón de "bandera de
    // un solo uso": un controlador marca la intención (cargar partida en
    // curso o revisar una partida del historial) antes de navegar, y el
    // siguiente controlador la consume al inicializarse. Tras leerse, la
    // bandera vuelve a su estado neutro para que la siguiente navegación
    // no la reinterprete por error.

    public static void requestLoadSavedGame() {
        loadSavedGameRequested = true;
        reviewGameRequested = null;
    }

    public static boolean consumeLoadSavedGameRequest() {
        boolean requested = loadSavedGameRequested;
        loadSavedGameRequested = false;
        return requested;
    }

    public static void requestReviewGame(GameRecord record) {
        reviewGameRequested = record;
        loadSavedGameRequested = false;
    }

    public static GameRecord consumeReviewGameRequest() {
        GameRecord record = reviewGameRequested;
        reviewGameRequested = null;
        return record;
    }

    private static String getString(JsonObject object, String key) {
        if (!object.has(key) || object.get(key).isJsonNull()) {
            return null;
        }
        return object.get(key).getAsString();
    }

    private static String initialsFromEmail(String email, String displayName) {
        String source = blankToNull(email);
        if (source != null && source.contains("@")) {
            String localPart = source.substring(0, source.indexOf('@'));
            String initials = initialsFromName(localPart);
            if (!initials.isBlank()) {
                return initials;
            }
        }

        String fromName = initialsFromName(displayName);
        return fromName.isBlank() ? "US" : fromName;
    }

    private static String initialsFromName(String value) {
        String normalized = blankToNull(value);
        if (normalized == null) {
            return "";
        }

        String[] parts = normalized.split("[._\\-\\s]+");
        StringBuilder initials = new StringBuilder();

        for (String part : parts) {
            if (!part.isBlank()) {
                initials.append(part.charAt(0));
                if (initials.length() == 2) {
                    break;
                }
            }
        }

        if (initials.length() == 1 && parts.length == 1 && parts[0].length() > 1) {
            initials.append(parts[0].charAt(1));
        }

        return initials.toString().toUpperCase(Locale.ROOT);
    }

    private static String safeUserId(String email) {
        String normalized = blankToNull(email);
        if (normalized == null) {
            return GUEST_USER_ID;
        }
        return normalized.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9._-]", "_");
    }

    private static String blankToDefault(String value, String fallback) {
        String normalized = blankToNull(value);
        return normalized == null ? fallback : normalized;
    }

    private static String blankToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}
