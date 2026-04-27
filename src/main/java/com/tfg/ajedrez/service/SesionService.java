package com.tfg.ajedrez.service;

import com.tfg.ajedrez.model.Usuario;

public class SesionService {

    private static Usuario usuarioActual;
    private static boolean invitado = false;

    public static void iniciarSesion(Usuario usuario) {
        usuarioActual = usuario;
        invitado = false;
    }

    public static void entrarComoInvitado() {
        usuarioActual = null;
        invitado = true;
    }

    public static Usuario getUsuarioActual() {
        return usuarioActual;
    }

    public static boolean isInvitado() {
        return invitado;
    }

    public static void cerrarSesion() {
        usuarioActual = null;
        invitado = false;
    }
}