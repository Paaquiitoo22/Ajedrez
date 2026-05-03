package com.tfg.ajedrez.state;

import com.google.gson.JsonObject;
import com.tfg.ajedrez.persistence.GameRecord;

import java.util.Locale;

public final class AppSession {

    private static final String GUEST_USER_ID = "guest";

    private static UserSession currentUser = UserSession.guest();
    private static NuevaPartidaSettings nuevaPartidaSettings = NuevaPartidaSettings.defaults();
    private static boolean loadSavedGameRequested = false;
    private static GameRecord reviewGameRequested = null;

    private AppSession() {
    }

    public static void setGuestUser() {
        currentUser = UserSession.guest();
        loadSavedGameRequested = false;
        reviewGameRequested = null;
    }

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
                blankToNull(displayName)
        );
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
