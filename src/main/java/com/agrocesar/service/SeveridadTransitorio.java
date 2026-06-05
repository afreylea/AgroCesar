package com.agrocesar.service;

import org.springframework.stereotype.Component;

/**
 * Implementación de SeveridadStrategy para cultivos TRANSITORIOS.
 *
 * Lógica fenológica:
 * - ALTA  : pctTranscurrido ≤ 20% — etapa de establecimiento.
 *           La planta depende de reservas limitadas y es más frágil.
 *           El daño puede ser letal con baja capacidad de recuperación.
 *
 * - MEDIA : 20% < pctTranscurrido ≤ 60% — crecimiento vegetativo.
 *           La planta está desarrollando su estructura. Vulnerable
 *           pero con mayor capacidad de recuperación que en establecimiento.
 *
 * - BAJA  : pctTranscurrido > 60% — reproducción y maduración.
 *           La planta está establecida y tiene mayor resiliencia.
 *           El impacto afecta rendimiento pero raramente es letal.
 */
@Component("TRANSITORIO")
public class SeveridadTransitorio extends SeveridadStrategy {

    @Override
    public String calcularSeveridad(int diasRestantes, int diasCosechaProm) {
        int diasTranscurridos = diasCosechaProm - diasRestantes;
        double pct = calcularPorcentaje(diasTranscurridos, diasCosechaProm);

        if      (pct <= UMBRAL_ESTABLECIMIENTO) return "ALTA";
        else if (pct <= UMBRAL_VEGETATIVO)      return "MEDIA";
        else                                    return "BAJA";
    }

    @Override
    public String calcularSeveridadLluviaExtrema(int diasRestantes, int diasCosechaProm) {
        int diasTranscurridos = diasCosechaProm - diasRestantes;
        double pct = calcularPorcentaje(diasTranscurridos, diasCosechaProm);

        // Establecimiento y vegetativo: ALTA — plántula frágil o en desarrollo,
        // encharcamiento puede ser letal o causar daño mecánico severo.
        // Reproducción: MEDIA — planta madura, impacto solo en rendimiento.
        return (pct <= UMBRAL_VEGETATIVO) ? "ALTA" : "MEDIA";
    }               

    /**
     * Para TRANSITORIO el umbral es por ciclo completo.
     * Se pondera según la etapa actual: la reproducción concentra
     * el 50% del requerimiento hídrico aunque represente el 40% del tiempo.
     */
    @Override
    public double normalizarLluvia(double lluviaUmbral, int diasCiclo,
                                   int diasAcumulados, int diasRestantes) {
        int diasTranscurridos = diasCiclo - diasRestantes;
        double pct = calcularPorcentaje(diasTranscurridos, diasCiclo);
        double peso = pesoPorEtapa(pct);

        // Lluvia asignada a esta etapa, distribuida en sus días proporcionales
        double diasEtapa = diasCiclo * (pct <= UMBRAL_ESTABLECIMIENTO ? UMBRAL_ESTABLECIMIENTO
                         : pct <= UMBRAL_VEGETATIVO ? (UMBRAL_VEGETATIVO - UMBRAL_ESTABLECIMIENTO)
                         : (1.0 - UMBRAL_VEGETATIVO));

        double lluviaDiariaEtapa = (lluviaUmbral * peso) / diasEtapa;
        return lluviaDiariaEtapa * diasAcumulados;
    }
}