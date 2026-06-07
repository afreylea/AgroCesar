package com.agrocesar.service;

import com.agrocesar.dto.CultivoConUmbralesDTO;
import com.agrocesar.dto.DailyForecast;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * SiembraOptimaService analiza el pronóstico climático de los próximos 16 días
 * para un cultivo registrado y determina si la ventana actual es óptima para
 * sembrar, usando los umbrales efectivos del catálogo y generando una guía
 * de acción preventiva vía Groq.
 *
 * Patrón: mismo que RecomendacionService — retorna null si Groq falla,
 * nunca interrumpe el flujo principal (RNF05).
 */
@Service
public class SiembraOptimaService {

    private static final Logger log = LoggerFactory.getLogger(SiembraOptimaService.class);

    /**
     * Porcentaje máximo de días del pronóstico que pueden estar
     * fuera de umbral para considerar la ventana como óptima.
     * Ejemplo: 0.30 significa que si más del 30% de los días
     * tienen condiciones adversas, la ventana NO es óptima.
     */
    private static final double UMBRAL_RIESGO = 0.30;

    /**
     * Tolerancia absoluta en grados Celsius aplicada a los umbrales de temperatura
     * antes de evaluar si un día es adverso. Amortigua variaciones de pronóstico
     * menores que no implican riesgo real para el cultivo.
     */
    private static final double TOLERANCIA_TEMPERATURA = 2.0;

    /**
     * Tolerancia en puntos porcentuales aplicada al umbral de humedad
     * antes de comparar contra la humedad media diaria.
     * Se suma al máximo y se resta al mínimo para crear una zona de indiferencia.
     */
    private static final double TOLERANCIA_HUMEDAD = 5.0;

    /**
     * Precipitación diaria en mm a partir de la cual se considera un evento
     * extremo de lluvia, independientemente del umbral del cultivo.
     * Coincide con {@link SeveridadStrategy#LLUVIA_EXTREMA_MM}.
     */
    private static final double LLUVIA_EXTREMA_MM = SeveridadStrategy.LLUVIA_EXTREMA_MM;

    /**
     * Precipitación diaria en mm por debajo de la cual se considera un día
     * seco, desfavorable para la germinación e implantación inicial del cultivo.
     */
    private static final double LLUVIA_DIA_SECO_MM = 5.0;

    private final WeatherService weatherService;
    private final RecomendacionService recomendacionService;

    /**
     * Constructor con inyección por constructor.
     *
     * @param weatherService       servicio de pronóstico Open-Meteo
     * @param recomendacionService servicio centralizado de llamadas a Groq
     */
    public SiembraOptimaService(WeatherService weatherService,
            RecomendacionService recomendacionService) {
        this.weatherService = weatherService;
        this.recomendacionService = recomendacionService;
    }

    /**
     * Resultado del análisis de siembra óptima para un cultivo.
     * Se pasa como modelo a la vista Thymeleaf.
     *
     * @param esOptima      true si la ventana actual es favorable para sembrar
     * @param diasEnRiesgo  número de días del pronóstico con condiciones adversas
     * @param totalDias     total de días evaluados (hasta 16)
     * @param guia          texto generado por Groq con recomendaciones concretas
     * @param cultivo       nombre del cultivo evaluado
     * @param municipio     municipio donde está registrado el cultivo
     * @param pronostico    lista de pronósticos diarios para mostrar en la vista
     * @param proximaVentana rango de fechas de la próxima ventana favorable, o null
     * @param umbrales      DTO del cultivo con los umbrales efectivos
     * @param diasAdversos  lista de booleanos, una entrada por día del pronóstico;
     *                      true si ese día es adverso según diaEsAdverso (opción A).
     *                      Permite a la vista colorear el pronóstico extendido sin
     *                      reimplementar la lógica de evaluación.
     * @param factoresHoy   mapa ordenado con el estado de cada factor climático para
     *                      hoy: claves "temperatura", "lluvia", "humedad";
     *                      valores "Favorable", "Excesiva" o "Insuficiente" (opción B).
     *                      Permite a la vista mostrar los badges sin duplicar lógica.
     */
    public record ResultadoSiembra(
            boolean esOptima,
            int diasEnRiesgo,
            int totalDias,
            String guia,
            String cultivo,
            String municipio,
            List<DailyForecast> pronostico,
            String proximaVentana,
            CultivoConUmbralesDTO umbrales,
            List<Boolean> diasAdversos,
            Map<String, String> factoresHoy) {
    }

