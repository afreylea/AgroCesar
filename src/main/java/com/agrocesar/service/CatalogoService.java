package com.agrocesar.service;

import com.agrocesar.model.CultivoCatalogo;
import com.agrocesar.repository.CatalogoRepository;
import org.jdbi.v3.core.Jdbi;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CatalogoService {

    private final Jdbi jdbi;

    public CatalogoService(Jdbi jdbi) {
        this.jdbi = jdbi;
    }

    public List<CultivoCatalogo> listarTodos() {
        return jdbi.withExtension(CatalogoRepository.class,
                CatalogoRepository::findAll);
    }

    public List<CultivoCatalogo> listarActivos() {
        return jdbi.withExtension(CatalogoRepository.class,
                CatalogoRepository::findAllActivos);
    }

    public Optional<CultivoCatalogo> buscarPorId(Long id) {
        return jdbi.withExtension(CatalogoRepository.class,
                repo -> repo.findById(id));
    }

    public void crear(CultivoCatalogo catalogo) {
        jdbi.useExtension(CatalogoRepository.class, repo -> {
            Long id = repo.nextId();
            catalogo.setId(id);
            repo.insert(catalogo);
        });
    }

    public boolean actualizar(CultivoCatalogo catalogo) {
        int filas = jdbi.withExtension(CatalogoRepository.class,
                repo -> repo.update(catalogo));
        return filas > 0;
    }

    public boolean desactivar(Long id) {
        int filas = jdbi.withExtension(CatalogoRepository.class,
                repo -> repo.desactivar(id));
        return filas > 0;
    }

    public boolean activar(Long id) {
        int filas = jdbi.withExtension(CatalogoRepository.class,
                repo -> repo.activar(id));
        return filas > 0;
    }
}
