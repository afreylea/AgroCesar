package com.agrocesar.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.agrocesar.dto.CultivoResumen;
import com.agrocesar.dto.DailyForecast;
import com.agrocesar.dto.RankingCultivoDTO;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@Service
public class RecomendacionService {

    private static final Logger log = LoggerFactory.getLogger(RecomendacionService.class);
    private static final int MAX_CHARS = 1000;

    private final WebClient webClient;

    @Value("${groq.api.key}")
    private String apiKey;

    @Value("${groq.api.model}")
    private String model;

    public RecomendacionService(@Value("${groq.api.url}") String apiUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(apiUrl)
                .build();
    }

    /**
     * Genera una recomendación agronómica para la alerta detectada.
     * Retorna null si la llamada falla (RNF05: no interrumpe el motor).
     */
    public String generar(String tipoAlerta, String cultivo, double valorDetectado,
            String severidad, int diasRestantes, int diasCosechaProm) {
        try {
            String prompt = construirPrompt(tipoAlerta, cultivo, valorDetectado,
                    severidad, diasRestantes, diasCosechaProm);

            Map<?, ?> body = Map.of(
                    "model", model,
                    "max_tokens", 300,
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
            return texto;

        } catch (Exception e) {
            log.warn("Groq no disponible para alerta {}/{}: {}", tipoAlerta, cultivo, e.getMessage());
            return null;
        }
    }

    private String construirPrompt(String tipoAlerta, String cultivo,
            double valorDetectado, String severidad,
            int diasRestantes, int diasCosechaProm) {
        int diasTranscurridos = diasCosechaProm - diasRestantes;
        int pctCiclo = (int) ((diasTranscurridos / (double) diasCosechaProm) * 100);

        return String.format(
                "Eres un agrónomo experto en cultivos del Caribe colombiano. " +
                        "Se detectó una alerta de tipo %s (severidad %s) en el cultivo de %s. " +
                        "Valor registrado: %.2f. " +
                        "El cultivo lleva %d%% de su ciclo completado (%d días restantes). " +
                        "En máximo 2 oraciones concretas y directas, indica qué debe hacer el agricultor ahora mismo.",
                tipoAlerta, severidad, cultivo, valorDetectado, pctCiclo, diasRestantes);
    }

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
     * Recomendacion especifica para un cultivo activo del dashboard.
     * Retorna null si Groq falla — no interrumpe el flujo.
     */
    public String generarRecomendacionCultivo(String cultivoNombre, String municipio,
            double hectareas, String fechaSiembra,
            List<DailyForecast> pronostico) {
        try {
            String prompt = construirPromptCultivo(cultivoNombre, municipio,
                    hectareas, fechaSiembra, pronostico);
            return llamarGroq(prompt, 250);
        } catch (Exception e) {
            log.warn("Groq no disponible para recomendacion cultivo {}: {}", cultivoNombre, e.getMessage());
            return null;
        }
    }

    /**
     * Recomendacion general para todos los cultivos del agricultor.
     * Incluye contexto del ranking del Cesar.
     * Retorna null si Groq falla — no interrumpe el flujo.
     */
    public String generarRecomendacionGeneral(List<CultivoResumen> cultivos,
            String municipioPrincipal,
            List<DailyForecast> pronostico,
            List<RankingCultivoDTO> ranking) {
        try {
            String prompt = construirPromptGeneral(cultivos, municipioPrincipal,
                    pronostico, ranking);
            return llamarGroq(prompt, 350);
        } catch (Exception e) {
            log.warn("Groq no disponible para recomendacion general: {}", e.getMessage());
            return null;
        }
    }

    /* ── Prompts ──────────────────────────────────────────────── */

    private String construirPromptCultivo(String cultivoNombre, String municipio,
            double hectareas, String fechaSiembra,
            List<DailyForecast> pronostico) {
        StringBuilder sb = new StringBuilder();
        sb.append("Eres un agronomo experto en cultivos del Caribe colombiano. ");
        sb.append(String.format("El agricultor tiene %.1f hectareas de %s en %s, sembrado el %s. ",
                hectareas, cultivoNombre, municipio, fechaSiembra));
        sb.append("Pronostico climatico para los proximos 7 dias: ");
        for (int i = 0; i < Math.min(pronostico.size(), 7); i++) {
            DailyForecast d = pronostico.get(i);
            sb.append(String.format("Dia %d: %.1f°C max, %.1fmm lluvia, %.0f%% humedad. ",
                    i + 1,
                    d.getTempMax() != null ? d.getTempMax() : 0.0,
                    d.getLluviaMm() != null ? d.getLluviaMm() : 0.0,
                    d.getHumedadMax() != null ? d.getHumedadMax() : 0.0));
        }
        sb.append("En maximo 3 oraciones concretas y simples, ");
        sb.append("genera una recomendacion agronomica practica para este cultivo ");
        sb.append("basada en el clima de los proximos dias. ");
        sb.append("Menciona el cultivo y una accion especifica que el agricultor debe hacer ahora.");
        return sb.toString();
    }

    private String construirPromptGeneral(List<CultivoResumen> cultivos,
            String municipioPrincipal,
            List<DailyForecast> pronostico,
            List<RankingCultivoDTO> ranking) {
        StringBuilder sb = new StringBuilder();
        sb.append("Eres un agronomo experto en cultivos del Caribe colombiano. ");
        sb.append("El agricultor tiene los siguientes cultivos en el Cesar: ");
        for (CultivoResumen c : cultivos) {
            sb.append(String.format("%s en %s (%.1f ha), ",
                    c.getNombreCultivo(), c.getMunicipio(), c.getHectareas()));
        }
        sb.append(String.format("Pronostico para %s los proximos 3 dias: ", municipioPrincipal));
        for (int i = 0; i < Math.min(pronostico.size(), 3); i++) {
            DailyForecast d = pronostico.get(i);
            sb.append(String.format("Dia %d: %.1f°C, %.1fmm lluvia, %.0f%% humedad. ",
                    i + 1,
                    d.getTempMax() != null ? d.getTempMax() : 0.0,
                    d.getLluviaMm() != null ? d.getLluviaMm() : 0.0,
                    d.getHumedadMax() != null ? d.getHumedadMax() : 0.0));
        }
        if (!ranking.isEmpty()) {
            sb.append("Los cultivos mas populares del Cesar actualmente son: ");
            for (int i = 0; i < Math.min(ranking.size(), 3); i++) {
                sb.append(String.format("%s en %s, ",
                        ranking.get(i).getNombre(), ranking.get(i).getMunicipio()));
            }
        }
        sb.append("En maximo 3 oraciones, genera una recomendacion general considerando ");
        sb.append("todos sus cultivos y el contexto regional. ");
        sb.append("Se practico y menciona acciones concretas para los proximos dias.");
        return sb.toString();
    }

    /* ── Llamada a Groq reutilizable ──────────────────────────── */

    private String llamarGroq(String prompt, int maxTokens) {
        Map<?, ?> body = Map.of(
                "model", model,
                "max_tokens", maxTokens,
                "messages", List.of(
                        Map.of("role", "user", "content", prompt)));

        Map<String, Object> respuesta = webClient.post()
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .bodyValue(body)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
                })
                .timeout(Duration.ofSeconds(15))
                .block();

