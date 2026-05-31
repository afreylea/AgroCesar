package com.agrocesar.service;

import com.agrocesar.model.CultivoCatalogo;
import com.agrocesar.repository.CatalogoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CatalogoService {

    private final CatalogoRepository catalogoRepository;

    public CatalogoService(CatalogoRepository catalogoRepository) {
        this.catalogoRepository = catalogoRepository;
    }

    public List<CultivoCatalogo> listarTodos() {
        return catalogoRepository.findAll();
    }

    public List<CultivoCatalogo> listarActivos() {
        return catalogoRepository.findAllActivos();
    }

    public Optional<CultivoCatalogo> buscarPorId(Long id) {
        return catalogoRepository.findById(id);
    }

    public void crear(CultivoCatalogo catalogo) {
        catalogoRepository.insert(catalogo);
    }

    public boolean actualizar(CultivoCatalogo catalogo) {
        int filas = catalogoRepository.update(catalogo);
        return filas > 0;
    }

    public boolean desactivar(Long id) {
        // TODO Sprint 3: antes del UPDATE, consultar countActivosByCatalogoId(id)
        // y notificar por SMS a los agricultores afectados via SmsService.
        // El trigger TRG_CATALOGO_DESACTIVAR_CASCADE en BD ya maneja la cascada.
        int filas = catalogoRepository.desactivar(id);
        return filas > 0;
    }

    public boolean activar(Long id) {
        int filas = catalogoRepository.activar(id);
        return filas > 0;
    }
}
