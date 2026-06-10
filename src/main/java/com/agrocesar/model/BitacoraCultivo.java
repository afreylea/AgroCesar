package com.agrocesar.model;

import java.time.LocalDate;

public class BitacoraCultivo {

    private Long id;
    private Long cultivoAgricultorId;
    private Long tipoActividadId;
    private String cultivo; // JOIN desde Catalogo
    private String tipoNombre; // JOIN desde TIPOS_ACTIVIDAD
    private String tipoIcono; // JOIN desde TIPOS_ACTIVIDAD
    private Long alertaId; // nullable — FK opcional
    private String descripcion; // nullable
    private LocalDate fechaActividad;
    private LocalDate fechaCreacion;
    private String responsable;
    private String ubicacion;
    private String estado;

    private BitacoraCultivo() {
    }

    public BitacoraCultivo(Long id, Long cultivoAgricultorId, Long tipoActividadId,
            String tipoNombre, String tipoIcono, Long alertaId,
            String descripcion, LocalDate fechaActividad,
            LocalDate fechaCreacion, String cultivo, String responsable, String ubicacion, String estado) {
        this.id = id;
        this.cultivoAgricultorId = cultivoAgricultorId;
        this.tipoActividadId = tipoActividadId;
        this.cultivo = cultivo;
        this.tipoNombre = tipoNombre;
        this.tipoIcono = tipoIcono;
        this.alertaId = alertaId;
        this.descripcion = descripcion;
        this.fechaActividad = fechaActividad;
        this.fechaCreacion = fechaCreacion;
        this.responsable = responsable;
        this.ubicacion = ubicacion;
        this.estado = estado;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final BitacoraCultivo o = new BitacoraCultivo();

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

        public Builder tipoNombre(String v) {
            o.tipoNombre = v;
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

        public Builder cultivo(String v) {
            o.cultivo = v;
            return this;
        }

        public Builder responsable(String v) {
            o.responsable = v;
            return this;
        }

        public Builder ubicacion(String v) {
            o.ubicacion = v;
            return this;
        }
        public Builder estado(String v) {
            o.estado = v;
            return this;
        }
        public BitacoraCultivo build() {
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

    public String getTipoNombre() {
        return tipoNombre;
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

    public String getCultivo() {
        return cultivo;
    }

    public String getResponsable() {
        return responsable;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public String getEstado() {
        return estado;
    }
}
