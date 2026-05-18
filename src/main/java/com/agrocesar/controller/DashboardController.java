package com.agrocesar.controller;

import com.agrocesar.dto.DailyForecast;
import com.agrocesar.dto.RankingCultivoDTO;
import com.agrocesar.model.Municipio;
import com.agrocesar.repository.MunicipioRepository;
import com.agrocesar.service.WeatherService;
import com.agrocesar.service.CultivoAgricultorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/pronostico")
public class DashboardController {

    private final WeatherService weatherService;
    private final MunicipioRepository municipioRepository;
    private final CultivoAgricultorService cultivoService;

    public DashboardController(WeatherService weatherService,
                               MunicipioRepository municipioRepository,
                               CultivoAgricultorService cultivoService) {
        this.weatherService = weatherService;
        this.municipioRepository = municipioRepository;
        this.cultivoService = cultivoService;
    }

    /**
     * Endpoint interno: retorna pronóstico 7 días para un municipio.
     * Usado por Dev 3 en el dashboard y por AlertaScheduler.
     *
     * GET /api/pronostico/{municipioId}
     */
    @GetMapping("/{municipioId}")
    public ResponseEntity<List<DailyForecast>> getPronostico(
            @PathVariable Long municipioId) {

        Optional<Municipio> municipio = municipioRepository.findById(municipioId);

        if (municipio.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        List<DailyForecast> pronostico = weatherService.obtenerPronostico7Dias(
                municipio.get().getLatitud(),
                municipio.get().getLongitud()
        );

        if (pronostico.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(pronostico);
    }

    @GetMapping("/dashboard/ranking-cultivos")
    @ResponseBody
    public List<RankingCultivoDTO> rankingCultivos() {
        return cultivoService.obtenerRanking();
    }
}