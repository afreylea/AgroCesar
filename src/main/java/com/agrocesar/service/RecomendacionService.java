package com.agrocesar.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@Service
public class RecomendacionService {

    private static final Logger log = LoggerFactory.getLogger(RecomendacionService.class);
    private static final int MAX_CHARS = 1000;

    private final WebClient webClient;

    @Value("${grop.api.key}")
    private String apiKey;

    @Value("${grop.api.model}")
    private String model;

    public RecomendacionService(@Value("${grop.api.url}") String apiUrl) {
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
                    Map.of("role", "user", "content", prompt)
                )
            );

            Map<String, Object> respuesta = webClient.post()
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .bodyValue(body)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .timeout(Duration.ofSeconds(10))
                .block();

            String texto = extraerTexto(respuesta);

            if (texto != null && texto.length() > MAX_CHARS)
                texto = texto.substring(0, MAX_CHARS);
            return texto;

        } catch (Exception e) {
            log.warn("Grop no disponible para alerta {}/{}: {}", tipoAlerta, cultivo, e.getMessage());
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
            tipoAlerta, severidad, cultivo, valorDetectado, pctCiclo, diasRestantes
        );
    }

    private String extraerTexto(Map<String, Object> respuesta) {
        if (respuesta == null) return null;
        var choices = (List<?>) respuesta.get("choices");
        if (choices == null || choices.isEmpty()) return null;
        var message = (Map<?, ?>) ((Map<?, ?>) choices.get(0)).get("message");
        if (message == null) return null;
        return (String) message.get("content");
    }
}