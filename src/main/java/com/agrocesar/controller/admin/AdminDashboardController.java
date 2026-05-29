package com.agrocesar.controller.admin;

import com.agrocesar.dto.AlertaVistaDTO;
import com.agrocesar.service.AlertaService;
import com.agrocesar.service.UsuarioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

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
    public String dashboard(Model model) {

        List<AlertaVistaDTO> historial = alertaService.findAll();

        long totalAlertas = historial.size();
        long alertasActivas = historial.stream()
                .filter(a -> !a.isLeida()).count();
        long alertasTransitorio = historial.stream()
                .filter(a -> "TRANSITORIO".equals(a.getCategoria())).count();
        long alertasPermanente = historial.stream()
                .filter(a -> "PERMANENTE".equals(a.getCategoria())).count();
        long agricultoresActivos = usuarioService.listarActivos().stream()
                .filter(u -> "AGRICULTOR".equals(u.getRol())).count();

        List<AlertaAdminView> historialView = historial.stream().map(a -> new AlertaAdminView(
                a.getAgricultor(),
                a.getCultivo(),
                a.getCategoria(),
                a.getDescripcion(),
                a.getFechaGeneracion() != null
                        ? a.getFechaGeneracion().toLocalDate().atStartOfDay()
                        : null))
                .toList();

        model.addAttribute("historialAlertas", historialView);
        model.addAttribute("totalAlertas", totalAlertas);
        model.addAttribute("alertasActivas", alertasActivas);
        model.addAttribute("alertasTransitorio", alertasTransitorio);
        model.addAttribute("alertasPermanente", alertasPermanente);
        model.addAttribute("agricultoresActivos", agricultoresActivos);

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
        private final java.time.LocalDateTime fechaCreacion;

        /**
         * Construye la vista a partir de los campos del DTO original.
         *
         * @param nombreAgricultor nombre completo del agricultor
         * @param nombreCultivo    nombre del cultivo afectado
         * @param categoria        TRANSITORIO o PERMANENTE
         * @param descripcion      descripcion de la alerta generada
         * @param fechaCreacion    fecha y hora de generacion de la alerta
         */
        public AlertaAdminView(String nombreAgricultor, String nombreCultivo,
                String categoria, String descripcion,
                java.time.LocalDateTime fechaCreacion) {
            this.nombreAgricultor = nombreAgricultor;
            this.nombreCultivo = nombreCultivo;
            this.categoria = categoria;
            this.descripcion = descripcion;
            this.fechaCreacion = fechaCreacion;
        }

        /** @return nombre completo del agricultor */
        public String getNombreAgricultor() {
            return nombreAgricultor;
        }

        /** @return nombre del cultivo afectado */
        public String getNombreCultivo() {
            return nombreCultivo;
        }

        /** @return categoria de la alerta: TRANSITORIO o PERMANENTE */
        public String getCategoria() {
            return categoria;
        }

        /** @return descripcion del umbral superado */
        public String getDescripcion() {
            return descripcion;
        }

        /** @return fecha y hora de generacion de la alerta */
        public java.time.LocalDateTime getFechaCreacion() {
            return fechaCreacion;
        }
    }
}