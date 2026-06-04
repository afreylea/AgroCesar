package com.agrocesar.service;

import com.agrocesar.dto.DailyForecast;
import com.agrocesar.dto.ForecastResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class WeatherService {

    private static final Logger log = LoggerFactory.getLogger(WeatherService.class);

    private final WebClient openMeteoWebClient;
    private final int timeoutSeconds;

    public WeatherService(
            WebClient openMeteoWebClient,
            @Value("${webclient.open-meteo.timeout-seconds}") int timeoutSeconds) {
        this.openMeteoWebClient = openMeteoWebClient;
        this.timeoutSeconds = timeoutSeconds;
    }

    /**
     * Retorna el pronostico de los proximos 7 dias para las coordenadas dadas.
     * Llamado por DashboardController y por AlertaScheduler en Sprint 3.
     *
     * Open-Meteo requiere cada variable daily como parametro separado,
     * no como lista separada por comas. Por eso se usa un queryParam por variable.
     *
     * @param latitud  latitud del municipio (tabla MUNICIPIOS)
     * @param longitud longitud del municipio (tabla MUNICIPIOS)
     * @return lista de 7 DailyForecast ordenados por fecha ascendente,
     *         o lista vacia si la API no responde o devuelve error.
     */
    public List<DailyForecast> obtenerPronostico7Dias(double latitud, double longitud) {
        log.info("Llamando Open-Meteo lat={} lng={}", latitud, longitud);

        try {
            ForecastResponse response = openMeteoWebClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/v1/forecast")
                            .queryParam("latitude", latitud)
                            .queryParam("longitude", longitud)
                            // Cada variable daily va como parametro separado
                            // Open-Meteo no acepta lista separada por comas
                            .queryParam("daily", "temperature_2m_max")
                            .queryParam("daily", "temperature_2m_min")
                            .queryParam("daily", "precipitation_sum")
                            .queryParam("daily", "relative_humidity_2m_max")
                            .queryParam("daily", "relative_humidity_2m_min")
                            .queryParam("daily", "weathercode")
                            .queryParam("forecast_days", 16)
                            .queryParam("timezone", "America/Bogota")
                            .build())
                    .retrieve()
                    .bodyToMono(ForecastResponse.class)
                    .timeout(Duration.ofSeconds(timeoutSeconds))
                    .onErrorResume(WebClientResponseException.class, ex -> {
                        // API respondio con 4xx/5xx
                        log.warn("Open-Meteo error HTTP {}: {}", ex.getStatusCode(), ex.getMessage());
                        return Mono.empty();
                    })
                    .block();

            if (response == null || response.getDaily() == null) {
                log.warn("Open-Meteo devolvio respuesta nula para lat={} lng={}", latitud, longitud);
                return Collections.emptyList();
            }

            List<DailyForecast> resultado = mapearDias(response.getDaily());
            log.info("Open-Meteo devolvio {} dias de pronostico", resultado.size());
            return resultado;

        } catch (Exception ex) {
            // Timeout u otro error de red
            log.error("Error al llamar Open-Meteo: {}", ex.getMessage());
            return Collections.emptyList();
        }
    }

    private List<DailyForecast> mapearDias(ForecastResponse.DailyData daily) {
        List<DailyForecast> resultado = new ArrayList<>();
        List<String> fechas = daily.getTime();

        for (int i = 0; i < fechas.size(); i++) {
            resultado.add(DailyForecast.builder()
                    .fecha(fechas.get(i))
                    .tempMax(valorSeguro(daily.getTemperatureMax(), i))
                    .tempMin(valorSeguro(daily.getTemperatureMin(), i))
                    .lluviaMm(valorSeguro(daily.getPrecipitationSum(), i))
                    .humedadMax(valorSeguro(daily.getHumidityMax(), i))
                    .humedadMin(valorSeguro(daily.getHumidityMin(), i))
                    .weatherCode(weatherCodeSeguro(daily.getWeatherCode(), i))
                    .build());
        }

        return resultado;
    }

    private double valorSeguro(List<Double> lista, int i) {
        if (lista == null || i >= lista.size() || lista.get(i) == null)
            return 0.0;
        return lista.get(i);
    }

    private int weatherCodeSeguro(List<Integer> lista, int i) {
        if (lista == null || i >= lista.size() || lista.get(i) == null)
            return 0;
        return lista.get(i);
    }
}