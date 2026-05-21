package com.agrocesar.model;

import java.time.LocalDateTime;

public class CultivoCatalogo {

    // NOT NULL (DDL garantiza)
    private Long id;
    private String nombre;
    private String categoria;           // 'TRANSITORIO' | 'PERMANENTE'
    private Double tempMin;
    private Double tempMax;
    private Double lluviaMin;
    private Double lluviaMax;
    private Double humedadMin;
    private Double humedadMax;
    private String tipoSuelo;
    private Integer diasCosechaMin;
    private Integer diasCosechaMax;
    private Integer activo;
    private LocalDateTime fechaCreacion;

    // Nullable
    private String descripcion;
    private String fuenteDatos;
    private LocalDateTime fechaActualizacion;

    // Constructor vacío (necesario para JDBI y Spring)
    public CultivoCatalogo() {}

    // Constructor completo (para mapeo JDBI desde ResultSet)
    public CultivoCatalogo(Long id, String nombre, String descripcion, String categoria,
                           Double tempMin, Double tempMax,
                           Double lluviaMin, Double lluviaMax,
                           Double humedadMin, Double humedadMax,
                           String tipoSuelo, Integer diasCosechaMin, Integer diasCosechaMax,
                           String fuenteDatos, Integer activo,
                           LocalDateTime fechaCreacion, LocalDateTime fechaActualizacion) {

        this.id                  = id;
        this.nombre              = nombre;
        this.descripcion         = descripcion;
        this.categoria           = categoria;
        this.tempMin             = tempMin;
        this.tempMax             = tempMax;
        this.lluviaMin           = lluviaMin;
        this.lluviaMax           = lluviaMax;
        this.humedadMin          = humedadMin;
        this.humedadMax          = humedadMax;
        this.tipoSuelo           = tipoSuelo;
        this.diasCosechaMin      = diasCosechaMin;
        this.diasCosechaMax      = diasCosechaMax;
        this.fuenteDatos         = fuenteDatos;
        this.activo              = activo;
        this.fechaCreacion       = fechaCreacion;
        this.fechaActualizacion  = fechaActualizacion;
    }

    // Getters
    public Long getId()                              { return id; }
    public String getNombre()                        { return nombre; }
    public String getDescripcion()                   { return descripcion; }
    public String getCategoria()                     { return categoria; }
    public Double getTempMin()                       { return tempMin; }
    public Double getTempMax()                       { return tempMax; }
    public Double getLluviaMin()                     { return lluviaMin; }
    public Double getLluviaMax()                     { return lluviaMax; }
    public Double getHumedadMin()                    { return humedadMin; }
    public Double getHumedadMax()                    { return humedadMax; }
    public String getTipoSuelo()                     { return tipoSuelo; }
    public Integer getDiasCosechaMin()               { return diasCosechaMin; }
    public Integer getDiasCosechaMax()               { return diasCosechaMax; }
    public String getFuenteDatos()                   { return fuenteDatos; }
    public Integer getActivo()                       { return activo; }
    public LocalDateTime getFechaCreacion()          { return fechaCreacion; }
    public LocalDateTime getFechaActualizacion()     { return fechaActualizacion; }

    

    // Setters
    public void setId(Long id)                                      { this.id = id; }
    public void setNombre(String nombre)                            { this.nombre = nombre; }
    public void setDescripcion(String descripcion)                  { this.descripcion = descripcion; }
    public void setCategoria(String categoria)                      { this.categoria = categoria; }
    public void setTempMin(Double tempMin)                          { this.tempMin = tempMin; }
    public void setTempMax(Double tempMax)                          { this.tempMax = tempMax; }
    public void setLluviaMin(Double lluviaMin)                      { this.lluviaMin = lluviaMin; }
    public void setLluviaMax(Double lluviaMax)                      { this.lluviaMax = lluviaMax; }
    public void setHumedadMin(Double humedadMin)                    { this.humedadMin = humedadMin; }
    public void setHumedadMax(Double humedadMax)                    { this.humedadMax = humedadMax; }
    public void setTipoSuelo(String tipoSuelo)                      { this.tipoSuelo = tipoSuelo; }
    public void setDiasCosechaMin(Integer diasCosechaMin)           { this.diasCosechaMin = diasCosechaMin; }
    public void setDiasCosechaMax(Integer diasCosechaMax)           { this.diasCosechaMax = diasCosechaMax; }
    public void setFuenteDatos(String fuenteDatos)                  { this.fuenteDatos = fuenteDatos; }
    public void setActivo(Integer activo)                           { this.activo = activo; }
    public void setFechaCreacion(LocalDateTime fechaCreacion)       { this.fechaCreacion = fechaCreacion; }
    public void setFechaActualizacion(LocalDateTime f)              { this.fechaActualizacion = f; }

