package com.agrocesar.controller;

import com.agrocesar.dto.CultivoResumen;
import com.agrocesar.dto.DailyForecast;
import com.agrocesar.model.CultivoAgricultor;
import com.agrocesar.model.Municipio;
import com.agrocesar.model.Usuario;
import com.agrocesar.repository.CatalogoRepository;
import com.agrocesar.repository.MunicipioRepository;
import com.agrocesar.service.CultivoAgricultorService;
import com.agrocesar.service.UsuarioService;
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
    private final CatalogoRepository catalogoRepository;

    public DashboardController(WeatherService weatherService,
            MunicipioRepository municipioRepository,
            UsuarioService usuarioService,
            CultivoAgricultorService cultivoService,
            CatalogoRepository catalogoRepository) {
        this.weatherService = weatherService;
        this.municipioRepository = municipioRepository;
        this.usuarioService = usuarioService;
        this.cultivoService = cultivoService;
        this.catalogoRepository = catalogoRepository;
    }

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

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        Usuario usuario = usuarioService.buscarPorEmail(userDetails.getUsername());
        List<CultivoAgricultor> cultivos = cultivoService.listarPorUsuario(usuario.getId());
        List<CultivoResumen> cultivosView = cultivos.stream().map(c -> {
            var cat = catalogoRepository.findById(c.getCatalogoId());
            String nombreCultivo = cat.map(x -> x.getNombre()).orElse("Sin nombre");
            String categoria = cat.map(x -> x.getCategoria()).orElse("");

            // Busca municipio una sola vez y extrae todo lo necesario
            Optional<Municipio> mun = municipioRepository.findById(c.getMunicipioId());
            String municipioNombre = mun.map(Municipio::getNombre).orElse("Sin municipio");

            // Prioriza coordenadas del cultivo si existen, si no usa las del municipio
            Double lat = c.getLatitudCultivo() != null ? c.getLatitudCultivo()
                    : mun.map(Municipio::getLatitud).orElse(null);
            Double lng = c.getLongitudCultivo() != null ? c.getLongitudCultivo()
                    : mun.map(Municipio::getLongitud).orElse(null);

            return new CultivoResumen(c.getId(), nombreCultivo, categoria,
                    municipioNombre, c.getHectareas(), c.getFechaSiembra(),
                    c.getMunicipioId(), lat, lng);
        }).toList();

        // Pronóstico inicial: primer cultivo con municipio válido
        if (!cultivosView.isEmpty()) {
            CultivoResumen primero = cultivosView.get(0);
            if (primero.getMunicipioId() != null) {
                Optional<Municipio> mun = municipioRepository.findById(primero.getMunicipioId());
                mun.ifPresent(m -> {
                    List<DailyForecast> pronostico = weatherService.obtenerPronostico7Dias(
                            m.getLatitud(), m.getLongitud());
                    model.addAttribute("pronostico", pronostico);
                    model.addAttribute("municipio", m.getNombre());
                    model.addAttribute("latInicial", m.getLatitud());
                    model.addAttribute("lngInicial", m.getLongitud());
                });
            }
        }

        model.addAttribute("cultivos", cultivosView);
        model.addAttribute("usuario", usuario);
        return "dashboard";
    }
}