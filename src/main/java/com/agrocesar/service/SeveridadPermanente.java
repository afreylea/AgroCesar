package com.agrocesar.service;

import org.springframework.stereotype.Component;

/**
 * Implementación de SeveridadStrategy para cultivos PERMANENTES.
 *
 * Lógica fenológica:
 * - ALTA  : pctMadurez ≤ 60% — establecimiento + crecimiento vegetativo.
 *           En permanentes estas fases son plurianuales. El daño en esta
 *           etapa afecta años de producción futura y puede comprometer
 *           toda la inversión acumulada.
 *
 * - MEDIA : pctMadurez > 60% — reproducción y maduración.
 *           La planta es más resiliente por su sistema radicular establecido,
 *           pero el impacto económico de cualquier evento climático sigue
 *           siendo significativo por el valor acumulado del cultivo.
 *
 * - NUNCA BAJA: un cultivo permanente nunca genera severidad BAJA
 *               dado que representa una inversión plurianual.
 */
@Component("PERMANENTE")
public class SeveridadPermanente extends SeveridadStrategy {

    @Override
    public String calcular(int diasRestantes, int diasCosechaProm) {
        int diasTranscurridos = diasCosechaProm - diasRestantes;
        double pct = calcularPorcentaje(diasTranscurridos, diasCosechaProm);

        if (pct <= UMBRAL_VEGETATIVO) return "ALTA";
        else                          return "MEDIA";
    }
}