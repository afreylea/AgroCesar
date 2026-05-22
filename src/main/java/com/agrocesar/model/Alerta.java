package com.agrocesar.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Alerta {

    private Long id;
    private Long cultivoAgricultorId;
    private String tipoAlerta;      // 'TEMPERATURA_ALTA' | 'TEMPERATURA_BAJA' |
                                    // 'LLUVIA_EXCESIVA'  | 'LLUVIA_INSUFICIENTE' |
                                    // 'HUMEDAD_EXCESIVA' | 'HUMEDAD_INSUFICIENTE'
    private String severidad;       // 'ALTA' | 'MEDIA' | 'BAJA'
    private String descripcion;
    private LocalDate fechaDiaPronostico;   // Día del pronóstico que disparó la alerta
    private LocalDateTime fechaGeneracion;
    private Double valorDetectado;          // Valor real del pronóstico (ej: 92.5 mm)
    private Double valorUmbral;             // Umbral efectivo aplicado (ej: 80 mm)
    private String recomendacion;
    private Integer leida;                  // 0 = no leída | 1 = leída

    private LocalDateTime fechaLectura;

    public Alerta() {}

    public Alerta(Long id, Long cultivoAgricultorId, String tipoAlerta, String severidad,
                  String descripcion, LocalDate fechaDiaPronostico,
                  LocalDateTime fechaGeneracion, Double valorDetectado, Double valorUmbral,
                  String recomendacion, Integer leida, LocalDateTime fechaLectura) {

        this.id                  = id;
        this.cultivoAgricultorId = cultivoAgricultorId;
        this.tipoAlerta          = tipoAlerta;
        this.severidad           = severidad;
        this.descripcion         = descripcion;
        this.fechaDiaPronostico  = fechaDiaPronostico;
        this.fechaGeneracion     = fechaGeneracion;
        this.valorDetectado      = valorDetectado;
        this.valorUmbral         = valorUmbral;
        this.recomendacion       = recomendacion;
        this.leida               = leida;
        this.fechaLectura        = fechaLectura;
    }

    // Getters
    public Long getId()                              { return id; }
    public Long getCultivoAgricultorId()             { return cultivoAgricultorId; }
    public String getTipoAlerta()                    { return tipoAlerta; }
    public String getSeveridad()                     { return severidad; }
    public String getDescripcion()                   { return descripcion; }
    public LocalDate getFechaDiaPronostico()         { return fechaDiaPronostico; }
    public LocalDateTime getFechaGeneracion()        { return fechaGeneracion; }
    public Double getValorDetectado()                { return valorDetectado; }
    public Double getValorUmbral()                   { return valorUmbral; }
    public String getRecomendacion()                 { return recomendacion; }
    public Integer getLeida()                        { return leida; }
    public LocalDateTime getFechaLectura()           { return fechaLectura; }

    public boolean isLeida()                          { return Integer.valueOf(1).equals(this.leida); }

    // Setters
    public void setId(Long id)                                          { this.id = id; }
    public void setCultivoAgricultorId(Long cultivoAgricultorId)        { this.cultivoAgricultorId = cultivoAgricultorId; }
    public void setTipoAlerta(String tipoAlerta)                        { this.tipoAlerta = tipoAlerta; }
    public void setSeveridad(String severidad)                          { this.severidad = severidad; }
    public void setDescripcion(String descripcion)                      { this.descripcion = descripcion; }
    public void setFechaDiaPronostico(LocalDate fechaDiaPronostico)     { this.fechaDiaPronostico = fechaDiaPronostico; }
    public void setFechaGeneracion(LocalDateTime fechaGeneracion)       { this.fechaGeneracion = fechaGeneracion; }
    public void setValorDetectado(Double valorDetectado)                { this.valorDetectado = valorDetectado; }
    public void setValorUmbral(Double valorUmbral)                      { this.valorUmbral = valorUmbral; }
    public void setRecomendacion(String recomendacion)                  { this.recomendacion = recomendacion; }
    public void setLeida(Integer leida)                                 { this.leida = leida; }
    public void setFechaLectura(LocalDateTime fechaLectura)             { this.fechaLectura = fechaLectura; }

    // Builder estático (reemplaza @Builder de Lombok)
    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private Long cultivoAgricultorId;
        private String tipoAlerta;
        private String severidad;
        private String descripcion;
        private LocalDate fechaDiaPronostico;
        private LocalDateTime fechaGeneracion;
        private Double valorDetectado;
        private Double valorUmbral;
        private String recomendacion;
        private Integer leida;
        private LocalDateTime fechaLectura;

        public Builder id(Long id)                                          { this.id = id; return this; }
        public Builder cultivoAgricultorId(Long cultivoAgricultorId)        { this.cultivoAgricultorId = cultivoAgricultorId; return this; }
        public Builder tipoAlerta(String tipoAlerta)                        { this.tipoAlerta = tipoAlerta; return this; }
        public Builder severidad(String severidad)                          { this.severidad = severidad; return this; }
        public Builder descripcion(String descripcion)                      { this.descripcion = descripcion; return this; }
        public Builder fechaDiaPronostico(LocalDate fechaDiaPronostico)     { this.fechaDiaPronostico = fechaDiaPronostico; return this; }
        public Builder fechaGeneracion(LocalDateTime fechaGeneracion)       { this.fechaGeneracion = fechaGeneracion; return this; }
        public Builder valorDetectado(Double valorDetectado)                { this.valorDetectado = valorDetectado; return this; }
        public Builder valorUmbral(Double valorUmbral)                      { this.valorUmbral = valorUmbral; return this; }
        public Builder recomendacion(String recomendacion)                  { this.recomendacion = recomendacion; return this; }
        public Builder leida(Integer leida)                                 { this.leida = leida; return this; }
        public Builder fechaLectura(LocalDateTime fechaLectura)             { this.fechaLectura = fechaLectura; return this; }

        public Alerta build() {
            return new Alerta(id, cultivoAgricultorId, tipoAlerta, severidad,
                    descripcion, fechaDiaPronostico, fechaGeneracion,
                    valorDetectado, valorUmbral, recomendacion, leida, fechaLectura);
        }
    }

    @Override
    public String toString() {
        return "Alerta{" +
            "id=" + id +
            ", cultivoAgricultorId=" + cultivoAgricultorId +
            ", tipoAlerta='" + tipoAlerta + '\'' +
            ", severidad='" + severidad + '\'' +
            ", fechaDiaPronostico=" + fechaDiaPronostico +
            ", valorDetectado=" + valorDetectado +
            ", valorUmbral=" + valorUmbral +
            ", leida=" + leida +
            ", fechaGeneracion=" + fechaGeneracion +
            '}';
    }
}