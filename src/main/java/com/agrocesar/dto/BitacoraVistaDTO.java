package com.agrocesar.dto;

import java.time.LocalDate;

/**
 * DTO que transporta los datos de una entrada de bitacora
 * con la informacion relacionada del tipo de actividad,
 * cultivo y municipio ya resuelta por el JOIN del paquete PL/SQL.
 *
 * Mapeado desde el cursor de PKG_BITACORA.prc_find_by_usuario
 * y PKG_BITACORA.prc_find_by_cultivo.
 */
public class BitacoraVistaDTO {

    private Long id;
    private Long cultivoAgricultorId;
    private Long tipoActividadId;
    private String tipoActividad;
    private String tipoIcono;
    private Long alertaId;
    private String descripcion;
    private LocalDate fechaActividad;
    private LocalDate fechaCreacion;
    private String nombreCultivo;
    private String municipio;

    private BitacoraVistaDTO() {
    }

    /**
     * Constructor completo — usado internamente por el Builder.update constructor and builder to include fechaCreacion
     *
     * @param id                  identificador unico de la entrada
     * @param cultivoAgricultorId FK al cultivo del agricultor
     * @param tipoActividadId     FK al tipo de actividad
     * @param tipoActividad       nombre del tipo (ej: Riego, Poda)
     * @param tipoIcono           clase Phosphor del icono (ej: ph-drop)
     * @param alertaId            FK opcional a la alerta que motivo la accion
     * @param descripcion         nota libre del agricultor
     * @param fechaActividad      cuando realizo la actividad
     * @param fechaCreacion       cuando registro la entrada en el sistema
     * @param nombreCultivo       nombre del cultivo del catalogo
     * @param municipio           nombre del municipio
     */
    public BitacoraVistaDTO(Long id, Long cultivoAgricultorId, Long tipoActividadId,
            String tipoActividad, String tipoIcono, Long alertaId,
            String descripcion, LocalDate fechaActividad,
            LocalDate fechaCreacion, String nombreCultivo,
            String municipio) {
        this.id = id;
        this.cultivoAgricultorId = cultivoAgricultorId;
        this.tipoActividadId = tipoActividadId;
        this.tipoActividad = tipoActividad;
        this.tipoIcono = tipoIcono;
        this.alertaId = alertaId;
        this.descripcion = descripcion;
        this.fechaActividad = fechaActividad;
        this.fechaCreacion = fechaCreacion;
        this.nombreCultivo = nombreCultivo;
        this.municipio = municipio;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final BitacoraVistaDTO o = new BitacoraVistaDTO();

        public Builder id(Long v) {
            o.id = v;
            return this;
        }

        public Builder cultivoAgricultorId(Long v) {
            o.cultivoAgricultorId = v;
            return this;
        }

        public Builder tipoActividadId(Long v) {
            o.tipoActividadId = v;
            return this;
        }

        public Builder tipoActividad(String v) {
            o.tipoActividad = v;
            return this;
        }

        public Builder tipoIcono(String v) {
            o.tipoIcono = v;
            return this;
        }

        public Builder alertaId(Long v) {
            o.alertaId = v;
            return this;
        }

        public Builder descripcion(String v) {
            o.descripcion = v;
            return this;
        }

        public Builder fechaActividad(LocalDate v) {
            o.fechaActividad = v;
            return this;
        }

        public Builder fechaCreacion(LocalDate v) {
            o.fechaCreacion = v;
            return this;
        }

        public Builder nombreCultivo(String v) {
            o.nombreCultivo = v;
            return this;
        }

        public Builder municipio(String v) {
            o.municipio = v;
            return this;
        }

        public BitacoraVistaDTO build() {
            return o;
        }
    }

    public Long getId() {
        return id;
    }

    public Long getCultivoAgricultorId() {
        return cultivoAgricultorId;
    }

    public Long getTipoActividadId() {
        return tipoActividadId;
    }

    public String getTipoActividad() {
        return tipoActividad;
    }

    public String getTipoIcono() {
        return tipoIcono;
    }

    public Long getAlertaId() {
        return alertaId;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public LocalDate getFechaActividad() {
        return fechaActividad;
    }

    public LocalDate getFechaCreacion() {
        return fechaCreacion;
    }

    public String getNombreCultivo() {
        return nombreCultivo;
    }

    public String getMunicipio() {
        return municipio;
    }
}