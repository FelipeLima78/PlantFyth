package com.network.plantfyth.Model;

import java.util.Calendar;

public class EventoCalendario {

    public enum TipoEvento {
        IRRIGACAO,
        ADUBACAO,
        PODA
    }

    private String nomePlanta;
    private TipoEvento tipo;
    private Calendar data;

    public EventoCalendario(String nomePlanta, TipoEvento tipo, Calendar data) {
        this.nomePlanta = nomePlanta;
        this.tipo = tipo;
        this.data = data;
    }

    public String getNomePlanta() { return nomePlanta; }
    public TipoEvento getTipo()   { return tipo; }
    public Calendar getData()     { return data; }
}