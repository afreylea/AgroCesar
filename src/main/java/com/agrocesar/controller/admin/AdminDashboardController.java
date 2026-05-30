package com.agrocesar.controller.admin;

import com.agrocesar.dto.AlertaVistaDTO;
import com.agrocesar.service.AlertaService;
import com.agrocesar.service.UsuarioService;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.time.LocalDate;

import java.util.List;

/**
 * Controlador del dashboard de administracion.
 *
 * Expone el endpoint GET /admin/dashboard y construye el modelo
 * con estadisticas globales del sistema e historial de alertas.
 *
 * Solo accesible para usuarios con rol ADMIN (Spring Security).
 */
@Controller
public class AdminDashboardController {

    private final AlertaService alertaService;
    private final UsuarioService usuarioService;

    /**
     * Constructor con inyeccion por constructor.
     *
     * @param alertaService  servicio de alertas del sistema
     * @param usuarioService servicio de usuarios y agricultores
     */
    public AdminDashboardController(AlertaService alertaService,
            UsuarioService usuarioService) {
        this.alertaService = alertaService;
        this.usuarioService = usuarioService;
    }

    /**
     * Renderiza el dashboard de administracion.
     *
     * Calcula en memoria las estadisticas a partir del historial completo
     * de alertas para evitar multiples queries a la base de datos.
     *
     * Atributos que se agregan al modelo:
     * historialAlertas - lista de AlertaAdminView con los datos de la tabla
     * totalAlertas - numero total de alertas en el historial
     * alertasActivas - alertas no leidas (proxy de activas)
     * alertasTransitorio - alertas de categoria TRANSITORIO
     * alertasPermanente - alertas de categoria PERMANENTE
     * agricultoresActivos - usuarios activos con rol AGRICULTOR
     *
     * @param model modelo de Spring MVC para pasar datos a la vista
     * @return nombre de la plantilla Thymeleaf: admin/dashboard
     */
    @GetMapping("/admin/dashboard")
    public String dashboard(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaDesde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaHasta,
            Model model) {

        if (fechaDesde == null)
            fechaDesde = LocalDate.now().minusDays(7);
        if (fechaHasta == null)
            fechaHasta = LocalDate.now();

        List<AlertaVistaDTO> historial = alertaService.findAll();

        /* Filtro por fechas en memoria */
        final LocalDate desde = fechaDesde;
        final LocalDate hasta = fechaHasta;
        List<AlertaVistaDTO> historialFiltrado = historial.stream()
                .filter(a -> {
                    if (a.getFechaGeneracion() == null)
                        return false;
                    LocalDate fecha = a.getFechaGeneracion().toLocalDate();
                    return !fecha.isBefore(desde) && !fecha.isAfter(hasta);
                })
                .toList();

        long totalAlertas = historialFiltrado.size();
        long alertasActivas = historialFiltrado.stream().filter(a -> !a.isLeida()).count();
        long alertasTransitorio = historialFiltrado.stream().filter(a -> "TRANSITORIO".equals(a.getCategoria()))
                .count();
        long alertasPermanente = historialFiltrado.stream().filter(a -> "PERMANENTE".equals(a.getCategoria())).count();
        long agricultoresActivos = usuarioService.listarActivos().stream()
                .filter(u -> "AGRICULTOR".equals(u.getRol())).count();

        List<AlertaAdminView> historialView = historialFiltrado.stream().map(a -> new AlertaAdminView(
                a.getAgricultor(),
                a.getCultivo(),
                a.getCategoria(),
                a.getDescripcion(),
                a.getSeveridad(),
                a.getTipoAlerta(),
                a.isLeida(),
                a.getFechaGeneracion() != null ? a.getFechaGeneracion().toLocalDate().atStartOfDay() : null))
                .toList();

        model.addAttribute("historialAlertas", historialView);
        model.addAttribute("totalAlertas", totalAlertas);
        model.addAttribute("alertasActivas", alertasActivas);
        model.addAttribute("alertasTransitorio", alertasTransitorio);
        model.addAttribute("alertasPermanente", alertasPermanente);
        model.addAttribute("agricultoresActivos", agricultoresActivos);
        model.addAttribute("fechaDesde", fechaDesde);
        model.addAttribute("fechaHasta", fechaHasta);

        return "admin/dashboard";
    }

    /**
     * DTO interno que adapta AlertaVistaDTO a los nombres
     * que espera la plantilla Thymeleaf admin/dashboard.html.
     *
     * Evita modificar AlertaVistaDTO (dominio de Dev 1) para
     * satisfacer un requerimiento de presentacion de Dev 3.
     */
    public static class AlertaAdminView {

        private final String nombreAgricultor;
        private final String nombreCultivo;
        private final String categoria;
        private final String descripcion;
        private final String severidad;
        private final String tipoAlerta;
        private final boolean leida;
        private final java.time.LocalDateTime fechaCreacion;

        public AlertaAdminView(String nombreAgricultor, String nombreCultivo,
                String categoria, String descripcion,
                String severidad, String tipoAlerta, boolean leida,
                java.time.LocalDateTime fechaCreacion) {
            this.nombreAgricultor = nombreAgricultor;
            this.nombreCultivo = nombreCultivo;
            this.categoria = categoria;
            this.descripcion = descripcion;
            this.severidad = severidad;
            this.tipoAlerta = tipoAlerta;
            this.leida = leida;
            this.fechaCreacion = fechaCreacion;
        }

        public String getNombreAgricultor() {
            return nombreAgricultor;
        }

        public String getNombreCultivo() {
            return nombreCultivo;
        }

        public String getCategoria() {
            return categoria;
        }

        public String getDescripcion() {
            return descripcion;
        }

        public String getSeveridad() {
            return severidad;
        }

        public String getTipoAlerta() {
            return tipoAlerta;
        }

        public boolean isLeida() {
            return leida;
        }

        public java.time.LocalDateTime getFechaCreacion() {
            return fechaCreacion;
        }
    }
}