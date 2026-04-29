package com.tfg.ajedrez.model;

import java.util.ArrayList;
import java.util.List;

public class Tablero {

    private final Pieza[][] casillas;
    private Posicion peonVulnerableAlPaso = null;

    public Tablero() {
        casillas = new Pieza[8][8];
        inicializarPiezas();
    }

    public Pieza getPieza(int fila, int columna) {
        return casillas[fila][columna];
    }

    public Pieza[][] getCasillas() {
        return casillas;
    }

    public void limpiarTablero() {
        for (int fila = 0; fila < 8; fila++) {
            for (int col = 0; col < 8; col++) {
                casillas[fila][col] = null;
            }
        }
    }

    public MovimientoInfo moverPieza(int filaOrigen, int colOrigen, int filaDestino, int colDestino) {
        if (!dentroTablero(filaOrigen, colOrigen) || !dentroTablero(filaDestino, colDestino)) {
            return null;
        }

        Pieza piezaOrigen = casillas[filaOrigen][colOrigen];

        if (piezaOrigen == null) {
            return null;
        }

        if (filaOrigen == filaDestino && colOrigen == colDestino) {
            return null;
        }

        Pieza piezaDestino = casillas[filaDestino][colDestino];

        if (piezaDestino != null && piezaDestino.getColor() == piezaOrigen.getColor()) {
            return null;
        }

        if (!movimientoValidoSinDejarJaque(piezaOrigen, filaOrigen, colOrigen, filaDestino, colDestino)) {
            return null;
        }

        boolean capturaAlPaso = esCapturaAlPaso(piezaOrigen, filaOrigen, colOrigen, filaDestino, colDestino);
        boolean captura = piezaDestino != null || capturaAlPaso;
        boolean enroque = piezaOrigen.getTipo() == TipoPieza.REY && Math.abs(colDestino - colOrigen) == 2;
        boolean promocion = false;

        TipoPieza tipoAntesDeMover = piezaOrigen.getTipo();

        if (capturaAlPaso) {
            casillas[filaOrigen][colDestino] = null;
        }

        casillas[filaDestino][colDestino] = piezaOrigen;
        casillas[filaOrigen][colOrigen] = null;

        piezaOrigen.setPosicion(new Posicion(filaDestino, colDestino));
        piezaOrigen.setMovida(true);

        if (enroque) {
            moverTorreEnEnroque(filaOrigen, colOrigen, filaDestino, colDestino);
        }

        if (piezaOrigen.getTipo() == TipoPieza.PEON) {
            if ((piezaOrigen.getColor() == ColorPieza.BLANCA && filaDestino == 0)
                    || (piezaOrigen.getColor() == ColorPieza.NEGRA && filaDestino == 7)) {
                piezaOrigen.setTipo(TipoPieza.DAMA);
                promocion = true;
            }
        }

        actualizarPeonVulnerableAlPaso(piezaOrigen, filaOrigen, filaDestino, colDestino);

        return new MovimientoInfo(
                tipoAntesDeMover,
                piezaOrigen.getColor(),
                filaOrigen,
                colOrigen,
                filaDestino,
                colDestino,
                captura,
                enroque,
                promocion
        );
    }

    private void moverTorreEnEnroque(int filaOrigen, int colOrigen, int filaDestino, int colDestino) {
        if (colDestino == 6) {
            Pieza torre = casillas[filaOrigen][7];
            casillas[filaOrigen][5] = torre;
            casillas[filaOrigen][7] = null;

            if (torre != null) {
                torre.setPosicion(new Posicion(filaOrigen, 5));
                torre.setMovida(true);
            }
        } else if (colDestino == 2) {
            Pieza torre = casillas[filaOrigen][0];
            casillas[filaOrigen][3] = torre;
            casillas[filaOrigen][0] = null;

            if (torre != null) {
                torre.setPosicion(new Posicion(filaOrigen, 3));
                torre.setMovida(true);
            }
        }
    }

    public List<Posicion> obtenerMovimientosValidos(int fila, int col) {
        List<Posicion> movimientos = new ArrayList<>();

        Pieza pieza = getPieza(fila, col);

        if (pieza == null) {
            return movimientos;
        }

        for (int f = 0; f < 8; f++) {
            for (int c = 0; c < 8; c++) {
                if (fila == f && col == c) {
                    continue;
                }

                Pieza destino = getPieza(f, c);

                if (destino != null && destino.getColor() == pieza.getColor()) {
                    continue;
                }

                if (movimientoValidoSinDejarJaque(pieza, fila, col, f, c)) {
                    movimientos.add(new Posicion(f, c));
                }
            }
        }

        return movimientos;
    }