        String texto = extraerTexto(respuesta);
        if (texto != null && texto.length() > MAX_CHARS)
            texto = texto.substring(0, MAX_CHARS);
        return texto;
    }

    /**
     * Genera un analisis inteligente del historial de alertas para el admin.
     * Retorna null si Groq falla — no interrumpe el flujo (RNF05).
     */
    public String generarAnalisisDashboard(long totalAlertas, long alertasActivas,
            String tipoMasFrecuente, String municipioMasAfectado,
            int pctActivas, int dias) {
        try {
            String prompt = String.format(
                    "Eres un analista agricola experto en el departamento del Cesar, Colombia. " +
                            "En los ultimos %d dias el sistema registro %d alertas climaticas en cultivos de agricultores. "
                            +
                            "De esas, %d estan activas sin revisar (%d%% del total). " +
                            "El tipo de alerta mas frecuente fue: %s. " +
                            "El municipio con mas alertas fue: %s. " +
                            "Genera exactamente dos partes separadas por el caracter |: " +
                            "PARTE 1 (analisis): 2 oraciones que describan la situacion actual con los datos concretos. "
                            +
                            "PARTE 2 (recomendaciones): exactamente 3 acciones concretas separadas por punto y coma. " +
                            "Usa lenguaje directo y simple. No uses markdown ni asteriscos.",
                    dias, totalAlertas, alertasActivas, pctActivas,
                    tipoMasFrecuente, municipioMasAfectado);
            return llamarGroq(prompt, 300);
        } catch (Exception e) {
            log.warn("Groq no disponible para analisis dashboard: {}", e.getMessage());
            return null;
        }
    }
}