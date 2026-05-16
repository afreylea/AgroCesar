package com.agrocesar.model;

import java.time.LocalDate;

public class Municipio {

    private Long id;
    private String nombre;
    private String departamento;
    private Double latitud;
    private Double longitud;
    private Integer activo;
    private LocalDate fechaCreacion;

    public Municipio () { }

    public Municipio (Long id, String nombre, String departamento, 
                      Double latitud, Double longitud, Integer activo, 
                      LocalDate fechaCreacion ) {
        
        this.id            = id;
        this.nombre        = nombre;
        this.departamento  = departamento;
        this.latitud       = latitud;
        this.longitud      = longitud;
        this.activo        = activo;
        this.fechaCreacion = fechaCreacion;               
    }

    public Long getId ()                     { return id; }
    public String getNombre ()               { return nombre; }
    public String getDepartamento ()         { return departamento; }
    public Double getLatitud ()              { return latitud; }
    public Double getLongitud ()             { return longitud; }
    public Integer getActivo ()              { return activo; }
    public LocalDate getFechaCreacion ()     { return fechaCreacion; }

    public boolean isActivo()                { return Integer.valueOf(1).equals(this.activo); }

    public void setId (Long id)                                {this.id = id; }
    public void setNombre (String nombre)                      { this.nombre = nombre; }
    public void setDepartamento (String departamento)          { this.departamento = departamento; }
    public void setLatitud (Double latitud)                    { this.latitud = latitud; }
    public void setLongitud (Double longitud)                  { this.longitud = longitud; }
    public void setActivo (Integer activo)                     { this.activo = activo; }
    public void setFechaCreacion (LocalDate fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public static Builder builder () { return new Builder(); }

    public static class Builder {

        private Long id;
        private String nombre;
        private String departamento;
        private Double latitud;
        private Double longitud;
        private Integer activo;
        private LocalDate fechaCreacion;

        public Builder id (Long id)                                { this.id = id; return this; }
        public Builder nombre (String nombre)                      { this.nombre = nombre; return this; }
        public Builder departamento (String departamento)          { this.departamento = departamento; return this; }
        public Builder latitud (Double latitud)                    { this.latitud = latitud; return this; }
        public Builder longitud (Double longitud)                  { this.longitud = longitud; return this;}
        public Builder activo (Integer activo)                     { this.activo = activo; return this; }
        public Builder fechaCreacion (LocalDate fechaCreacion)     { this.fechaCreacion = fechaCreacion; return this; }

        public Municipio build () {
            return new Municipio(id, nombre, departamento, latitud,
                                 longitud, activo, fechaCreacion );
        }
    }

    @Override
    public String toString() {
        return "Municipio{" +
            "id=" + id +
            ", nombre='" + nombre + '\'' +
            ", departamento='" + departamento + '\'' +
            ", latitud=" + latitud +
            ", longitud=" + longitud +
            ", activo=" + activo +
            '}';
    }
}