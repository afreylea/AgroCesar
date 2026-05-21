package com.agrocesar.dto;

public class RankingCultivoDTO {

    private String nombre;
    private String municipio;
    private Double totalHectareas;

    public RankingCultivoDTO(String nombre, String municipio, Double totalHectareas) {
        this.nombre         = nombre;
        this.municipio      = municipio;
        this.totalHectareas = totalHectareas;
    }

    public String getNombre()         { return nombre; }
    public String getMunicipio()      { return municipio; }
    public Double getTotalHectareas() { return totalHectareas; }
}