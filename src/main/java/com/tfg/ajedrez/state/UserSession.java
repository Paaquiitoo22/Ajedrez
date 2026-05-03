package com.tfg.ajedrez.state;

public record UserSession(String userId, String email, String displayName) {

    public static UserSession guest() {
        return new UserSession("guest", null, "Invitado");
    }
}
