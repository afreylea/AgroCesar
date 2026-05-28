package com.agrocesar.service;

/**
 * Clase abstracta que define el contrato y comportamiento base
 * para calcular la severidad de una alerta climática según la
 * categoría del cultivo (TRANSITORIO / PERMANENTE).
 *
 * Las constantes de umbral están basadas en las tres etapas
 * fenológicas compartidas por ambos tipos de cultivo:
 * establecimiento, crecimiento vegetativo y reproducción/maduración.
 */
public abstract class SeveridadStrategy {

    /** Límite de la etapa de establecimiento (0% - 20% del ciclo) */
    protected static final double UMBRAL_ESTABLECIMIENTO = 0.20;

    /** Límite de la etapa de crecimiento vegetativo (20% - 60% del ciclo) */
    protected static final double UMBRAL_VEGETATIVO = 0.60;

    /**
     * Calcula la severidad de una alerta según la etapa fenológica
     * del cultivo. Cada subclase implementa la lógica diferenciada
     * por categoría.
     *
     * @param diasRestantes   días restantes hasta el fin del ciclo
     * @param diasCosechaProm promedio del ciclo total del cultivo
     * @return "ALTA", "MEDIA" o "BAJA"
     */
    public abstract String calcular(int diasRestantes, int diasCosechaProm);

    /**
     * Calcula el porcentaje del ciclo transcurrido desde la siembra.
     * Método concreto compartido por ambas subclases.
     *
     * @param diasTranscurridos días desde la siembra
     * @param diasCosechaProm   promedio del ciclo total del cultivo
     * @return valor entre 0.0 y 1.0
     */
    protected double calcularPorcentaje(int diasTranscurridos, int diasCosechaProm) {
        if (diasCosechaProm <= 0)
            throw new IllegalArgumentException("El ciclo del cultivo debe ser mayor a cero.");
        return (double) diasTranscurridos / diasCosechaProm;
    }
}