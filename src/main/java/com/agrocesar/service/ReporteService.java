package com.agrocesar.service;

import com.agrocesar.dto.AlertaVistaDTO;
import com.agrocesar.dto.CultivoMasAfectadoDTO;
import com.agrocesar.repository.AlertaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ReporteService {

    private final AlertaRepository alertaRepository;

    public ReporteService(AlertaRepository alertaRepository) {
        this.alertaRepository = alertaRepository;
    }

    public List<AlertaVistaDTO> alertasPorPeriodo(LocalDate fechaDesde, LocalDate fechaHasta) {
        return alertaRepository.findByRangoFechas(fechaDesde, fechaHasta);
    }

    public List<CultivoMasAfectadoDTO> cultivosMasAfectados(LocalDate fechaDesde, LocalDate fechaHasta) {
        return alertaRepository.findCultivosMasAfectados(fechaDesde, fechaHasta);
    }

    public int totalAlertasActivas() {
        return alertaRepository.countActivas();
    }

    public int totalAlertasCriticas() {
        return alertaRepository.countCriticas();
    }
}