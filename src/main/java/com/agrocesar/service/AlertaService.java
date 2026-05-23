package com.agrocesar.service;

import com.agrocesar.dto.AlertaVistaDTO;
import com.agrocesar.model.Alerta;
import com.agrocesar.repository.AlertaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AlertaService {

    private final AlertaRepository alertaRepository;

    public AlertaService(AlertaRepository alertaRepository) {
        this.alertaRepository = alertaRepository;
    }

    public void insertar(Alerta alerta) {
        alertaRepository.insert(alerta);
    }

    public boolean actualizarRecomendacion(Long id, String recomendacion) {
        return alertaRepository.actualizarRecomendacion(id, recomendacion) > 0;
    }

    public boolean marcarLeida(Long id) {
        return alertaRepository.marcarLeida(id) > 0;
    }

    public Optional<AlertaVistaDTO> findById(Long id) {
        return alertaRepository.findById(id);
    }

    public List<AlertaVistaDTO> findByUsuarioId(Long usuarioId) {
        return alertaRepository.findByUsuarioId(usuarioId);
    }

    public List<AlertaVistaDTO> findNoLeidasByUsuarioId(Long usuarioId) {
        return alertaRepository.findNoLeidasByUsuarioId(usuarioId);
    }

    public List<AlertaVistaDTO> findByMunicipioId(Long municipioId) {
        return alertaRepository.findByMunicipioId(municipioId);
    }

    public List<AlertaVistaDTO> findByCatalogoId(Long catalogoId) {
        return alertaRepository.findByCatalogoId(catalogoId);
    }

    public List<AlertaVistaDTO> findByCultivoId(Long cultivoId) {
        return alertaRepository.findByCultivoId(cultivoId);
    }

    public List<AlertaVistaDTO> findByUsuarioIdAndCultivoId(Long usuarioId, Long cultivoId) {
        return alertaRepository.findByUsuarioIdAndCultivoId(usuarioId, cultivoId);
    }

    public List<AlertaVistaDTO> findByTipo(String tipoAlerta) {
        return alertaRepository.findByTipo(tipoAlerta);
    }

    public List<AlertaVistaDTO> findByUsuarioIdAndTipo(Long usuarioId, String tipoAlerta) {
        return alertaRepository.findByUsuarioIdAndTipo(usuarioId, tipoAlerta);
    }

    public List<AlertaVistaDTO> findBySeveridad(String severidad) {
        return alertaRepository.findBySeveridad(severidad);
    }

    public List<AlertaVistaDTO> findByUsuarioIdAndSeveridad(Long usuarioId, String severidad) {
        return alertaRepository.findByUsuarioIdAndSeveridad(usuarioId, severidad);
    }

    public List<AlertaVistaDTO> findAll() {
        return alertaRepository.findAll();
    }
}