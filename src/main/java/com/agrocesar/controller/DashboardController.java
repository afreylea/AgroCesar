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
import com.agrocesar.dto.RankingCultivoDTO;
import com.agrocesar.repository.CultivoConUmbralesRepository;
import com.agrocesar.service.RecomendacionService;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Controlador principal del agricultor.
 *
 * <p>
 * Responsabilidades:
 * <ul>
 * <li>Renderizar el dashboard con pronostico climatico inicial y lista de
 * cultivos.</li>
 * <li>Exponer el endpoint REST {@code GET /api/pronostico/{municipioId}} usado
 * por
 * Alpine.js en el frontend para actualizar el pronostico al seleccionar un
 * cultivo.</li>
 * </ul>
 *
 * <p>
 * Patron aplicado: MVC — este controller actua como intermediario entre los
 * servicios
 * de dominio (WeatherService, CultivoAgricultorService) y las vistas Thymeleaf.
 */
@Controller
public class DashboardController {

    private final WeatherService weatherService;
    private final MunicipioRepository municipioRepository;
    private final UsuarioService usuarioService;
    private final CultivoAgricultorService cultivoService;
    private final CatalogoRepository catalogoRepository;
    private final CultivoConUmbralesRepository cultivoConUmbralesRepository;
    private final RecomendacionService recomendacionService;

    public DashboardController(WeatherService weatherService,
            MunicipioRepository municipioRepository,
            UsuarioService usuarioService,
            CultivoAgricultorService cultivoService,
            CatalogoRepository catalogoRepository,
            CultivoConUmbralesRepository cultivoConUmbralesRepository, RecomendacionService recomendacionService) {
        this.weatherService = weatherService;
        this.municipioRepository = municipioRepository;
        this.usuarioService = usuarioService;
        this.cultivoService = cultivoService;
        this.catalogoRepository = catalogoRepository;
        this.cultivoConUmbralesRepository = cultivoConUmbralesRepository;
        this.recomendacionService = recomendacionService;
    }

    /**
     * Endpoint REST consumido por Alpine.js via fetch cuando el agricultor
     * selecciona un cultivo diferente en el dashboard.
     *
     * <p>
     * Retorna 404 si el municipio no existe en BD.
     * Retorna 204 si Open-Meteo no devuelve datos (error de red o timeout).
     * Retorna 200 con la lista de 7 dias si todo va bien.
     *
     * @param municipioId ID del municipio cuyas coordenadas se usaran para la
     *                    consulta
     * @return lista de {@link DailyForecast} con el pronostico de 7 dias
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
     * Renderiza el dashboard principal del agricultor.
     *
     * <p>
     * Flujo:
     * <ol>
     * <li>Carga los cultivos del agricultor autenticado.</li>
     * <li>Construye {@link CultivoResumen} combinando datos de catalogo, municipio
     * e imagen. Las coordenadas priorizan las del cultivo; si son null, usan
     * las del municipio como fallback.</li>
     * <li>Carga el pronostico inicial del primer cultivo para que la vista tenga
     * datos desde el primer render, antes de cualquier interaccion Alpine.</li>
     * </ol>
     *
     * <p>
     * Atributos enviados al modelo:
     * <ul>
     * <li>{@code cultivos} — lista de {@link CultivoResumen} del agricultor</li>
     * <li>{@code pronostico} — lista de {@link DailyForecast} del primer
     * cultivo</li>
     * <li>{@code municipio} — nombre del municipio del primer cultivo</li>
     * <li>{@code latInicial} / {@code lngInicial} — coordenadas para inicializar
     * Leaflet</li>
     * <li>{@code usuario} — objeto {@link Usuario} del agricultor autenticado</li>
     * </ul>
     *
     * @param userDetails usuario autenticado inyectado por Spring Security
     * @param model       modelo Thymeleaf
     * @return nombre de la vista {@code dashboard}
     */
    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        Usuario usuario = usuarioService.buscarPorEmail(userDetails.getUsername());
        List<CultivoAgricultor> cultivos = cultivoService.listarPorUsuario(usuario.getId());

        List<CultivoResumen> cultivosView = cultivos.stream().map(c -> {
            var cat = catalogoRepository.findById(c.getCatalogoId());
            String nombreCultivo = cat.map(x -> x.getNombre()).orElse("Sin nombre");
            String categoria = cat.map(x -> x.getCategoria()).orElse("");
            String imagenUrl = cat.map(x -> x.getImagenUrl()).orElse(null);

            // Municipio consultado una sola vez para nombre y coordenadas
            Optional<Municipio> mun = municipioRepository.findById(c.getMunicipioId());
            String municipioNombre = mun.map(Municipio::getNombre).orElse("Sin municipio");

            // Coordenadas: prioriza las del cultivo, cae al municipio si son null
            Double lat = c.getLatitudCultivo() != null
                    ? c.getLatitudCultivo()
                    : mun.map(Municipio::getLatitud).orElse(null);
            Double lng = c.getLongitudCultivo() != null
                    ? c.getLongitudCultivo()
                    : mun.map(Municipio::getLongitud).orElse(null);

            return new CultivoResumen(c.getId(), nombreCultivo, categoria,
                    municipioNombre, c.getHectareas(), c.getFechaSiembra(),
                    c.getMunicipioId(), lat, lng, imagenUrl);
        }).toList();

