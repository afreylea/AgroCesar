package com.agrocesar.controller;

import com.agrocesar.dto.DailyForecast;
import com.agrocesar.model.CultivoAgricultor;
import com.agrocesar.model.Municipio;
import com.agrocesar.model.Usuario;
import com.agrocesar.service.CultivoAgricultorService;
import com.agrocesar.service.UsuarioService;
import com.agrocesar.repository.MunicipioRepository;
import com.agrocesar.service.WeatherService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller 
public class DashboardController {

    private final WeatherService weatherService;
    private final MunicipioRepository municipioRepository;
    private final UsuarioService usuarioService;
    private final CultivoAgricultorService cultivoService;

    public DashboardController(WeatherService weatherService,
            MunicipioRepository municipioRepository,
            UsuarioService usuarioService,
            CultivoAgricultorService cultivoService) {
        this.weatherService = weatherService;
        this.municipioRepository = municipioRepository;
        this.usuarioService = usuarioService;
        this.cultivoService = cultivoService;
    }

    /**
     * Endpoint API REST para pronóstico (usado por scheduler y AJAX)
     */
    @GetMapping("/api/pronostico/{municipioId}")
    @ResponseBody
    public ResponseEntity<List<DailyForecast>> getPronostico(@PathVariable Long municipioId) {
        Optional<Municipio> municipio = municipioRepository.findById(municipioId);

        if (municipio.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        List<DailyForecast> pronostico = weatherService.obtenerPronostico7Dias(
                municipio.get().getLatitud(),
                municipio.get().getLongitud());

        return pronostico.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(pronostico);
    }

    /**
     * Vista dashboard para agricultor
     */
    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        Usuario usuario = usuarioService.buscarPorEmail(userDetails.getUsername());
        List<CultivoAgricultor> cultivos = cultivoService.listarPorUsuario(usuario.getId());

        if (!cultivos.isEmpty()) {
            Optional<Municipio> municipio = municipioRepository.findById(cultivos.get(0).getMunicipioId());
            if (municipio.isPresent()) {
                List<DailyForecast> pronostico = weatherService.obtenerPronostico7Dias(
                        municipio.get().getLatitud(),
                        municipio.get().getLongitud());
                model.addAttribute("pronostico", pronostico);
                model.addAttribute("municipio", municipio.get().getNombre());
            }
        }

        model.addAttribute("cultivos", cultivos);
        model.addAttribute("usuario", usuario);
        return "dashboard";
    }
}