    /**
     * Analiza si la ventana actual es óptima para sembrar el cultivo dado.
     * Obtiene el pronóstico de 16 días, cuenta los días con condiciones adversas
     * según los umbrales efectivos, y genera una guía vía Groq.
     *
     * @param cultivo DTO con los umbrales efectivos y coordenadas del cultivo
     * @return ResultadoSiembra con el análisis completo, o null si el pronóstico
     *         falla
     */
    public ResultadoSiembra analizar(CultivoConUmbralesDTO cultivo) {
        List<DailyForecast> pronostico = weatherService.obtenerPronostico(
                cultivo.getLatitud(), cultivo.getLongitud(), 16);

        if (pronostico == null || pronostico.isEmpty()) {
            log.warn("[SiembraOptima] Sin pronóstico para cultivo={} municipio={}",
                    cultivo.getCultivo(), cultivo.getMunicipio());
            return null;
        }

        // Construye la lista de adversidad por día 
        List<Boolean> diasAdversos = new ArrayList<>();
        for (DailyForecast dia : pronostico) {
            diasAdversos.add(diaEsAdverso(dia, cultivo));
        }

        int diasEnRiesgo = (int) diasAdversos.stream().filter(Boolean::booleanValue).count();
        int totalDias    = pronostico.size();
        boolean esOptima = (diasEnRiesgo / (double) totalDias) <= UMBRAL_RIESGO;

        // Construye el mapa de factores para hoy 
        Map<String, String> factoresHoy = evaluarFactoresHoy(pronostico.get(0), cultivo);

        String proximaVentana = esOptima ? null : calcularProximaVentana(pronostico, cultivo);
        String guia = generarGuia(cultivo, pronostico, esOptima, diasEnRiesgo, totalDias);

        return new ResultadoSiembra(
                esOptima,
                diasEnRiesgo,
                totalDias,
                guia,
                cultivo.getCultivo(),
                cultivo.getMunicipio(),
                pronostico,
                proximaVentana,
                cultivo,
                diasAdversos,
                factoresHoy);
    }

    /**
     * Determina si un día específico tiene condiciones adversas para el cultivo.
     * Aplica tolerancias específicas por variable antes de comparar contra umbrales: 
     *   emperatura: ±{@value #TOLERANCIA_TEMPERATURA}°C
     *   Lluvia máxima: evento extremo ≥ {@value #LLUVIA_EXTREMA_MM} mm (sin tolerancia)
     *   Lluvia mínima: día seco &lt; {@value #LLUVIA_DIA_SECO_MM} mm
     *   Humedad: ±{@value #TOLERANCIA_HUMEDAD}% sobre la humedad media diaria
     * Retorna true en cuanto detecta la primera condición adversa.
     *
     * @param dia     pronóstico del día a evaluar
     * @param cultivo DTO con los umbrales efectivos
     * @return true si el día tiene al menos una condición fuera de umbral
     */
    private boolean diaEsAdverso(DailyForecast dia, CultivoConUmbralesDTO cultivo) {
        // Temperatura máxima excesiva (con tolerancia)
        if (cultivo.getTempMaxEfectiva() != null && dia.getTempMax() != null
                && dia.getTempMax() > cultivo.getTempMaxEfectiva() + TOLERANCIA_TEMPERATURA)
            return true;

        // Temperatura mínima insuficiente (con tolerancia)
        if (cultivo.getTempMinEfectiva() != null && dia.getTempMin() != null
                && dia.getTempMin() < cultivo.getTempMinEfectiva() - TOLERANCIA_TEMPERATURA)
            return true;

        // Lluvia extrema diaria — riesgo de encharcamiento o daño mecánico
        if (dia.getLluviaMm() != null && dia.getLluviaMm() >= LLUVIA_EXTREMA_MM)
            return true;

        // Día seco — insuficiente para germinación e implantación inicial
        if (dia.getLluviaMm() != null && dia.getLluviaMm() < LLUVIA_DIA_SECO_MM)
            return true;

        // Humedad excesiva (humedad media con tolerancia)
        if (cultivo.getHumedadMaxEfectiva() != null && dia.getHumedadMedia() != null
                && dia.getHumedadMedia() > cultivo.getHumedadMaxEfectiva() + TOLERANCIA_HUMEDAD)
            return true;

        // Humedad insuficiente (humedad media con tolerancia)
        if (cultivo.getHumedadMinEfectiva() != null && dia.getHumedadMedia() != null
                && dia.getHumedadMedia() < cultivo.getHumedadMinEfectiva() - TOLERANCIA_HUMEDAD)
            return true;

        return false;
    }

