package com.agrocesar.service;

import com.agrocesar.dto.CultivoConUmbralesDTO;
import com.agrocesar.dto.DailyForecast;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * SiembraOptimaService analiza el pronostico climatico de los proximos 7 dias
 * para un cultivo registrado y determina si la ventana actual es optima para
 * sembrar, usando los umbrales efectivos del catalogo y generando una guia
 * de accion preventiva via Groq.
 *
 * Patron: mismo que RecomendacionService — retorna null si Groq falla,
 * nunca interrumpe el flujo principal (RNF05).
 */
@Service
public class SiembraOptimaService {

    private static final Logger log = LoggerFactory.getLogger(SiembraOptimaService.class);

    /**
     * Limite de caracteres para la respuesta de Groq.
     * Se recorta si la IA devuelve mas de 1200 caracteres.
     */
    private static final int MAX_CHARS = 1200;

    /**
     * Porcentaje maximo de dias del pronostico que pueden estar
     * fuera de umbral para considerar la ventana como optima.
     * Ejemplo: 0.30 significa que si mas del 30% de los dias
     * tienen condiciones adversas, la ventana NO es optima.
     */
    private static final double UMBRAL_RIESGO = 0.30;

    private final WebClient webClient;
    private final WeatherService weatherService;

    @Value("${groq.api.key}")
    private String apiKey;

    @Value("${groq.api.model}")
    private String model;

    /**
     * Constructor. El WebClient se configura con la URL base de Groq
     * inyectada desde application.properties (groq.api.url).
     *
     * @param apiUrl         URL base de la API de Groq
     * @param weatherService servicio de pronostico Open-Meteo
     */
    public SiembraOptimaService(@Value("${groq.api.url}") String apiUrl,
            WeatherService weatherService) {
        this.webClient = WebClient.builder().baseUrl(apiUrl).build();
        this.weatherService = weatherService;
    }

