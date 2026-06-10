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

    // Pesos hídricos por etapa — basados en coeficientes Kc FAO
    protected static final double PESO_ESTABLECIMIENTO = 0.20;
    protected static final double PESO_VEGETATIVO      = 0.30;
    protected static final double PESO_REPRODUCCION    = 0.50;

    /**
     * Umbral de precipitación diaria a partir del cual se considera
     * un evento extremo de lluvia excesiva, independientemente del
     * requerimiento hídrico del cultivo.
     */
    public static final double LLUVIA_EXTREMA_MM = 50.0;

    /**
     * Calcula la severidad de una alerta según la etapa fenológica
     * del cultivo. Cada subclase implementa la lógica diferenciada
     * por categoría.
     *
     * @param diasRestantes   días restantes hasta el fin del ciclo
     * @param diasCosechaProm promedio del ciclo total del cultivo
     * @return "ALTA", "MEDIA" o "BAJA"
     */
    public abstract String calcularSeveridad(int diasRestantes, int diasCosechaProm);

    /**
     * Calcula la severidad para eventos de lluvia extrema (> LLUVIA_EXTREMA_MM por día).
     * La severidad difiere de {@link #calcularSeveridad} porque el daño mecánico e
     * hídrico de un evento extremo tiene impacto distinto según la etapa fenológica
     * y la categoría del cultivo.
     *
     * @param diasRestantes   días restantes hasta el fin del ciclo
     * @param diasCosechaProm promedio del ciclo total del cultivo
     * @return "ALTA" o "MEDIA"
     */
    public abstract String calcularSeveridadLluviaExtrema(int diasRestantes, int diasCosechaProm);

    /**
     * Normaliza el umbral de lluvia al periodo de acumulación dado,
     * ponderando según la etapa fenológica actual del cultivo.
     *
     * @param lluviaUmbral   requerimiento total por ciclo (TRANSITORIO) o anual (PERMANENTE)
     * @param diasCiclo      días del ciclo productivo (diasCosechaProm)
     * @param diasAcumulados días del periodo de acumulación (ej. 15)
     * @param diasRestantes  días restantes para calcular la etapa actual
     */
    public abstract double normalizarLluvia(double lluviaUmbral, int diasCiclo,
                                            int diasAcumulados, int diasRestantes);

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

    protected double pesoPorEtapa(double pct) {
        if (pct <= UMBRAL_ESTABLECIMIENTO) return PESO_ESTABLECIMIENTO;
        if (pct <= UMBRAL_VEGETATIVO)      return PESO_VEGETATIVO;
        return PESO_REPRODUCCION;
    }
}