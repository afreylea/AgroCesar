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
     * @param apiUrl       URL base de la API de Groq
     * @param weatherService servicio de pronostico Open-Meteo
     */
    public SiembraOptimaService(@Value("${groq.api.url}") String apiUrl,
                                 WeatherService weatherService) {
        this.webClient      = WebClient.builder().baseUrl(apiUrl).build();
        this.weatherService = weatherService;
    }
}