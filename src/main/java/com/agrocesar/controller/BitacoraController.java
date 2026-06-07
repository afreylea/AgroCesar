package com.agrocesar.controller;

import com.agrocesar.model.BitacoraCultivo;
import com.agrocesar.model.Usuario;
import com.agrocesar.repository.TipoActividadRepository;
import com.agrocesar.service.BitacoraService;
import com.agrocesar.service.CultivoAgricultorService;
import com.agrocesar.service.UsuarioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controller de la bitacora de actividades agricolas.
 *
 * Ruta base: /bitacora
 *
 * Permite al agricultor autenticado registrar, consultar, eliminar
 * y exportar actividades realizadas en sus cultivos. Cada entrada
 * puede estar vinculada opcionalmente a una alerta del sistema.
 */
@Controller
@RequestMapping("/bitacora")
public class BitacoraController {

    private static final Logger log = LoggerFactory.getLogger(BitacoraController.class);

    private final BitacoraService bitacoraService;
    private final UsuarioService usuarioService;
    private final CultivoAgricultorService cultivoService;
    private final TipoActividadRepository tipoActividadRepository;

    /**
     * Constructor con inyeccion por constructor.
     *
     * @param bitacoraService         servicio de logica de negocio de la bitacora
     * @param usuarioService          servicio para obtener el usuario autenticado
     * @param cultivoService          servicio para listar cultivos del agricultor
     * @param tipoActividadRepository repositorio de tipos de actividad
     */
    public BitacoraController(BitacoraService bitacoraService,
            UsuarioService usuarioService,
            CultivoAgricultorService cultivoService,
            TipoActividadRepository tipoActividadRepository) {
        this.bitacoraService = bitacoraService;
        this.usuarioService = usuarioService;
        this.cultivoService = cultivoService;
        this.tipoActividadRepository = tipoActividadRepository;
    }