        // Pronostico inicial: se carga desde el primer cultivo para evitar
        // que la vista quede vacia en el primer render antes de interaccion Alpine
        if (!cultivosView.isEmpty()) {
            CultivoResumen primero = cultivosView.get(0);
            if (primero.getMunicipioId() != null) {
                Optional<Municipio> mun = municipioRepository.findById(primero.getMunicipioId());
                mun.ifPresent(m -> {
                    List<DailyForecast> pronostico = weatherService.obtenerPronostico7Dias(
                            m.getLatitud(), m.getLongitud());
                    // Limita a 7 dias para el dashboard — el metodo ahora devuelve 16
                    List<DailyForecast> pronostico7 = pronostico.size() > 7
                            ? pronostico.subList(0, 7)
                            : pronostico;
                    model.addAttribute("pronostico", pronostico7);
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

    @GetMapping(value = "/dashboard/ranking-cultivos", produces = "application/json")
    @ResponseBody
    public ResponseEntity<List<RankingCultivoDTO>> getRankingCultivos() {
        List<RankingCultivoDTO> ranking = cultivoConUmbralesRepository.findRankingCultivos();
        return ranking.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(ranking);
    }

    /**
     * Recomendacion especifica para el cultivo activo del dashboard.
     * Llamado por Alpine.js cuando el agricultor selecciona un cultivo.
     */
    @GetMapping(value = "/api/recomendacion/cultivo", produces = "application/json")
    @ResponseBody
    public ResponseEntity<Map<String, String>> getRecomendacionCultivo(
            @RequestParam String cultivoNombre,
            @RequestParam Long municipioId,
            @RequestParam double hectareas,
            @RequestParam String fechaSiembra) {

        Optional<Municipio> municipio = municipioRepository.findById(municipioId);
        if (municipio.isEmpty())
            return ResponseEntity.notFound().build();

        List<DailyForecast> pronostico = weatherService.obtenerPronostico7Dias(
                municipio.get().getLatitud(), municipio.get().getLongitud());

        String texto = recomendacionService.generarRecomendacionCultivo(
                cultivoNombre, municipio.get().getNombre(),
                hectareas, fechaSiembra, pronostico);

        if (texto == null)
            return ResponseEntity.noContent().build();
        return ResponseEntity.ok(Map.of("recomendacion", texto));
    }

    /**
     * Recomendacion general para todos los cultivos del agricultor autenticado.
     * Llamado una sola vez al cargar el dashboard.
     */
    @GetMapping(value = "/api/recomendacion/general", produces = "application/json")
    @ResponseBody
    public ResponseEntity<Map<String, String>> getRecomendacionGeneral(
            @AuthenticationPrincipal UserDetails userDetails) {

        Usuario usuario = usuarioService.buscarPorEmail(userDetails.getUsername());
        List<CultivoAgricultor> cultivosRaw = cultivoService.listarPorUsuario(usuario.getId());

        /* Construye CultivoResumen igual que en dashboard() */
        List<CultivoResumen> cultivos = cultivosRaw.stream().map(c -> {
            var cat = catalogoRepository.findById(c.getCatalogoId());
            Optional<Municipio> mun = municipioRepository.findById(c.getMunicipioId());
            return new CultivoResumen(
                    c.getId(),
                    cat.map(x -> x.getNombre()).orElse("Sin nombre"),
                    cat.map(x -> x.getCategoria()).orElse(""),
                    mun.map(Municipio::getNombre).orElse("Sin municipio"),
                    c.getHectareas(), c.getFechaSiembra(),
                    c.getMunicipioId(),
                    mun.map(Municipio::getLatitud).orElse(null),
                    mun.map(Municipio::getLongitud).orElse(null),
                    cat.map(x -> x.getImagenUrl()).orElse(null));
        }).toList();

        if (cultivos.isEmpty())
            return ResponseEntity.noContent().build();

        /* Pronostico del municipio del primer cultivo */
        Long municipioId = cultivos.get(0).getMunicipioId();
        Optional<Municipio> mun = municipioRepository.findById(municipioId);
        if (mun.isEmpty())
            return ResponseEntity.noContent().build();

        List<DailyForecast> pronostico = weatherService.obtenerPronostico7Dias(
                mun.get().getLatitud(), mun.get().getLongitud());

        List<RankingCultivoDTO> ranking = cultivoConUmbralesRepository.findRankingCultivos();

        String texto = recomendacionService.generarRecomendacionGeneral(
                cultivos, mun.get().getNombre(), pronostico, ranking);

        if (texto == null)
            return ResponseEntity.noContent().build();
        return ResponseEntity.ok(Map.of("recomendacion", texto));
    }
}