package com.agrocesar.controller;

import com.agrocesar.model.BitacoraCultivo;
import com.agrocesar.model.Usuario;
import com.agrocesar.dto.CultivoResumen;
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

import java.time.LocalDate;
import java.util.List;

/**
 * Controller de la bitacora de actividades agricolas.
 *
 * Ruta base: /bitacora
 *
 * Permite al agricultor autenticado registrar, consultar y eliminar
 * actividades realizadas en sus cultivos. Cada entrada puede estar
 * vinculada opcionalmente a una alerta del sistema.
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
     * Si se recibe un cultivoId, filtra por ese cultivo.
     *
     * Atributos del modelo:
     * entradas - lista de BitacoraCultivo del agricultor
     * cultivos - lista de cultivos para el filtro por cultivo
     * cultivoId - id del cultivo seleccionado en el filtro, null si es todos
     *
     * @param userDetails usuario autenticado inyectado por Spring Security
     * @param cultivoId   filtro opcional por cultivo especifico
     * @param model       modelo de Spring MVC
     * @return vista cultivos/bitacora
     */
    @GetMapping
    public String listar(@AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) Long cultivoId,
            Model model) {

        Usuario usuario = usuarioService.buscarPorEmail(userDetails.getUsername());

        List<BitacoraCultivo> entradas;
        if (cultivoId != null) {
            entradas = bitacoraService.listarPorCultivo(cultivoId);
        } else {
            entradas = bitacoraService.listarPorUsuario(usuario.getId());
        }

        List<CultivoResumen> cultivos = cultivoService.listarResumenPorUsuario(usuario.getId());

        model.addAttribute("entradas", entradas);
        model.addAttribute("cultivos", cultivos);
        model.addAttribute("cultivoId", cultivoId);

        return "cultivos/bitacora";
    }

    /**
     * Muestra el formulario para registrar una nueva entrada en la bitacora.
     *
     * Atributos del modelo:
     * cultivos - lista de cultivos del agricultor para el selector
     * tiposActividad - lista de tipos de actividad activos
     * cultivoId - id del cultivo preseleccionado si viene por parametro
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
     * Redirige a la bitacora con mensaje de exito o error.
     *
     * @param userDetails         usuario autenticado
     * @param cultivoAgricultorId id del cultivo seleccionado
     * @param tipoActividadId     id del tipo de actividad realizada
     * @param alertaId            id de la alerta asociada, puede ser null
     * @param descripcion         nota libre del agricultor
     * @param fechaActividad      fecha en que se realizo la actividad
     * @param redirectAttributes  atributos flash para mensajes post-redirect
     * @return redireccion a /bitacora
     */
    @PostMapping("/nueva")
    public String registrar(@AuthenticationPrincipal UserDetails userDetails,
            @RequestParam Long cultivoAgricultorId,
            @RequestParam Long tipoActividadId,
            @RequestParam(required = false) Long alertaId,
            @RequestParam(required = false) String descripcion,
            @RequestParam String fechaActividad,
            RedirectAttributes redirectAttributes) {
        try {
            LocalDate fecha = LocalDate.parse(fechaActividad);

            bitacoraService.registrar(cultivoAgricultorId, tipoActividadId,
                    alertaId, descripcion, fecha);

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
     * Solo permite eliminar entradas que pertenezcan al agricultor autenticado.
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
}