package com.agrocesar.service;

import org.springframework.stereotype.Component;

/**
 * Implementación de SeveridadStrategy para cultivos PERMANENTES.
 *
 * Lógica fenológica:
 * - ALTA  : pctCicloAnual ≤ 60% — establecimiento + crecimiento vegetativo
 *           dentro del ciclo productivo anual. El daño en esta etapa afecta
 *           la floración y el cuajado de frutos del año en curso, comprometiendo
 *           el rendimiento acumulado de toda la temporada.
 *
 * - MEDIA : pctCicloAnual > 60% — reproducción y maduración dentro del ciclo
 *           anual. La planta es más resiliente por su sistema radicular
 *           establecido, pero el impacto económico sigue siendo significativo
 *           por el valor acumulado del cultivo.
 *
 * - NUNCA BAJA: un cultivo permanente nunca genera severidad BAJA dado que
 *               representa una inversión plurianual con ciclos productivos
 *               repetitivos cada año.
 *
 * Fenología anual:
 * A diferencia de los transitorios (que se evalúan sobre el ciclo completo),
 * los permanentes se evalúan sobre un ciclo productivo anual de 365 días.
 * Esto refleja que plantas como palma, mango o café repiten su ciclo
 * floración-fructificación-cosecha cada año, independientemente de su edad.
 * Se usa (diasTranscurridos % 365) para obtener el día dentro del año
 * productivo actual.
 */
@Component("PERMANENTE")
public class SeveridadPermanente extends SeveridadStrategy {

    /** Días del ciclo productivo anual de referencia para cultivos permanentes */
    private static final int CICLO_ANUAL = 365;

    // Pesos hídricos diferenciados para permanentes:
    // la reproducción concentra 57.5% porque floración y fructificación
    // son las fases más críticas del ciclo anual.
    private static final double PESO_EST_PERM  = 0.15;
    private static final double PESO_VEG_PERM  = 0.275;
    private static final double PESO_REP_PERM  = 0.575;

    /**
     * Calcula la severidad basándose en el porcentaje del ciclo productivo
     * anual transcurrido (no del ciclo de vida total del cultivo).
     */
    @Override
    public String calcularSeveridad(int diasRestantes, int diasCosechaProm) {
        int diasTranscurridos = diasCosechaProm - diasRestantes;
        int diasEnCicloAnual  = diasTranscurridos % CICLO_ANUAL;
        double pct = calcularPorcentaje(diasEnCicloAnual, CICLO_ANUAL);

        if (pct <= UMBRAL_VEGETATIVO) return "ALTA";
        else                          return "MEDIA";
    }

    @Override
    public String calcularSeveridadLluviaExtrema(int diasRestantes, int diasCosechaProm) {
        int diasTranscurridos = diasCosechaProm - diasRestantes;
        int diasEnCicloAnual  = diasTranscurridos % CICLO_ANUAL;
        double pct = calcularPorcentaje(diasEnCicloAnual, CICLO_ANUAL);

        // Vegetativo/establecimiento (≤ 60% del ciclo anual): MEDIA
        // — raíces profundas, planta resiliente al exceso hídrico.
        // Floración/fructificación (> 60%): ALTA
        // — flores y frutos jóvenes muy sensibles; riesgo de aborto floral
        // y caída de frutos por encharcamiento o daño mecánico.
        return (pct <= UMBRAL_VEGETATIVO) ? "MEDIA" : "ALTA";
    }

    /**
     * Normaliza el umbral de lluvia sobre el ciclo productivo anual (365 días).
     * Pesos hídricos diferenciados: reproducción concentra 57.5% porque cada
     * ciclo anual depende de floración y fructificación óptimas.
     */
    @Override
    public double normalizarLluvia(double lluviaUmbral, int diasCiclo,
                                   int diasAcumulados, int diasRestantes) {
        int diasTranscurridos = diasCiclo - diasRestantes;
        int diasEnCicloAnual  = diasTranscurridos % CICLO_ANUAL;
        double pct = calcularPorcentaje(diasEnCicloAnual, CICLO_ANUAL);

        double peso;
        double fraccionEtapa;
        if (pct <= UMBRAL_ESTABLECIMIENTO) {
            peso = PESO_EST_PERM;
            fraccionEtapa = UMBRAL_ESTABLECIMIENTO;
        } else if (pct <= UMBRAL_VEGETATIVO) {
            peso = PESO_VEG_PERM;
            fraccionEtapa = UMBRAL_VEGETATIVO - UMBRAL_ESTABLECIMIENTO;
        } else {
            peso = PESO_REP_PERM;
            fraccionEtapa = 1.0 - UMBRAL_VEGETATIVO;
        }

        double lluviaDiariaEtapa = (lluviaUmbral * peso) / (CICLO_ANUAL * fraccionEtapa);
        return lluviaDiariaEtapa * diasAcumulados;
    }
}