    public boolean estaEnJaque(ColorPieza color) {
        Posicion rey = buscarRey(color);

        if (rey == null) {
            return false;
        }

        ColorPieza rival = color == ColorPieza.BLANCA ? ColorPieza.NEGRA : ColorPieza.BLANCA;

        for (int fila = 0; fila < 8; fila++) {
            for (int col = 0; col < 8; col++) {
                Pieza pieza = casillas[fila][col];

                if (pieza != null && pieza.getColor() == rival) {
                    if (movimientoAtaqueValido(pieza, fila, col, rey.getFila(), rey.getColumna())) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public boolean tieneMovimientosLegales(ColorPieza color) {
        for (int fila = 0; fila < 8; fila++) {
            for (int col = 0; col < 8; col++) {
                Pieza pieza = casillas[fila][col];

                if (pieza != null && pieza.getColor() == color) {
                    if (!obtenerMovimientosValidos(fila, col).isEmpty()) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public String exportarEstado() {
        StringBuilder sb = new StringBuilder();

        for (int fila = 0; fila < 8; fila++) {
            for (int col = 0; col < 8; col++) {
                Pieza pieza = casillas[fila][col];

                if (pieza == null) {
                    sb.append("..");
                } else {
                    sb.append(codigoPieza(pieza));
                }

                if (col < 7) {
                    sb.append(" ");
                }
            }

            if (fila < 7) {
                sb.append("\n");
            }
        }

        return sb.toString();
    }

    public void importarEstado(String estado) {
        limpiarTablero();

        String[] filas = estado.split("\\n");

        for (int fila = 0; fila < filas.length && fila < 8; fila++) {
            String[] columnas = filas[fila].trim().split("\\s+");

            for (int col = 0; col < columnas.length && col < 8; col++) {
                String codigo = columnas[col];

                if (!codigo.equals("..")) {
                    casillas[fila][col] = crearPiezaDesdeCodigo(codigo, fila, col);
                }
            }
        }
    }

    private boolean movimientoValidoSinDejarJaque(Pieza pieza, int fo, int co, int fd, int cd) {
        if (!movimientoBasicoValido(pieza, fo, co, fd, cd)) {
            return false;
        }

        return !dejaEnJaque(pieza.getColor(), fo, co, fd, cd);
    }

    private boolean dejaEnJaque(ColorPieza color, int fo, int co, int fd, int cd) {
        Pieza origen = casillas[fo][co];
        Pieza destino = casillas[fd][cd];

        boolean capturaAlPaso = esCapturaAlPaso(origen, fo, co, fd, cd);
        Pieza peonCapturadoAlPaso = null;

        if (capturaAlPaso) {
            peonCapturadoAlPaso = casillas[fo][cd];
            casillas[fo][cd] = null;
        }

        casillas[fd][cd] = origen;
        casillas[fo][co] = null;

        Posicion posicionOriginal = origen.getPosicion();
        origen.setPosicion(new Posicion(fd, cd));

        boolean enJaque = estaEnJaque(color);

        casillas[fo][co] = origen;
        casillas[fd][cd] = destino;
        origen.setPosicion(posicionOriginal);

        if (capturaAlPaso) {
            casillas[fo][cd] = peonCapturadoAlPaso;
        }

        return enJaque;
    }

    private Posicion buscarRey(ColorPieza color) {
        for (int fila = 0; fila < 8; fila++) {
            for (int col = 0; col < 8; col++) {
                Pieza pieza = casillas[fila][col];

                if (pieza != null && pieza.getColor() == color && pieza.getTipo() == TipoPieza.REY) {
                    return new Posicion(fila, col);
                }
            }
        }

        return null;
    }

    private boolean movimientoAtaqueValido(Pieza pieza, int fo, int co, int fd, int cd) {
        return switch (pieza.getTipo()) {
            case PEON -> validarAtaquePeon(pieza, fo, co, fd, cd);
            case TORRE -> validarTorre(fo, co, fd, cd);
            case CABALLO -> validarCaballo(fo, co, fd, cd);
            case ALFIL -> validarAlfil(fo, co, fd, cd);
            case DAMA -> validarDama(fo, co, fd, cd);
            case REY -> validarReySimple(fo, co, fd, cd);
        };
    }

    private boolean movimientoBasicoValido(Pieza pieza, int fo, int co, int fd, int cd) {
        return switch (pieza.getTipo()) {
            case PEON -> validarPeon(pieza, fo, co, fd, cd);
            case TORRE -> validarTorre(fo, co, fd, cd);
            case CABALLO -> validarCaballo(fo, co, fd, cd);
            case ALFIL -> validarAlfil(fo, co, fd, cd);
            case DAMA -> validarDama(fo, co, fd, cd);
            case REY -> validarRey(pieza, fo, co, fd, cd);
        };
    }

    private boolean validarPeon(Pieza pieza, int fo, int co, int fd, int cd) {
        int direccion = pieza.getColor() == ColorPieza.BLANCA ? -1 : 1;
        int filaInicial = pieza.getColor() == ColorPieza.BLANCA ? 6 : 1;

        Pieza destino = casillas[fd][cd];

        if (co == cd && fd == fo + direccion && destino == null) {
            return true;
        }

        if (co == cd
                && fo == filaInicial
                && fd == fo + 2 * direccion
                && destino == null
                && casillas[fo + direccion][co] == null) {
            return true;
        }

        if (Math.abs(cd - co) == 1
                && fd == fo + direccion
                && destino != null
                && destino.getColor() != pieza.getColor()) {
            return true;
        }

        return esCapturaAlPaso(pieza, fo, co, fd, cd);
    }

    private boolean validarAtaquePeon(Pieza pieza, int fo, int co, int fd, int cd) {
        int direccion = pieza.getColor() == ColorPieza.BLANCA ? -1 : 1;

        return Math.abs(cd - co) == 1 && fd == fo + direccion;
    }

    private boolean esCapturaAlPaso(Pieza pieza, int fo, int co, int fd, int cd) {
        if (pieza == null || pieza.getTipo() != TipoPieza.PEON) {
            return false;
        }

        if (peonVulnerableAlPaso == null) {
            return false;
        }

        int direccion = pieza.getColor() == ColorPieza.BLANCA ? -1 : 1;

        return Math.abs(cd - co) == 1
                && fd == fo + direccion
                && peonVulnerableAlPaso.getFila() == fo
                && peonVulnerableAlPaso.getColumna() == cd
                && casillas[fo][cd] != null
                && casillas[fo][cd].getTipo() == TipoPieza.PEON
                && casillas[fo][cd].getColor() != pieza.getColor();
    }

    private void actualizarPeonVulnerableAlPaso(Pieza pieza, int filaOrigen, int filaDestino, int colDestino) {
        if (pieza.getTipo() == TipoPieza.PEON && Math.abs(filaDestino - filaOrigen) == 2) {
            peonVulnerableAlPaso = new Posicion(filaDestino, colDestino);
        } else {
            peonVulnerableAlPaso = null;
        }
    }

    private boolean validarTorre(int fo, int co, int fd, int cd) {
        if (fo != fd && co != cd) {
            return false;
        }

        if (fo == fd) {
            int inicio = Math.min(co, cd) + 1;
            int fin = Math.max(co, cd);

            for (int c = inicio; c < fin; c++) {
                if (casillas[fo][c] != null) {
                    return false;
                }
            }
        }

        if (co == cd) {
            int inicio = Math.min(fo, fd) + 1;
            int fin = Math.max(fo, fd);

            for (int f = inicio; f < fin; f++) {
                if (casillas[f][co] != null) {
                    return false;
                }
            }
        }

        return true;
    }

    private boolean validarCaballo(int fo, int co, int fd, int cd) {
        int filaDiff = Math.abs(fd - fo);
        int colDiff = Math.abs(cd - co);

        return (filaDiff == 2 && colDiff == 1) || (filaDiff == 1 && colDiff == 2);
    }

    private boolean validarAlfil(int fo, int co, int fd, int cd) {
        if (Math.abs(fd - fo) != Math.abs(cd - co)) {
            return false;
        }

        int pasoFila = fd > fo ? 1 : -1;
        int pasoCol = cd > co ? 1 : -1;

        int f = fo + pasoFila;
        int c = co + pasoCol;

        while (f != fd && c != cd) {
            if (casillas[f][c] != null) {
                return false;
            }

            f += pasoFila;
            c += pasoCol;
        }

        return true;
    }

    private boolean validarDama(int fo, int co, int fd, int cd) {
        return validarTorre(fo, co, fd, cd) || validarAlfil(fo, co, fd, cd);
    }

    private boolean validarRey(Pieza rey, int fo, int co, int fd, int cd) {
        int filaDiff = Math.abs(fd - fo);
        int colDiff = Math.abs(cd - co);

        if (filaDiff <= 1 && colDiff <= 1) {
            return true;
        }

        return validarEnroque(rey, fo, co, fd, cd);
    }

    private boolean validarReySimple(int fo, int co, int fd, int cd) {
        int filaDiff = Math.abs(fd - fo);
        int colDiff = Math.abs(cd - co);

        return filaDiff <= 1 && colDiff <= 1;
    }

    private boolean validarEnroque(Pieza rey, int fo, int co, int fd, int cd) {
        if (rey.isMovida()) {
            return false;
        }

        if (fo != fd) {
            return false;
        }

        if (co != 4) {
            return false;
        }

        if (estaEnJaque(rey.getColor())) {
            return false;
        }

        if (cd == 6) {
            Pieza torre = casillas[fo][7];

            if (torre == null || torre.getTipo() != TipoPieza.TORRE || torre.isMovida()) {
                return false;
            }

            if (casillas[fo][5] != null || casillas[fo][6] != null) {
                return false;
            }

            if (dejaEnJaque(rey.getColor(), fo, co, fo, 5)) {
                return false;
            }

            if (dejaEnJaque(rey.getColor(), fo, co, fo, 6)) {
                return false;
            }

            return true;
        }

        if (cd == 2) {
            Pieza torre = casillas[fo][0];

            if (torre == null || torre.getTipo() != TipoPieza.TORRE || torre.isMovida()) {
                return false;
            }

            if (casillas[fo][1] != null || casillas[fo][2] != null || casillas[fo][3] != null) {
                return false;
            }

            if (dejaEnJaque(rey.getColor(), fo, co, fo, 3)) {
                return false;
            }

            if (dejaEnJaque(rey.getColor(), fo, co, fo, 2)) {
                return false;
            }

            return true;
        }

        return false;
    }

    private Pieza crearPiezaDesdeCodigo(String codigo, int fila, int col) {
        ColorPieza color = codigo.charAt(0) == 'B' ? ColorPieza.BLANCA : ColorPieza.NEGRA;
        char tipoChar = codigo.charAt(1);

        TipoPieza tipo = switch (tipoChar) {
            case 'R' -> TipoPieza.REY;
            case 'D' -> TipoPieza.DAMA;
            case 'T' -> TipoPieza.TORRE;
            case 'A' -> TipoPieza.ALFIL;
            case 'C' -> TipoPieza.CABALLO;
            case 'P' -> TipoPieza.PEON;
            default -> null;
        };

        if (tipo == null) {
            return null;
        }

        return new Pieza(tipo, color, new Posicion(fila, col));
    }

    private String codigoPieza(Pieza pieza) {
        String color = pieza.getColor() == ColorPieza.BLANCA ? "B" : "N";

        String tipo = switch (pieza.getTipo()) {
            case REY -> "R";
            case DAMA -> "D";
            case TORRE -> "T";
            case ALFIL -> "A";
            case CABALLO -> "C";
            case PEON -> "P";
        };

        return color + tipo;
    }

    private boolean dentroTablero(int fila, int col) {
        return fila >= 0 && fila < 8 && col >= 0 && col < 8;
    }

    private void inicializarPiezas() {
        for (int col = 0; col < 8; col++) {
            casillas[1][col] = new Pieza(TipoPieza.PEON, ColorPieza.NEGRA, new Posicion(1, col));
            casillas[6][col] = new Pieza(TipoPieza.PEON, ColorPieza.BLANCA, new Posicion(6, col));
        }

        casillas[0][0] = new Pieza(TipoPieza.TORRE, ColorPieza.NEGRA, new Posicion(0, 0));
        casillas[0][7] = new Pieza(TipoPieza.TORRE, ColorPieza.NEGRA, new Posicion(0, 7));
        casillas[7][0] = new Pieza(TipoPieza.TORRE, ColorPieza.BLANCA, new Posicion(7, 0));
        casillas[7][7] = new Pieza(TipoPieza.TORRE, ColorPieza.BLANCA, new Posicion(7, 7));

        casillas[0][1] = new Pieza(TipoPieza.CABALLO, ColorPieza.NEGRA, new Posicion(0, 1));
        casillas[0][6] = new Pieza(TipoPieza.CABALLO, ColorPieza.NEGRA, new Posicion(0, 6));
        casillas[7][1] = new Pieza(TipoPieza.CABALLO, ColorPieza.BLANCA, new Posicion(7, 1));
        casillas[7][6] = new Pieza(TipoPieza.CABALLO, ColorPieza.BLANCA, new Posicion(7, 6));

        casillas[0][2] = new Pieza(TipoPieza.ALFIL, ColorPieza.NEGRA, new Posicion(0, 2));
        casillas[0][5] = new Pieza(TipoPieza.ALFIL, ColorPieza.NEGRA, new Posicion(0, 5));
        casillas[7][2] = new Pieza(TipoPieza.ALFIL, ColorPieza.BLANCA, new Posicion(7, 2));
        casillas[7][5] = new Pieza(TipoPieza.ALFIL, ColorPieza.BLANCA, new Posicion(7, 5));

        casillas[0][3] = new Pieza(TipoPieza.DAMA, ColorPieza.NEGRA, new Posicion(0, 3));
        casillas[7][3] = new Pieza(TipoPieza.DAMA, ColorPieza.BLANCA, new Posicion(7, 3));

        casillas[0][4] = new Pieza(TipoPieza.REY, ColorPieza.NEGRA, new Posicion(0, 4));
        casillas[7][4] = new Pieza(TipoPieza.REY, ColorPieza.BLANCA, new Posicion(7, 4));
    }
}