    /**
     * Resultado del analisis de siembra optima para un cultivo.
     * Se pasa como modelo a la vista Thymeleaf.
     *
     * @param esOptima     true si la ventana actual es favorable para sembrar
     * @param diasEnRiesgo numero de dias del pronostico con condiciones adversas
     * @param totalDias    total de dias evaluados (hasta 7)
     * @param guia         texto generado por Groq con recomendaciones concretas
     * @param cultivo      nombre del cultivo evaluado
     * @param municipio    municipio donde esta registrado el cultivo
     * @param pronostico   lista de pronosticos diarios para mostrar en la vista
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
            CultivoConUmbralesDTO umbrales) {
    }

    /**
     * Analiza si la ventana actual es optima para sembrar el cultivo dado.
     * Obtiene el pronostico de 7 dias, cuenta los dias con condiciones adversas
     * segun los umbrales efectivos, y genera una guia via Groq.
     *
     * @param cultivo DTO con los umbrales efectivos y coordenadas del cultivo
     * @return ResultadoSiembra con el analisis completo, o null si el pronostico
     *         falla
     */
    public ResultadoSiembra analizar(CultivoConUmbralesDTO cultivo) {
        List<DailyForecast> pronostico = weatherService.obtenerPronostico(
                cultivo.getLatitud(), cultivo.getLongitud(), 16);

        if (pronostico == null || pronostico.isEmpty()) {
            log.warn("[SiembraOptima] Sin pronostico para cultivo={} municipio={}",
                    cultivo.getCultivo(), cultivo.getMunicipio());
            return null;
        }

        int diasEnRiesgo = contarDiasEnRiesgo(pronostico, cultivo);
        int totalDias = pronostico.size();
        boolean esOptima = (diasEnRiesgo / (double) totalDias) <= UMBRAL_RIESGO;
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
                cultivo);
    }

    /**
     * Cuenta cuantos dias del pronostico tienen al menos una condicion climatica
     * fuera de los umbrales efectivos del cultivo.
     * Un dia se cuenta una sola vez aunque supere varios umbrales.
     *
     * @param pronostico lista de dias pronosticados
     * @param cultivo    DTO con los umbrales efectivos
     * @return numero de dias con condiciones adversas
     */
    private int contarDiasEnRiesgo(List<DailyForecast> pronostico,
            CultivoConUmbralesDTO cultivo) {
        int riesgo = 0;
        for (DailyForecast dia : pronostico) {
            if (diaEsAdverso(dia, cultivo))
                riesgo++;
        }
        return riesgo;
    }

    /**
     * Determina si un dia especifico tiene condiciones adversas para el cultivo.
     * Evalua temperatura, lluvia y humedad contra los umbrales efectivos.
     * Retorna true en cuanto detecta la primera condicion adversa.
     *
     * @param dia     pronostico del dia a evaluar
     * @param cultivo DTO con los umbrales efectivos
     * @return true si el dia tiene al menos una condicion fuera de umbral
     */
    private boolean diaEsAdverso(DailyForecast dia, CultivoConUmbralesDTO cultivo) {
        // Temperatura maxima excesiva
        if (cultivo.getTempMaxEfectiva() != null && dia.getTempMax() != null
                && dia.getTempMax() > cultivo.getTempMaxEfectiva())
            return true;

        // Temperatura minima insuficiente
        if (cultivo.getTempMinEfectiva() != null && dia.getTempMin() != null
                && dia.getTempMin() < cultivo.getTempMinEfectiva())
            return true;

        // Lluvia excesiva
        if (cultivo.getLluviaMaxEfectiva() != null && dia.getLluviaMm() != null
                && dia.getLluviaMm() > cultivo.getLluviaMaxEfectiva())
            return true;

        // Lluvia insuficiente
        if (cultivo.getLluviaMinEfectiva() != null && dia.getLluviaMm() != null
                && dia.getLluviaMm() < cultivo.getLluviaMinEfectiva())
            return true;

        // Humedad excesiva
        if (cultivo.getHumedadMaxEfectiva() != null && dia.getHumedadMax() != null
                && dia.getHumedadMax() > cultivo.getHumedadMaxEfectiva())
            return true;

        // Humedad insuficiente
        if (cultivo.getHumedadMinEfectiva() != null && dia.getHumedadMin() != null
                && dia.getHumedadMin() < cultivo.getHumedadMinEfectiva())
            return true;

        return false;
    }

    /**
     * Genera una guia de accion preventiva usando Groq.
     * Si la llamada falla, retorna un mensaje de fallback predefinido
     * para no interrumpir la experiencia del usuario (RNF05).
     *
     * @param cultivo      DTO del cultivo evaluado
     * @param pronostico   lista de dias pronosticados
     * @param esOptima     resultado del analisis de ventana
     * @param diasEnRiesgo numero de dias adversos detectados
     * @param totalDias    total de dias evaluados
     * @return texto con la guia de accion, nunca null
     */
    private String generarGuia(CultivoConUmbralesDTO cultivo,
            List<DailyForecast> pronostico,
            boolean esOptima,
            int diasEnRiesgo,
            int totalDias) {
        try {
            String prompt = construirPrompt(cultivo, pronostico, esOptima,
                    diasEnRiesgo, totalDias);

            Map<?, ?> body = Map.of(
                    "model", model,
                    "max_tokens", 400,
                    "messages", List.of(
                            Map.of("role", "user", "content", prompt)));

            Map<String, Object> respuesta = webClient.post()
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
                    })
                    .timeout(Duration.ofSeconds(10))
                    .block();

            String texto = extraerTexto(respuesta);

            if (texto != null && texto.length() > MAX_CHARS)
                texto = texto.substring(0, MAX_CHARS);

            return texto != null ? texto : fallback(esOptima, cultivo.getCultivo());

        } catch (Exception e) {
            log.warn("[SiembraOptima] Groq no disponible para cultivo={}: {}",
                    cultivo.getCultivo(), e.getMessage());
            return fallback(esOptima, cultivo.getCultivo());
        }
    }

    /**
     * Construye el prompt para Groq con el contexto climatico y agronomico
     * del cultivo evaluado.
     *
     * @param cultivo      DTO con datos del cultivo y umbrales
     * @param pronostico   lista de dias pronosticados
     * @param esOptima     si la ventana actual es favorable
     * @param diasEnRiesgo dias con condiciones adversas
     * @param totalDias    total de dias analizados
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
                    d.getLluviaMm(), d.getHumedadMax()));
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
     * Extrae el contenido de texto de la respuesta JSON de Groq.
     * Navega por choices[0].message.content.
     *
     * @param respuesta mapa con la respuesta completa de Groq
     * @return texto generado, o null si la estructura es invalida
     */
    private String extraerTexto(Map<String, Object> respuesta) {
        if (respuesta == null)
            return null;
        var choices = (List<?>) respuesta.get("choices");
        if (choices == null || choices.isEmpty())
            return null;
        var message = (Map<?, ?>) ((Map<?, ?>) choices.get(0)).get("message");
        if (message == null)
            return null;
        return (String) message.get("content");
    }

    /**
     * Busca el primer bloque de al menos 2 dias consecutivos sin riesgo
     * en el pronostico extendido, a partir del dia 2 (omite hoy).
     * Retorna una cadena con el rango de fechas en formato dd/MM,
     * o null si no encuentra ninguna ventana favorable en el periodo.
     *
     * @param pronostico lista de dias pronosticados (hasta 16 dias)
     * @param cultivo    DTO con los umbrales efectivos
     * @return rango de fechas de la proxima ventana, o null si no hay
     */
    private String calcularProximaVentana(List<DailyForecast> pronostico,
            CultivoConUmbralesDTO cultivo) {
        // Omite el dia 0 (hoy) para buscar ventanas futuras
        for (int i = 1; i < pronostico.size() - 1; i++) {
            DailyForecast diaActual = pronostico.get(i);
            DailyForecast diaSiguiente = pronostico.get(i + 1);

            if (!diaEsAdverso(diaActual, cultivo) && !diaEsAdverso(diaSiguiente, cultivo)) {
                LocalDate inicio = LocalDate.parse(diaActual.getFecha(),
                        DateTimeFormatter.ISO_LOCAL_DATE);
                LocalDate fin = LocalDate.parse(diaSiguiente.getFecha(),
                        DateTimeFormatter.ISO_LOCAL_DATE);

                // Extiende la ventana si los dias siguientes tambien son favorables
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
     * Mensaje de fallback cuando Groq no esta disponible.
     * Garantiza que la vista siempre reciba un texto util (RNF05).
     *
     * @param esOptima true si la ventana es favorable
     * @param cultivo  nombre del cultivo
     * @return mensaje predefinido segun el resultado del analisis
     */
    private String fallback(boolean esOptima, String cultivo) {
        return esOptima
                ? "Las condiciones climaticas de los proximos dias son favorables para sembrar " + cultivo
                        + ". Procede con la siembra y mantente atento a las alertas del sistema."
                : "Las condiciones climaticas actuales presentan riesgo para sembrar " + cultivo
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