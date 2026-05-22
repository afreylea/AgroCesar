package com.agrocesar.dto;

import java.time.LocalDate;

public class CultivoResumen {
    private Long id;
    private String nombreCultivo;
    private String categoria;
    private String municipio;
    private Double hectareas;
    private LocalDate fechaSiembra;
    private Long municipioId;
    private Double latitud;
    private Double longitud;

    public CultivoResumen(Long id, String nombreCultivo, String categoria,
            String municipio, Double hectareas, LocalDate fechaSiembra, Long municipioId, Double latitud,
            Double longitud) {
        this.id = id;
        this.nombreCultivo = nombreCultivo;
        this.categoria = categoria;
        this.municipio = municipio;
        this.hectareas = hectareas;
        this.fechaSiembra = fechaSiembra;
        this.municipioId = municipioId;
        this.latitud = latitud;
        this.longitud = longitud;
    }

    public Long getId() {
        return id;
    }

    public String getNombreCultivo() {
        return nombreCultivo;
    }

    public String getCategoria() {
        return categoria;
    }

    public String getMunicipio() {
        return municipio;
    }

    public Double getHectareas() {
        return hectareas;
    }

    public LocalDate getFechaSiembra() {
        return fechaSiembra;
    }

    public Long getMunicipioId() {
        return municipioId;
    }

    public Double getLatitud() {
        return latitud;
    }

    public Double getLongitud() {
        return longitud;
    }
}