package com.agrocesar.dto;

import java.time.LocalDate;

public class CultivoConUmbralesDTO {

    private Long    id;
    private Long    usuarioId;
    private String  agricultor;
    private String  telefono;
    private String  cultivo;
    private String  categoria;
    private Long    municipioId;
    private String  municipio;
    private Double  latitud;
    private Double  longitud;
    private Double  hectareas;
    private LocalDate fechaSiembra;
    private Double  tempMinEfectiva;
    private Double  tempMaxEfectiva;
    private Double  lluviaMinEfectiva;
    private Double  lluviaMaxEfectiva;
    private Double  humedadMinEfectiva;
    private Double  humedadMaxEfectiva;
    private Integer diasCosechaProm;
    private Integer diasRestantes;

    public CultivoConUmbralesDTO(
            Long    id,
            Long    usuarioId,
            String  agricultor,
            String  telefono,
            String  cultivo,
            String  categoria,
            Long    municipioId,
            String  municipio,
            Double  latitud,
            Double  longitud,
            Double  hectareas,
            LocalDate fechaSiembra,
            Double  tempMinEfectiva,
            Double  tempMaxEfectiva,
            Double  lluviaMinEfectiva,
            Double  lluviaMaxEfectiva,
            Double  humedadMinEfectiva,
            Double  humedadMaxEfectiva,
            Integer diasCosechaProm,
            Integer diasRestantes) {

        this.id                 = id;
        this.usuarioId          = usuarioId;
        this.agricultor         = agricultor;
        this.telefono           = telefono;
        this.cultivo            = cultivo;
        this.categoria          = categoria;
        this.municipioId        = municipioId;
        this.municipio          = municipio;
        this.latitud            = latitud;
        this.longitud           = longitud;
        this.hectareas          = hectareas;
        this.fechaSiembra       = fechaSiembra;
        this.tempMinEfectiva    = tempMinEfectiva;
        this.tempMaxEfectiva    = tempMaxEfectiva;
        this.lluviaMinEfectiva  = lluviaMinEfectiva;
        this.lluviaMaxEfectiva  = lluviaMaxEfectiva;
        this.humedadMinEfectiva = humedadMinEfectiva;
        this.humedadMaxEfectiva = humedadMaxEfectiva;
        this.diasCosechaProm    = diasCosechaProm;
        this.diasRestantes      = diasRestantes;
    }

    public int getDiasTranscurridos() {
        return diasCosechaProm - diasRestantes;
    }

    public Long      getId()                 { return id; }
    public Long      getUsuarioId()          { return usuarioId; }
    public String    getAgricultor()         { return agricultor; }
    public String    getTelefono()           { return telefono; }
    public String    getCultivo()            { return cultivo; }
    public String    getCategoria()          { return categoria; }
    public Long      getMunicipioId()        { return municipioId; }
    public String    getMunicipio()          { return municipio; }
    public Double    getLatitud()            { return latitud; }
    public Double    getLongitud()           { return longitud; }
    public Double    getHectareas()          { return hectareas; }
    public LocalDate getFechaSiembra()       { return fechaSiembra; }
    public Double    getTempMinEfectiva()    { return tempMinEfectiva; }
    public Double    getTempMaxEfectiva()    { return tempMaxEfectiva; }
    public Double    getLluviaMinEfectiva()  { return lluviaMinEfectiva; }
    public Double    getLluviaMaxEfectiva()  { return lluviaMaxEfectiva; }
    public Double    getHumedadMinEfectiva() { return humedadMinEfectiva; }
    public Double    getHumedadMaxEfectiva() { return humedadMaxEfectiva; }
    public Integer   getDiasCosechaProm()    { return diasCosechaProm; }
    public Integer   getDiasRestantes()      { return diasRestantes; }
}