    /**
     * Lista todas las entradas de bitacora del agricultor autenticado.
     * Soporta filtro por cultivo y por rango de fechas.
     *
     * Calcula estadisticas para las stats cards:
     * totalEntradas, ultimaFecha, entradasConAlerta, tipoFrecuente
     *
     * @param userDetails usuario autenticado inyectado por Spring Security
     * @param cultivoId   filtro opcional por cultivo especifico
     * @param fechaDesde  fecha inicial del rango, opcional
     * @param fechaHasta  fecha final del rango, opcional
     * @param model       modelo de Spring MVC
     * @return vista cultivos/bitacora
     */
    @GetMapping
    public String listar(@AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) Long cultivoId,
            @RequestParam(required = false) String fechaDesde,
            @RequestParam(required = false) String fechaHasta,
            Model model) {

        Usuario usuario = usuarioService.buscarPorEmail(userDetails.getUsername());

        // Obtener entradas segun filtros
        List<BitacoraCultivo> entradas;

        if (fechaDesde != null && fechaHasta != null
                && !fechaDesde.isBlank() && !fechaHasta.isBlank()) {
            LocalDate desde = LocalDate.parse(fechaDesde);
            LocalDate hasta = LocalDate.parse(fechaHasta);
            entradas = bitacoraService.listarPorUsuarioRango(usuario.getId(), desde, hasta);
        } else if (cultivoId != null) {
            entradas = bitacoraService.listarPorCultivo(cultivoId);
        } else {
            entradas = bitacoraService.listarPorUsuario(usuario.getId());
        }

        // Stats cards
        long entradasConAlerta = entradas.stream()
                .filter(e -> e.getAlertaId() != null)
                .count();

        String tipoFrecuente = entradas.stream()
                .collect(Collectors.groupingBy(BitacoraCultivo::getTipoNombre, Collectors.counting()))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("—");

        long tipoFrecuenteCount = entradas.stream()
                .filter(e -> e.getTipoNombre().equals(tipoFrecuente))
                .count();

        model.addAttribute("entradas", entradas);
        model.addAttribute("cultivos", cultivoService.listarResumenPorUsuario(usuario.getId()));
        model.addAttribute("cultivoId", cultivoId);
        model.addAttribute("fechaDesde", fechaDesde);
        model.addAttribute("fechaHasta", fechaHasta);

        // Stats
        model.addAttribute("totalEntradas", entradas.size());
        model.addAttribute("entradasConAlerta", entradasConAlerta);
        model.addAttribute("tipoFrecuente", tipoFrecuente);
        model.addAttribute("tipoFrecuenteCount", tipoFrecuenteCount);

        return "cultivos/bitacora";
    }

    /**
     * Muestra el formulario para registrar una nueva entrada en la bitacora.
     *
     * @param userDetails usuario autenticado
     * @param cultivoId   cultivo preseleccionado, opcional
     * @param model       modelo de Spring MVC
     * @return vista cultivos/bitacora-form
     */
    @GetMapping("/nueva")
    public String formularioNueva(@AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) Long cultivoId,
            Model model) {

        Usuario usuario = usuarioService.buscarPorEmail(userDetails.getUsername());

        model.addAttribute("cultivos", cultivoService.listarResumenPorUsuario(usuario.getId()));
        model.addAttribute("tiposActividad", tipoActividadRepository.listarActivos());
        model.addAttribute("cultivoId", cultivoId);

        return "cultivos/bitacora-form";
    }

    /**
     * Procesa el formulario de registro de una nueva entrada.
     * Incluye campos opcionales de responsable y ubicacion.
     *
     * @param cultivoAgricultorId id del cultivo seleccionado
     * @param tipoActividadId     id del tipo de actividad realizada
     * @param alertaId            id de la alerta asociada, puede ser null
     * @param descripcion         nota libre del agricultor
     * @param fechaActividad      fecha en que se realizo la actividad
     * @param responsable         nombre de quien realizo la actividad
     * @param ubicacion           lugar donde se realizo la actividad
     * @param redirectAttributes  atributos flash para mensajes post-redirect
     * @return redireccion a /bitacora
     */
    @PostMapping("/nueva")
    public String registrar(@RequestParam Long cultivoAgricultorId,
            @RequestParam Long tipoActividadId,
            @RequestParam(required = false) Long alertaId,
            @RequestParam(required = false) String descripcion,
            @RequestParam String fechaActividad,
            @RequestParam(required = false) String responsable,
            @RequestParam(required = false) String ubicacion,
            RedirectAttributes redirectAttributes) {
        try {
            LocalDate fecha = LocalDate.parse(fechaActividad);

            bitacoraService.registrar(cultivoAgricultorId, tipoActividadId,
                    alertaId, descripcion, fecha, responsable, ubicacion);

            redirectAttributes.addFlashAttribute("exito",
                    "Actividad registrada correctamente.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            log.error("[Bitacora] Error al registrar entrada: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error",
                    "Error al registrar la actividad.");
        }
        return "redirect:/bitacora";
    }

    /**
     * Elimina una entrada de la bitacora.
     *
     * @param id                 identificador de la entrada a eliminar
     * @param redirectAttributes atributos flash para mensajes post-redirect
     * @return redireccion a /bitacora
     */
    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Long id,
            RedirectAttributes redirectAttributes) {
        try {
            boolean eliminado = bitacoraService.eliminar(id);
            redirectAttributes.addFlashAttribute(
                    eliminado ? "exito" : "error",
                    eliminado ? "Entrada eliminada correctamente."
                            : "No se encontro la entrada.");
        } catch (Exception e) {
            log.error("[Bitacora] Error al eliminar entrada {}: {}", id, e.getMessage());
            redirectAttributes.addFlashAttribute("error",
                    "Error al eliminar la entrada.");
        }
        return "redirect:/bitacora";
    }

    /**
     * Exporta las entradas de bitacora del agricultor a CSV.
     * El archivo se descarga directamente desde el navegador.
     *
     * @param userDetails usuario autenticado
     * @param response    respuesta HTTP para escribir el archivo
     */
    @GetMapping("/exportar")
    public void exportarCsv(@AuthenticationPrincipal UserDetails userDetails,
            HttpServletResponse response) {
        try {
            Usuario usuario = usuarioService.buscarPorEmail(userDetails.getUsername());
            List<BitacoraCultivo> entradas = bitacoraService.listarPorUsuario(usuario.getId());

            String fecha = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            response.setContentType("text/csv; charset=UTF-8");
            response.setHeader("Content-Disposition",
                    "attachment; filename=bitacora_" + fecha + ".csv");

            PrintWriter writer = response.getWriter();

            // BOM para que Excel reconozca UTF-8
            writer.write('\uFEFF');

            // Encabezados
            writer.println("Fecha Actividad,Tipo,Cultivo,Responsable,Ubicacion,Descripcion,Con Alerta,Fecha Registro");

            // Datos
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            for (BitacoraCultivo e : entradas) {
                writer.println(String.join(",",
                        escapeCsv(e.getFechaActividad() != null ? e.getFechaActividad().format(fmt) : ""),
                        escapeCsv(e.getTipoNombre()),
                        escapeCsv(e.getCultivo()),
                        escapeCsv(e.getResponsable()),
                        escapeCsv(e.getUbicacion()),
                        escapeCsv(e.getDescripcion()),
                        e.getAlertaId() != null ? "Si" : "No",
                        escapeCsv(e.getFechaCreacion() != null ? e.getFechaCreacion().format(fmt) : "")));
            }

            writer.flush();
        } catch (Exception e) {
            log.error("[Bitacora] Error al exportar CSV: {}", e.getMessage());
        }
    }

    /**
     * Escapa un valor para CSV: si contiene comas, saltos de linea
     * o comillas, lo envuelve en comillas dobles.
     *
     * @param valor texto a escapar, puede ser null
     * @return texto seguro para CSV
     */
    private String escapeCsv(String valor) {
        if (valor == null)
            return "";
        if (valor.contains(",") || valor.contains("\"") || valor.contains("\n")) {
            return "\"" + valor.replace("\"", "\"\"") + "\"";
        }
        return valor;
    }
}