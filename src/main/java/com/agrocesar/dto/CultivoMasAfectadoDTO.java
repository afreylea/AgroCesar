package com.agrocesar.dto;

public class CultivoMasAfectadoDTO {

    private String nombreCultivo;
    private String municipio;
    private int totalAlertas;

    public CultivoMasAfectadoDTO(String nombreCultivo, String municipio, int totalAlertas) {
        this.nombreCultivo = nombreCultivo;
        this.municipio     = municipio;
        this.totalAlertas  = totalAlertas;
    }

    public String getNombreCultivo() { return nombreCultivo; }
    public String getMunicipio()     { return municipio; }
    public int getTotalAlertas()     { return totalAlertas; }
}