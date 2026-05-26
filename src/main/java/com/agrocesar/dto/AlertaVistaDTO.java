package com.agrocesar.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class AlertaVistaDTO {

    private Long      alertaId;
    private String    agricultor;
    private String    cultivo;
    private String    categoria;
    private String    municipio;
    private String    tipoAlerta;
    private String    severidad;
    private String    descripcion;
    private String    recomendacion;
    private Double    valorDetectado;
    private Double    valorUmbral;
    private LocalDate fechaDiaPronostico;
    private LocalDateTime fechaGeneracion;
    private Integer   leida;
    private Long      usuarioId;
    private Integer   diasRestantesCosecha;

    public AlertaVistaDTO(Long alertaId, String agricultor,
                          String cultivo, String categoria, String municipio,
                          String tipoAlerta, String severidad, String descripcion,
                          String recomendacion, Double valorDetectado, Double valorUmbral,
                          LocalDate fechaDiaPronostico, LocalDateTime fechaGeneracion,
                          Integer leida, Long usuarioId, Integer diasRestantesCosecha) {

        this.alertaId             = alertaId;
        this.agricultor           = agricultor;
        this.cultivo              = cultivo;
        this.categoria            = categoria;
        this.municipio            = municipio;
        this.tipoAlerta           = tipoAlerta;
        this.severidad            = severidad;
        this.descripcion          = descripcion;
        this.recomendacion        = recomendacion;
        this.valorDetectado       = valorDetectado;
        this.valorUmbral          = valorUmbral;
        this.fechaDiaPronostico   = fechaDiaPronostico;
        this.fechaGeneracion      = fechaGeneracion;
        this.leida                = leida;
        this.usuarioId            = usuarioId;
        this.diasRestantesCosecha = diasRestantesCosecha;
    }

    public boolean isLeida() { return Integer.valueOf(1).equals(this.leida); }

    public Long      getAlertaId()             { return alertaId; }
    public String    getAgricultor()           { return agricultor; }
    public String    getCultivo()              { return cultivo; }
    public String    getCategoria()            { return categoria; }
    public String    getMunicipio()            { return municipio; }
    public String    getTipoAlerta()           { return tipoAlerta; }
    public String    getSeveridad()            { return severidad; }
    public String    getDescripcion()          { return descripcion; }
    public String    getRecomendacion()        { return recomendacion; }
    public Double    getValorDetectado()       { return valorDetectado; }
    public Double    getValorUmbral()          { return valorUmbral; }
    public LocalDate getFechaDiaPronostico()   { return fechaDiaPronostico; }
    public LocalDateTime getFechaGeneracion()  { return fechaGeneracion; }
    public Integer   getLeida()                { return leida; }
    public Long      getUsuarioId()            { return usuarioId; }
    public Integer   getDiasRestantesCosecha() { return diasRestantesCosecha; }
}