    /**
     * Evalúa el estado de cada factor climático para el día de hoy y retorna
     * un mapa ordenado con los badges que la vista debe mostrar.
     * Las claves son "temperatura", "lluvia" y "humedad".
     * Los valores son "Favorable", "Excesiva" o "Insuficiente".
     *
     * La lógica aplica los mismos criterios y tolerancias que
     * {@link #diaEsAdverso}, pero granularizados por factor para que la
     * vista pueda mostrar un badge descriptivo en lugar de un booleano global.
     *
     * @param hoy     pronóstico del día actual (pronostico[0])
     * @param cultivo DTO con los umbrales efectivos
     * @return mapa ordenado con el estado de cada factor
     */
    private Map<String, String> evaluarFactoresHoy(DailyForecast hoy,
            CultivoConUmbralesDTO cultivo) {
        Map<String, String> factores = new LinkedHashMap<>();

        // Temperatura
        if (cultivo.getTempMaxEfectiva() != null && hoy.getTempMax() != null
                && hoy.getTempMax() > cultivo.getTempMaxEfectiva() + TOLERANCIA_TEMPERATURA) {
            factores.put("temperatura", "Excesiva");
        } else if (cultivo.getTempMinEfectiva() != null && hoy.getTempMin() != null
                && hoy.getTempMin() < cultivo.getTempMinEfectiva() - TOLERANCIA_TEMPERATURA) {
            factores.put("temperatura", "Insuficiente");
        } else {
            factores.put("temperatura", "Favorable");
        }

        // Lluvia
        if (hoy.getLluviaMm() != null && hoy.getLluviaMm() >= LLUVIA_EXTREMA_MM) {
            factores.put("lluvia", "Excesiva");
        } else if (cultivo.getLluviaMinEfectiva() != null
                && cultivo.getLluviaMinEfectiva() > 0
                && hoy.getLluviaMm() != null
                && hoy.getLluviaMm() < LLUVIA_DIA_SECO_MM) {
            factores.put("lluvia", "Insuficiente");
        } else {
            factores.put("lluvia", "Favorable");
        }

        // Humedad (usa humedad media diaria de Open-Meteo)
        if (cultivo.getHumedadMaxEfectiva() != null && hoy.getHumedadMedia() != null
                && hoy.getHumedadMedia() > cultivo.getHumedadMaxEfectiva() + TOLERANCIA_HUMEDAD) {
            factores.put("humedad", "Excesiva");
        } else if (cultivo.getHumedadMinEfectiva() != null && hoy.getHumedadMedia() != null
                && hoy.getHumedadMedia() < cultivo.getHumedadMinEfectiva() - TOLERANCIA_HUMEDAD) {
            factores.put("humedad", "Insuficiente");
        } else {
            factores.put("humedad", "Favorable");
        }

        return factores;
    }

    /**
     * Genera una guía de acción preventiva usando Groq a través de
     * {@link RecomendacionService#llamarGroq}.
     * Si la llamada falla, retorna un mensaje de fallback predefinido
     * para no interrumpir la experiencia del usuario (RNF05).
     *
     * @param cultivo      DTO del cultivo evaluado
     * @param pronostico   lista de días pronosticados
     * @param esOptima     resultado del análisis de ventana
     * @param diasEnRiesgo número de días adversos detectados
     * @param totalDias    total de días evaluados
     * @return texto con la guía de acción, nunca null
     */
    private String generarGuia(CultivoConUmbralesDTO cultivo,
            List<DailyForecast> pronostico,
            boolean esOptima,
            int diasEnRiesgo,
            int totalDias) {
        try {
            String prompt = construirPrompt(cultivo, pronostico, esOptima, diasEnRiesgo, totalDias);
            String texto  = recomendacionService.llamarGroq(prompt, 400);

            return texto != null ? texto : fallback(esOptima, cultivo.getCultivo());
        } catch (Exception e) {
            log.warn("[SiembraOptima] Groq no disponible para cultivo={}: {}",
                    cultivo.getCultivo(), e.getMessage());
            return fallback(esOptima, cultivo.getCultivo());
        }
    }

