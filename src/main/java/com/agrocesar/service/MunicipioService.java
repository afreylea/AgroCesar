package com.agrocesar.service;

import com.agrocesar.model.Municipio;
import com.agrocesar.repository.MunicipioRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class MunicipioService {
    private final MunicipioRepository municipioRepository;

    public MunicipioService (MunicipioRepository municipioRepository) {
        this.municipioRepository = municipioRepository;
    }

    public List<Municipio> findAllActivos () {
        return municipioRepository.findAllActivos();
    }

    public Optional<Municipio> findById(Long id) {
        return municipioRepository.findById(id);
    }

    /**
     * Persiste un municipio nuevo.
     * Obtiene el NEXTVAL de la secuencia Oracle antes del INSERT para evitar
     * el problema de @GetGeneratedKeys con triggers BEFORE INSERT.
     * Retorna el municipio con el ID asignado.
     */
    public Municipio insertar(String nombre, String departamento,
                              Double latitud, Double longitud) {
        Long id = municipioRepository.nextId();

        Municipio municipio = Municipio.builder()
            .id(id)
            .nombre(nombre.trim())
            .departamento(departamento != null ? departamento.trim() : "Cesar")
            .latitud(latitud)
            .longitud(longitud)
            .build();

        municipioRepository.insert(municipio);
        return municipio;
    }
}