package com.agrocesar.model;

import java.time.LocalDate;

public class TipoActividad {

    private Long id;
    private String nombre;
    private String icono;
    private Integer activo;
    private LocalDate fechaCreacion;

    private TipoActividad() {}

    public TipoActividad(Long id, String nombre, String icono, Integer activo, LocalDate fechaCreacion) {
        this.id = id;
        this.nombre = nombre;
        this.icono = icono;
        this.activo = activo;
        this.fechaCreacion = fechaCreacion;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final TipoActividad o = new TipoActividad();

        public Builder id(Long v)                { o.id = v;            return this; }
        public Builder nombre(String v)          { o.nombre = v;        return this; }
        public Builder icono(String v)           { o.icono = v;         return this; }
        public Builder activo(Integer v)         { o.activo = v;        return this; }
        public Builder fechaCreacion(LocalDate v){ o.fechaCreacion = v; return this; }
        
        public TipoActividad build()             { return o; }
    }

    public Long getId()                { return id; }
    public String getNombre()          { return nombre; }
    public String getIcono()           { return icono; }
    public Integer getActivo()         { return activo; }
    public LocalDate getFechaCreacion(){ return fechaCreacion; }
}
