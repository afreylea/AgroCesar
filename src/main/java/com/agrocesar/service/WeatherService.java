package com.agrocesar.service;

import com.agrocesar.dto.DailyForecast;
import com.agrocesar.dto.ForecastResponse;
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

    private final WebClient openMeteoWebClient;
    private final int timeoutSeconds;

    public WeatherService(
            WebClient openMeteoWebClient,
            @Value("${webclient.open-meteo.timeout-seconds}") int timeoutSeconds) {
        this.openMeteoWebClient = openMeteoWebClient;
        this.timeoutSeconds = timeoutSeconds;
    }

    /**
     * Retorna el pronóstico de los próximos 7 días para las coordenadas dadas.
     * Llamado por DashboardController y por AlertaScheduler.
     *
     * @param latitud  latitud del municipio (de la tabla MUNICIPIOS)
     * @param longitud longitud del municipio (de la tabla MUNICIPIOS)
     * @return lista de 7 DailyForecast ordenados por fecha ascendente,
     *         o lista vacía si la API no responde.
     */
    public List<DailyForecast> obtenerPronostico7Dias(double latitud, double longitud) {
        try {
            ForecastResponse response = openMeteoWebClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/forecast")
                            .queryParam("latitude", latitud)
                            .queryParam("longitude", longitud)
                            .queryParam("daily",
                                    "temperature_2m_max",
                                    "temperature_2m_min",
                                    "precipitation_sum",
                                    "relative_humidity_2m_max",
                                    "relative_humidity_2m_min",
                                    "weathercode")
                            .queryParam("forecast_days", 7)
                            .queryParam("timezone", "America/Bogota")
                            .build())
                    .retrieve()
                    .bodyToMono(ForecastResponse.class)
                    .timeout(Duration.ofSeconds(timeoutSeconds))
                    .onErrorResume(WebClientResponseException.class, ex -> {
                        // API respondió con 4xx/5xx — retornar vacío
                        return Mono.empty();
                    })
                    .block();

            if (response == null || response.getDaily() == null) {
                return Collections.emptyList();
            }

            return mapearDias(response.getDaily());

        } catch (Exception ex) {
            // Timeout u otro error de red — el scheduler y el controller manejan la lista vacía
            return Collections.emptyList();
        }
    }

    // ── Mapeo interno ────────────────────────────────────────────────────────

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

    /** Devuelve 0.0 si la lista es null, está vacía o el índice no existe. */
    private double valorSeguro(List<Double> lista, int i) {
        if (lista == null || i >= lista.size() || lista.get(i) == null) return 0.0;
        return lista.get(i);
    }

    /** Devuelve 0 si la lista de códigos es null o el índice no existe. */
    private int weatherCodeSeguro(List<Integer> lista, int i) {
        if (lista == null || i >= lista.size() || lista.get(i) == null) return 0;
        return lista.get(i);
    }
}