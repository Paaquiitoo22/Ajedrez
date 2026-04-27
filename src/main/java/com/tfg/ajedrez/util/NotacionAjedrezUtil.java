package com.tfg.ajedrez.util;

import com.tfg.ajedrez.model.MovimientoInfo;
import com.tfg.ajedrez.model.TipoPieza;

public class NotacionAjedrezUtil {

    public static String formatearMovimiento(int numero, MovimientoInfo mov) {
        if (mov.isEnroque()) {
            return numero + ". " + (mov.getColDestino() == 6 ? "Enroque corto" : "Enroque largo");
        }

        String nombrePieza = nombrePieza(mov.getTipoPieza());
        String origen = convertirCasilla(mov.getFilaOrigen(), mov.getColOrigen());
        String destino = convertirCasilla(mov.getFilaDestino(), mov.getColDestino());
        String separador = mov.isCaptura() ? " x " : " -> ";

        String texto = numero + ". " + nombrePieza + " " + origen + separador + destino;

        if (mov.isPromocion()) {
            texto += " = Dama";
        }

        return texto;
    }

    private static String convertirCasilla(int fila, int col) {
        char letra = (char) ('a' + col);
        int numero = 8 - fila;
        return "" + letra + numero;
    }

    private static String nombrePieza(TipoPieza tipo) {
        return switch (tipo) {
            case REY -> "Rey";
            case DAMA -> "Dama";
            case TORRE -> "Torre";
            case ALFIL -> "Alfil";
            case CABALLO -> "Caballo";
            case PEON -> "Peón";
        };
    }
}