package com.agrocesar.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.agrocesar.model.Usuario;
import com.agrocesar.repository.CatalogoRepository;
import com.agrocesar.repository.MunicipioRepository;
import com.agrocesar.service.CultivoAgricultorService;
import com.agrocesar.service.UsuarioService;

import ch.qos.logback.core.model.Model;



@Controller
@RequestMapping("/cultivos")

public class CultivoController {
    private final CultivoAgricultorService CultivoAgricultorService;
    private final UsuarioService usuarioService;
    private final CatalogoRepository catalogoRepository;
    private final MunicipioRepository municipioRepository;

    public CultivoController(CultivoAgricultorService cultivoService,
            UsuarioService usuarioService,
            CatalogoRepository catalogoRepository,
            MunicipioRepository municipioRepository) {
        this.CultivoAgricultorService = cultivoService;
        this.usuarioService = usuarioService;
        this.catalogoRepository = catalogoRepository;
        this.municipioRepository = municipioRepository;
    }

    
}
