package com.agrocesar.controller;

import com.agrocesar.dto.DailyForecast;
import com.agrocesar.model.Municipio;
import com.agrocesar.repository.MunicipioRepository;
import com.agrocesar.service.WeatherService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clima")
public class WeatherController {

    private final WeatherService weatherService;
    private final MunicipioRepository municipioRepository;

    public WeatherController(WeatherService weatherService,
                             MunicipioRepository municipioRepository) {
        this.weatherService = weatherService;
        this.municipioRepository = municipioRepository;
    }


    // Retorna el pronóstico de 7 días para un municipio dado su ID.

    @GetMapping("/pronostico/{municipioId}")
    public ResponseEntity<List<DailyForecast>> pronostico(@PathVariable Long municipioId) {

        Municipio municipio = municipioRepository.findById(municipioId)
                .orElse(null);

        if (municipio == null) {
            return ResponseEntity.notFound().build();
        }

        List<DailyForecast> forecast = weatherService
                .obtenerPronostico7Dias(municipio.getLatitud(), municipio.getLongitud());

        if (forecast.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(forecast);
    }
}

