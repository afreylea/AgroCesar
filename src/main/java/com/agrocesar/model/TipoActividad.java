package com.agrocesar.model;

import java.time.LocalDate;

/**
 * Modelo que representa un tipo de actividad agricola
 * registrable en la bitacora del cultivo.
 *
 * Mapeado a la tabla TIPOS_ACTIVIDAD.
 * Ejemplos: Riego, Fertilizacion, Fumigacion, Cosecha, Poda, etc.
 *
 * Cada tipo tiene un icono Phosphor asociado para la capa de presentacion
 * y un estado activo/inactivo que permite deshabilitarlo sin eliminarlo.
 */
public class TipoActividad {

    private Long id;
    private String nombre;
    private String icono;
    private Integer activo;
    private LocalDate fechaCreacion;

    private TipoActividad() {}

    /**
     * Constructor completo.
     *
     * @param id             identificador unico de la secuencia SEQ_TIPOS_ACTIVIDAD
     * @param nombre         nombre visible de la actividad (ej: Riego, Poda)
     * @param icono          clase del icono Phosphor para el frontend (ej: ph-drop)
     * @param activo         1 si esta habilitado, 0 si esta desactivado
     * @param fechaCreacion  fecha en que se registro el tipo en la base de datos
     */
    public TipoActividad(Long id, String nombre, String icono, Integer activo, LocalDate fechaCreacion) {
        this.id = id;
        this.nombre = nombre;
        this.icono = icono;
        this.activo = activo;
        this.fechaCreacion = fechaCreacion;
    }

    public static Builder builder() { return new Builder(); }

    /**
     * Builder para construir instancias de TipoActividad paso a paso.
     * Usado por el mapper del repository al transformar filas del ResultSet.
     */
    public static class Builder {
        private final TipoActividad o = new TipoActividad();

        public Builder id(Long v)                { o.id = v;            return this; }
        public Builder nombre(String v)          { o.nombre = v;        return this; }
        public Builder icono(String v)           { o.icono = v;         return this; }
        public Builder activo(Integer v)         { o.activo = v;        return this; }
        public Builder fechaCreacion(LocalDate v){ o.fechaCreacion = v; return this; }

        public TipoActividad build()             { return o; }
    }

    public Long getId()                { return id; }
    public String getNombre()          { return nombre; }
    public String getIcono()           { return icono; }
    public Integer getActivo()         { return activo; }
    public LocalDate getFechaCreacion(){ return fechaCreacion; }
}