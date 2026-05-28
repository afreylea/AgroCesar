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
    public String calcular(int diasRestantes, int diasCosechaProm) {
        int diasTranscurridos = diasCosechaProm - diasRestantes;
        double pct = calcularPorcentaje(diasTranscurridos, diasCosechaProm);

        if      (pct <= UMBRAL_ESTABLECIMIENTO) return "ALTA";
        else if (pct <= UMBRAL_VEGETATIVO)      return "MEDIA";
        else                                    return "BAJA";
    }
}