package com.agrocesar.dto;

public class RankingCultivoDTO {

    private String nombre;
    private Integer totalAgricultores;
    private Double totalHectareas;

    public RankingCultivoDTO(String nombre, Integer totalAgricultores, Double totalHectareas) {
        this.nombre = nombre;
        this.totalAgricultores = totalAgricultores;
        this.totalHectareas = totalHectareas;
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
}