    /**
     * Construye el prompt para Groq con el contexto climático y agronómico
     * del cultivo evaluado. Usa la humedad media diaria (relative_humidity_2m_mean)
     * para el resumen del pronóstico, que es más representativa del estrés real
     * del cultivo durante el día que los extremos máximo/mínimo.
     *
     * @param cultivo      DTO con datos del cultivo y umbrales
     * @param pronostico   lista de días pronosticados
     * @param esOptima     si la ventana actual es favorable
     * @param diasEnRiesgo días con condiciones adversas
     * @param totalDias    total de días analizados
     * @return prompt listo para enviar a Groq
     */
    private String construirPrompt(CultivoConUmbralesDTO cultivo,
            List<DailyForecast> pronostico,
            boolean esOptima,
            int diasEnRiesgo,
            int totalDias) {
        StringBuilder resumenClima = new StringBuilder();
        for (int i = 0; i < pronostico.size(); i++) {
            DailyForecast d = pronostico.get(i);
            resumenClima.append(String.format(
                    "Dia %d: temp %.1f/%.1f C, lluvia %.1f mm, humedad %.0f%%\n",
                    i + 1, d.getTempMin(), d.getTempMax(),
                    d.getLluviaMm(),
                    d.getHumedadMedia() != null ? d.getHumedadMedia() : 0.0));
        }

        return String.format(
                "Eres un agronomo experto en cultivos del Caribe colombiano, especificamente del departamento del Cesar. "
                        +
                        "El agricultor cultiva %s en %s. " +
                        "Umbrales optimos del cultivo: temperatura entre %.1f y %.1f C, " +
                        "lluvia entre %.1f y %.1f mm, humedad entre %.0f y %.0f por ciento. " +
                        "Pronostico de los proximos %d dias:\n%s" +
                        "De esos %d dias, %d tienen condiciones adversas para la siembra. " +
                        "La ventana actual %s optima para sembrar. " +
                        "En maximo 3 oraciones concretas y directas, indica al agricultor que debe hacer: " +
                        "si debe sembrar ahora, esperar, o tomar alguna accion preventiva especifica.",
                cultivo.getCultivo(), cultivo.getMunicipio(),
                safe(cultivo.getTempMinEfectiva()), safe(cultivo.getTempMaxEfectiva()),
                safe(cultivo.getLluviaMinEfectiva()), safe(cultivo.getLluviaMaxEfectiva()),
                safe(cultivo.getHumedadMinEfectiva()), safe(cultivo.getHumedadMaxEfectiva()),
                totalDias, resumenClima,
                totalDias, diasEnRiesgo,
                esOptima ? "ES" : "NO ES");
    }

    /**
     * Busca el primer bloque de al menos 2 días consecutivos sin riesgo
     * en el pronóstico extendido, a partir del día 2 (omite hoy).
     * Retorna una cadena con el rango de fechas en formato dd/MM,
     * o null si no encuentra ninguna ventana favorable en el periodo.
     *
     * @param pronostico lista de días pronosticados (hasta 16 días)
     * @param cultivo    DTO con los umbrales efectivos
     * @return rango de fechas de la próxima ventana, o null si no hay
     */
    private String calcularProximaVentana(List<DailyForecast> pronostico,
            CultivoConUmbralesDTO cultivo) {
        // Omite el día 0 (hoy) para buscar ventanas futuras
        for (int i = 1; i < pronostico.size() - 1; i++) {
            DailyForecast diaActual    = pronostico.get(i);
            DailyForecast diaSiguiente = pronostico.get(i + 1);

            if (!diaEsAdverso(diaActual, cultivo) && !diaEsAdverso(diaSiguiente, cultivo)) {
                LocalDate inicio = LocalDate.parse(diaActual.getFecha(),
                        DateTimeFormatter.ISO_LOCAL_DATE);
                LocalDate fin    = LocalDate.parse(diaSiguiente.getFecha(),
                        DateTimeFormatter.ISO_LOCAL_DATE);

                // Extiende la ventana si los días siguientes también son favorables
                int j = i + 2;
                while (j < pronostico.size()
                        && !diaEsAdverso(pronostico.get(j), cultivo)) {
                    fin = LocalDate.parse(pronostico.get(j).getFecha(),
                            DateTimeFormatter.ISO_LOCAL_DATE);
                    j++;
                }

                return inicio.getDayOfMonth() + "/" +
                        String.format("%02d", inicio.getMonthValue()) +
                        " — " +
                        fin.getDayOfMonth() + "/" +
                        String.format("%02d", fin.getMonthValue());
            }
        }
        return null;
    }

    /**
     * Mensaje de fallback cuando Groq no está disponible.
     * Garantiza que la vista siempre reciba un texto útil (RNF05).
     *
     * @param esOptima true si la ventana es favorable
     * @param cultivo  nombre del cultivo
     * @return mensaje predefinido según el resultado del análisis
     */
    private String fallback(boolean esOptima, String cultivo) {
        return esOptima
                ? "Las condiciones climáticas de los próximos días son favorables para sembrar " + cultivo
                        + ". Procede con la siembra y mantente atento a las alertas del sistema."
                : "Las condiciones climáticas actuales presentan riesgo para sembrar " + cultivo
                        + ". Se recomienda esperar a que mejoren las condiciones antes de iniciar la siembra.";
    }

    /**
     * Convierte un Double nullable a double primitivo de forma segura.
     * Retorna 0.0 si el valor es null para evitar NullPointerException
     * en String.format.
     *
     * @param valor Double que puede ser null
     * @return valor primitivo o 0.0 si es null
     */
    private double safe(Double valor) {
        return valor != null ? valor : 0.0;
    }
}