package com.tfg.ajedrez.model;

public class PartidaGuardada {

    private int id;
    private Integer usuarioId;
    private String fecha;
    private String turno;
    private int tiempoBlancas;
    private int tiempoNegras;
    private String estadoTablero;
    private String resultado;

    public PartidaGuardada() {
    }

    public PartidaGuardada(int id, Integer usuarioId, String fecha, String turno,
                           int tiempoBlancas, int tiempoNegras, String estadoTablero, String resultado) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.fecha = fecha;
        this.turno = turno;
        this.tiempoBlancas = tiempoBlancas;
        this.tiempoNegras = tiempoNegras;
        this.estadoTablero = estadoTablero;
        this.resultado = resultado;
    }

    public int getId() {
        return id;
    }

    public Integer getUsuarioId() {
        return usuarioId;
    }

    public String getFecha() {
        return fecha;
    }

    public String getTurno() {
        return turno;
    }

    public int getTiempoBlancas() {
        return tiempoBlancas;
    }

    public int getTiempoNegras() {
        return tiempoNegras;
    }

    public String getEstadoTablero() {
        return estadoTablero;
    }

    public String getResultado() {
        return resultado;
    }
}
