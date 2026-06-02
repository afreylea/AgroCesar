package com.agrocesar.service;

import com.agrocesar.model.Municipio;
import com.agrocesar.repository.MunicipioRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class MunicipioService {

    private final MunicipioRepository municipioRepository;

    public MunicipioService(MunicipioRepository municipioRepository) {
        this.municipioRepository = municipioRepository;
    }

    public List<Municipio> findAll() {
        return municipioRepository.findAll();
    }

    public List<Municipio> findActivos() {
        return municipioRepository.findActivos();
    }

    public List<Municipio> findInactivos() {
        return municipioRepository.findInactivos();
    }

    public Optional<Municipio> findById(Long id) {
        return municipioRepository.findById(id);
    }

    public List<Municipio> findByNombre(String nombre) {
        return municipioRepository.findByNombre(nombre.toUpperCase() + "%");
    }

    public List<Municipio> findByDepartamento(String departamento) {
        return municipioRepository.findByDepartamento(departamento);
    }

    public Municipio insertar(String nombre,
                              String departamento,
                              Double latitud,
                              Double longitud) {

        List<Municipio> municipioExiste = municipioRepository.findByNombre(nombre.toUpperCase());

        if (!municipioExiste.isEmpty()) {
            throw new IllegalArgumentException("Este municipio ya está registrado en la base de datos");
        }

        validarCoordenadas(latitud, longitud);

        Municipio municipio = Municipio.builder()
            .nombre(nombre.trim())
            .departamento(departamento.trim())
            .latitud(latitud)
            .longitud(longitud)
            .activo(1)
            .fechaCreacion(LocalDate.now())
            .build();

        municipioRepository.insert(municipio);

        return municipio;
    }

    public boolean actualizar(Long id,
                              String nombre,
                              String departamento,
                              Double latitud,
                              Double longitud,
                              Integer activo) {

        Optional<Municipio> municipioExistente = municipioRepository.findById(id);

        if (municipioExistente.isEmpty()) {
            throw new IllegalArgumentException("Municipio inexistente");
        }

        validarCoordenadas(latitud, longitud);

        Municipio municipio = municipioExistente.get();

        municipio.setNombre(nombre.trim());
        municipio.setDepartamento(departamento.trim());
        municipio.setLatitud(latitud);
        municipio.setLongitud(longitud);
        municipio.setActivo(activo);

        return municipioRepository.update(municipio) > 0;
    }

    public boolean desactivar(Long id) {
        return municipioRepository.desactivar(id) > 0;
    }

    public boolean activar(Long id) {
        return municipioRepository.activar(id) > 0;
    }

    private void validarCoordenadas(double latitud, double longitud) {

        if (latitud < -90 || latitud > 90) {
            throw new IllegalArgumentException("Latitud fuera de rango, debe estar entre 0 y 90");
        }

        if (longitud < -180 || longitud > 180) {
            throw new IllegalArgumentException("Longitud fuera de rango, debe estar entre 0 y 180");
        }
    }
}