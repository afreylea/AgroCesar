package com.agrocesar.service;

import com.agrocesar.model.Municipio;
import com.agrocesar.repository.MunicipioRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class MunicipioService {
    private final MunicipioRepository municipioRepository;

    public MunicipioService (MunicipioRepository municipioRepository) {
        this.municipioRepository = municipioRepository;
    }

    public List<Municipio> findAllActivos () {
        return municipioRepository.findAllActivos();
    }
}
