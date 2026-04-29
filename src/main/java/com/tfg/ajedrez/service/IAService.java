package com.tfg.ajedrez.service;

import com.tfg.ajedrez.model.ColorPieza;
import com.tfg.ajedrez.model.MovimientoIA;
import com.tfg.ajedrez.model.Pieza;
import com.tfg.ajedrez.model.Posicion;
import com.tfg.ajedrez.model.Tablero;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class IAService {

    private final Random random = new Random();

    public MovimientoIA calcularMovimiento(Tablero tablero, ColorPieza colorIA) {
        List<MovimientoIA> capturas = new ArrayList<>();
        List<MovimientoIA> normales = new ArrayList<>();

        for (int fila = 0; fila < 8; fila++) {
            for (int col = 0; col < 8; col++) {
                Pieza pieza = tablero.getPieza(fila, col);

                if (pieza != null && pieza.getColor() == colorIA) {
                    List<Posicion> movimientos = tablero.obtenerMovimientosValidos(fila, col);

                    for (Posicion destino : movimientos) {
                        Pieza piezaDestino = tablero.getPieza(destino.getFila(), destino.getColumna());
                        boolean captura = piezaDestino != null && piezaDestino.getColor() != colorIA;

                        MovimientoIA mov = new MovimientoIA(
                                fila,
                                col,
                                destino.getFila(),
                                destino.getColumna(),
                                captura
                        );

                        if (captura) {
                            capturas.add(mov);
                        } else {
                            normales.add(mov);
                        }
                    }
                }
            }
        }

        if (!capturas.isEmpty()) {
            return capturas.get(random.nextInt(capturas.size()));
        }

        if (!normales.isEmpty()) {
            return normales.get(random.nextInt(normales.size()));
        }

        return null;
    }
}