    // Builder estático (reemplaza @Builder de Lombok)
    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private String nombre;
        private String descripcion;
        private String categoria;
        private Double tempMin;
        private Double tempMax;
        private Double lluviaMin;
        private Double lluviaMax;
        private Double humedadMin;
        private Double humedadMax;
        private String tipoSuelo;
        private Integer diasCosechaMin;
        private Integer diasCosechaMax;
        private String fuenteDatos;
        private Integer activo;
        private LocalDateTime fechaCreacion;
        private LocalDateTime fechaActualizacion;

        public Builder id(Long id)                                  { this.id = id; return this; }
        public Builder nombre(String nombre)                        { this.nombre = nombre; return this; }
        public Builder descripcion(String descripcion)              { this.descripcion = descripcion; return this; }
        public Builder categoria(String categoria)                  { this.categoria = categoria; return this; }
        public Builder tempMin(Double tempMin)                      { this.tempMin = tempMin; return this; }
        public Builder tempMax(Double tempMax)                      { this.tempMax = tempMax; return this; }
        public Builder lluviaMin(Double lluviaMin)                  { this.lluviaMin = lluviaMin; return this; }
        public Builder lluviaMax(Double lluviaMax)                  { this.lluviaMax = lluviaMax; return this; }
        public Builder humedadMin(Double humedadMin)                { this.humedadMin = humedadMin; return this; }
        public Builder humedadMax(Double humedadMax)                { this.humedadMax = humedadMax; return this; }
        public Builder tipoSuelo(String tipoSuelo)                  { this.tipoSuelo = tipoSuelo; return this; }
        public Builder diasCosechaMin(Integer diasCosechaMin)       { this.diasCosechaMin = diasCosechaMin; return this; }
        public Builder diasCosechaMax(Integer diasCosechaMax)       { this.diasCosechaMax = diasCosechaMax; return this; }
        public Builder fuenteDatos(String fuenteDatos)              { this.fuenteDatos = fuenteDatos; return this; }
        public Builder activo(Integer activo)                       { this.activo = activo; return this; }
        public Builder fechaCreacion(LocalDateTime f)               { this.fechaCreacion = f; return this; }
        public Builder fechaActualizacion(LocalDateTime f)          { this.fechaActualizacion = f; return this; }

        public CultivoCatalogo build() {
            return new CultivoCatalogo(id, nombre, descripcion, categoria,
                    tempMin, tempMax, lluviaMin, lluviaMax, humedadMin, humedadMax,
                    tipoSuelo, diasCosechaMin, diasCosechaMax, fuenteDatos,
                    activo, fechaCreacion, fechaActualizacion);
        }
    }
    
    @Override
    public String toString() {
        return "CultivoCatalogo{" +
            "id=" + id +
            ", nombre='" + nombre + '\'' +
            ", categoria='" + categoria + '\'' +
            ", temp=[" + tempMin + ", " + tempMax + "]" +
            ", lluvia=[" + lluviaMin + ", " + lluviaMax + "]" +
            ", humedad=[" + humedadMin + ", " + humedadMax + "]" +
            ", diasCosecha=[" + diasCosechaMin + ", " + diasCosechaMax + "]" +
            ", activo=" + activo +
            '}';
    }
}