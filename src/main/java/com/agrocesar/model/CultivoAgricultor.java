package com.agrocesar.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class CultivoAgricultor {

    // NOT NULL (DDL garantiza)
    private Long id;
    private Long usuarioId;
    private Long catalogoId;
    private Long municipioId;
    private Double hectareas;
    private LocalDate fechaSiembra;
    private Integer activo;
    private LocalDateTime fechaCreacion;

    // Nullable — overrides de umbrales (NULL = heredar del catálogo)
    // La vista V_CULTIVOS_CON_UMBRALES resuelve el umbral efectivo con NVL.
    // La capa Java NUNCA debe copiar valores del catálogo en estos campos.
    private Double tempMinOverride;
    private Double tempMaxOverride;
    private Double lluviaMinOverride;
    private Double lluviaMaxOverride;
    private Double humedadMinOverride;
    private Double humedadMaxOverride;

    // Nullable - overrides de latitud y longitud (NULL = heredar de municipio)
    private Double latitudCultivo;
    private Double longitudCultivo;

    // Nullable — tipo de suelo de la parcela (informativo, para versiones futuras)
    private String tipoSuelo;

    // Nullable — Oracle actualiza mediante trigger TRG_CULTAGR_FECHA_ACT
    private LocalDateTime fechaActualizacion;

    // Constructor vacío (necesario para JDBI y Spring)
    public CultivoAgricultor() {}

    // Constructor completo (para mapeo JDBI desde ResultSet)
    public CultivoAgricultor(Long id, Long usuarioId, Long catalogoId, Long municipioId,
                             Double hectareas, LocalDate fechaSiembra, String tipoSuelo,
                             Double tempMinOverride, Double tempMaxOverride,
                             Double lluviaMinOverride, Double lluviaMaxOverride,
                             Double humedadMinOverride, Double humedadMaxOverride,
                             Double latitudCultivo, Double longitudCultivo, 
                             Integer activo, LocalDateTime fechaCreacion,
                             LocalDateTime fechaActualizacion) {
                                
        this.id                 = id;
        this.usuarioId          = usuarioId;
        this.catalogoId         = catalogoId;
        this.municipioId        = municipioId;
        this.hectareas          = hectareas;
        this.fechaSiembra       = fechaSiembra;
        this.tipoSuelo          = tipoSuelo;
        this.tempMinOverride    = tempMinOverride;
        this.tempMaxOverride    = tempMaxOverride;
        this.lluviaMinOverride  = lluviaMinOverride;
        this.lluviaMaxOverride  = lluviaMaxOverride;
        this.humedadMinOverride = humedadMinOverride;
        this.humedadMaxOverride = humedadMaxOverride;
        this.latitudCultivo     = latitudCultivo;
        this.longitudCultivo    = longitudCultivo;
        this.activo             = activo;
        this.fechaCreacion      = fechaCreacion;
        this.fechaActualizacion = fechaActualizacion;
    }

    // Getters
    public Long getId()                              { return id; }
    public Long getUsuarioId()                       { return usuarioId; }
    public Long getCatalogoId()                      { return catalogoId; }
    public Long getMunicipioId()                     { return municipioId; }
    public Double getHectareas()                     { return hectareas; }
    public LocalDate getFechaSiembra()               { return fechaSiembra; }
    public String getTipoSuelo()                     { return tipoSuelo; }
    public Double getTempMinOverride()               { return tempMinOverride; }
    public Double getTempMaxOverride()               { return tempMaxOverride; }
    public Double getLluviaMinOverride()             { return lluviaMinOverride; }
    public Double getLluviaMaxOverride()             { return lluviaMaxOverride; }
    public Double getHumedadMinOverride()            { return humedadMinOverride; }
    public Double getHumedadMaxOverride()            { return humedadMaxOverride; }
    public Double getLatitudCultivo()                { return latitudCultivo;}
    public Double getLongitudCultivo()               { return longitudCultivo;}
    public Integer getActivo()                       { return activo; }
    public LocalDateTime getFechaCreacion()          { return fechaCreacion; }
    public LocalDateTime getFechaActualizacion()     { return fechaActualizacion; }

    public boolean isActivo()                        { return Integer.valueOf(1).equals(this.activo); }

    // Setters
    public void setId(Long id)                                      { this.id = id; }
    public void setUsuarioId(Long usuarioId)                        { this.usuarioId = usuarioId; }
    public void setCatalogoId(Long catalogoId)                      { this.catalogoId = catalogoId; }
    public void setMunicipioId(Long municipioId)                    { this.municipioId = municipioId; }
    public void setHectareas(Double hectareas)                      { this.hectareas = hectareas; }
    public void setFechaSiembra(LocalDate fechaSiembra)             { this.fechaSiembra = fechaSiembra; }
    public void setTipoSuelo(String tipoSuelo)                      { this.tipoSuelo = tipoSuelo; }
    public void setTempMinOverride(Double tempMinOverride)          { this.tempMinOverride = tempMinOverride; }
    public void setTempMaxOverride(Double tempMaxOverride)          { this.tempMaxOverride = tempMaxOverride; }
    public void setLluviaMinOverride(Double lluviaMinOverride)      { this.lluviaMinOverride = lluviaMinOverride; }
    public void setLluviaMaxOverride(Double lluviaMaxOverride)      { this.lluviaMaxOverride = lluviaMaxOverride; }
    public void setHumedadMinOverride(Double humedadMinOverride)    { this.humedadMinOverride = humedadMinOverride; }
    public void setHumedadMaxOverride(Double humedadMaxOverride)    { this.humedadMaxOverride = humedadMaxOverride; }
    public void setLatitudCultivo(Double latitudCultivo)            { this.latitudCultivo = latitudCultivo; }
    public void setLongitudCultivo(Double longitudCultivo)          { this.longitudCultivo = longitudCultivo; }
    public void setActivo(Integer activo)                           { this.activo = activo; }
    public void setFechaCreacion(LocalDateTime fechaCreacion)       { this.fechaCreacion = fechaCreacion; }
    public void setFechaActualizacion(LocalDateTime f)              { this.fechaActualizacion = f; }

    // Builder estático (reemplaza @Builder de Lombok)
    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private Long usuarioId;
        private Long catalogoId;
        private Long municipioId;
        private Double hectareas;
        private LocalDate fechaSiembra;
        private String tipoSuelo;
        private Double tempMinOverride;
        private Double tempMaxOverride;
        private Double lluviaMinOverride;
        private Double lluviaMaxOverride;
        private Double humedadMinOverride;
        private Double humedadMaxOverride;
        private Double latitudCultivo;
        private Double longitudCultivo;
        private Integer activo;
        private LocalDateTime fechaCreacion;
        private LocalDateTime fechaActualizacion;

        public Builder id(Long id)                                  { this.id = id; return this; }
        public Builder usuarioId(Long usuarioId)                    { this.usuarioId = usuarioId; return this; }
        public Builder catalogoId(Long catalogoId)                  { this.catalogoId = catalogoId; return this; }
        public Builder municipioId(Long municipioId)                { this.municipioId = municipioId; return this; }
        public Builder hectareas(Double hectareas)                  { this.hectareas = hectareas; return this; }
        public Builder fechaSiembra(LocalDate fechaSiembra)         { this.fechaSiembra = fechaSiembra; return this; }
        public Builder tipoSuelo(String tipoSuelo)                  { this.tipoSuelo = tipoSuelo; return this; }
        public Builder tempMinOverride(Double v)                    { this.tempMinOverride = v; return this; }
        public Builder tempMaxOverride(Double v)                    { this.tempMaxOverride = v; return this; }
        public Builder lluviaMinOverride(Double v)                  { this.lluviaMinOverride = v; return this; }
        public Builder lluviaMaxOverride(Double v)                  { this.lluviaMaxOverride = v; return this; }
        public Builder humedadMinOverride(Double v)                 { this.humedadMinOverride = v; return this; }
        public Builder humedadMaxOverride(Double v)                 { this.humedadMaxOverride = v; return this; }
        public Builder latitudCultivo(Double latitud)               { this.latitudCultivo = latitud; return this; }
        public Builder longitudCultivo(Double longitud)             { this.longitudCultivo = longitud; return this; }
        public Builder activo(Integer activo)                       { this.activo = activo; return this; }
        public Builder fechaCreacion(LocalDateTime f)               { this.fechaCreacion = f; return this; }
        public Builder fechaActualizacion(LocalDateTime f)          { this.fechaActualizacion = f; return this; }

        public CultivoAgricultor build() {
            return new CultivoAgricultor(id, usuarioId, catalogoId, municipioId,
                    hectareas, fechaSiembra, tipoSuelo,
                    tempMinOverride, tempMaxOverride,
                    lluviaMinOverride, lluviaMaxOverride,
                    humedadMinOverride, humedadMaxOverride,
                    latitudCultivo, longitudCultivo,
                    activo, fechaCreacion, fechaActualizacion);
        }
    }

    @Override
    public String toString() {
        return "CultivoAgricultor{" +
            "id=" + id +
            ", usuarioId=" + usuarioId +
            ", catalogoId=" + catalogoId +
            ", municipioId=" + municipioId +
            ", hectareas=" + hectareas +
            ", fechaSiembra=" + fechaSiembra +
            ", overrides={" +
                "tempMin=" + tempMinOverride +
                ", tempMax=" + tempMaxOverride +
                ", lluviaMin=" + lluviaMinOverride +
                ", lluviaMax=" + lluviaMaxOverride +
                ", humedadMin=" + humedadMinOverride +
                ", humedadMax=" + humedadMaxOverride +
            "}" +
            ", latitudCultivo=" + latitudCultivo +
            ", longitudCultivo=" + longitudCultivo +
            ", activo=" + activo +
            '}';
    }
}