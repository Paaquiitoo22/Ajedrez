package com.tfg.ajedrez.state;

/**
 * Información inmutable de la sesión del usuario actual.
 *
 * Se implementa como "record" porque la sesión es un valor: una vez
 * creada con los datos devueltos por Firebase no se modifica; un nuevo login
 * o un cierre de sesión producirá una instancia distinta.
 *
 * @param userId      es un identificador único que coincide con el localId de
 *                    Firebase para usuarios autenticados o con "guest" para
 *                    el modo invitado
 * @param email       correo del usuario o "null" si no se conoce.
 * @param displayName nombre visible o "null" si Firebase no lo devuelve.
 */
public record UserSession(String userId, String email, String displayName) {

    /** Sesión usada cuando el usuario entra sin autenticarse. */
    public static UserSession guest() {
        return new UserSession("guest", null, "Invitado");
    }
}
