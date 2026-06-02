package com.agrocesar.dto;

import java.time.LocalDate;

/**
 * DTO de proyeccion usado para mostrar un cultivo del agricultor en las vistas.
 * No mapea directamente una tabla — combina datos de CULTIVOS_AGRICULTOR,
 * CULTIVOS_CATALOGO y MUNICIPIOS para reducir el numero de consultas en el
 * controller.
 *
 * <p>
 * Usado en:
 * <ul>
 * <li>DashboardController — cards de cultivos y selector de pronostico</li>
 * <li>CultivoController — listado de cultivos del agricultor</li>
 * </ul>
 */
public class CultivoResumen {

    /** ID del registro en CULTIVOS_AGRICULTOR. */
    private Long id;

    /** Nombre del cultivo segun CULTIVOS_CATALOGO. */
    private String nombreCultivo;

    /** Categoria del cultivo: TRANSITORIO o PERMANENTE. */
    private String categoria;

    /** Nombre del municipio donde esta ubicado el cultivo. */
    private String municipio;

    /** Superficie cultivada en hectareas. */
    private Double hectareas;

    /** Fecha en que se realizo la siembra. */
    private LocalDate fechaSiembra;

    /** ID del municipio — usado para llamar al endpoint de pronostico climatico. */
    private Long municipioId;

    /**
     * Latitud del cultivo o del municipio como fallback.
     * Usada para centrar el mapa Leaflet en el dashboard.
     */
    private Double latitud;

    /**
     * Longitud del cultivo o del municipio como fallback.
     * Usada para centrar el mapa Leaflet en el dashboard.
     */
    private Double longitud;

    /**
     * Nombre del archivo de imagen almacenado en disco externo.
     * Puede ser null si el admin no ha subido imagen para este cultivo.
     * Se sirve via /imagenes/{imagenUrl} configurado en WebConfig.
     */
    private String imagenUrl;

    /**
     * Constructor completo usado en DashboardController y CultivoController
     * al construir la proyeccion desde CultivoAgricultor + CultivoCatalogo +
     * Municipio.
     *
     * @param id            ID del cultivo del agricultor
     * @param nombreCultivo nombre segun el catalogo
     * @param categoria     TRANSITORIO o PERMANENTE
     * @param municipio     nombre del municipio
     * @param hectareas     superficie en hectareas
     * @param fechaSiembra  fecha de siembra
     * @param municipioId   ID del municipio para el endpoint de pronostico
     * @param latitud       latitud para el mapa
     * @param longitud      longitud para el mapa
     * @param imagenUrl     nombre del archivo de imagen, puede ser null
     */
    public CultivoResumen(Long id, String nombreCultivo, String categoria,
            String municipio, Double hectareas, LocalDate fechaSiembra,
            Long municipioId, Double latitud, Double longitud, String imagenUrl) {
        this.id = id;
        this.nombreCultivo = nombreCultivo;
        this.categoria = categoria;
        this.municipio = municipio;
        this.hectareas = hectareas;
        this.fechaSiembra = fechaSiembra;
        this.municipioId = municipioId;
        this.latitud = latitud;
        this.longitud = longitud;
        this.imagenUrl = imagenUrl;
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

    public String getImagenUrl() {
        return imagenUrl;
    }
}