package com.agrocesar.dto;

public class RankingCultivoDTO {

    private String nombre;
    private Integer totalAgricultores;
    private Double totalHectareas;
    private String municipio;

    public RankingCultivoDTO(String nombre, Integer totalAgricultores,
            Double totalHectareas, String municipio) {
        this.nombre = nombre;
        this.totalAgricultores = totalAgricultores;
        this.totalHectareas = totalHectareas;
        this.municipio = municipio;
    }

    public String getNombre() {
        return nombre;
    }

    public Integer getTotalAgricultores() {
        return totalAgricultores;
    }

    public Double getTotalHectareas() {
        return totalHectareas;
    }

    public String getMunicipio() {
        return municipio;
    }
}