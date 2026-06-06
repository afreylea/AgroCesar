package com.agrocesar.model;

/**
 * Modelo que representa un tipo de actividad agricola
 * registrable en la bitacora del cultivo.
 *
 * Mapeado a la tabla TIPOS_ACTIVIDAD.
 * Ejemplos: Riego, Fertilizacion, Fumigacion, Cosecha, etc.
 */
public class TipoActividad {

    private Long id;
    private String nombre;
    private String icono;
    private Integer activo;

    public TipoActividad() {}

    /**
     * Constructor completo.
     *
     * @param id     identificador unico de la secuencia SEQ_TIPOS_ACTIVIDAD
     * @param nombre nombre visible de la actividad (ej: Riego, Poda)
     * @param icono  clase del icono Phosphor para el frontend (ej: ph-drop)
     * @param activo 1 si esta habilitado, 0 si esta desactivado
     */
    public TipoActividad(Long id, String nombre, String icono, Integer activo) {
        this.id     = id;
        this.nombre = nombre;
        this.icono  = icono;
        this.activo = activo;
    }

    public Long    getId()     { return id; }
    public String  getNombre() { return nombre; }
    public String  getIcono()  { return icono; }
    public Integer getActivo() { return activo; }

    public void setId(Long id)          { this.id = id; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setIcono(String icono)   { this.icono = icono; }
    public void setActivo(Integer activo) { this.activo = activo; }
}