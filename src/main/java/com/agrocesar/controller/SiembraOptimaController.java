package com.agrocesar.controller;

import com.agrocesar.dto.CultivoConUmbralesDTO;
import com.agrocesar.model.Usuario;
import com.agrocesar.repository.CultivoConUmbralesRepository;
import com.agrocesar.service.SiembraOptimaService;
import com.agrocesar.service.SiembraOptimaService.ResultadoSiembra;
import com.agrocesar.service.UsuarioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * SiembraOptimaController expone la vista de recomendacion de siembra optima
 * para el agricultor autenticado.
 *
 * Ruta base: /siembra-optima
 *
 * El agricultor selecciona uno de sus cultivos registrados y el sistema
 * analiza el pronostico climatico de los proximos 7 dias contra los umbrales
 * efectivos del catalogo, devolviendo una guia de accion generada por Groq.
 */
@Controller
@RequestMapping("/siembra-optima")
public class SiembraOptimaController {

    private static final Logger log = LoggerFactory.getLogger(SiembraOptimaController.class);

    private final UsuarioService usuarioService;
    private final CultivoConUmbralesRepository cultivoRepo;
    private final SiembraOptimaService siembraOptimaService;

    /**
     * Constructor con inyeccion por constructor.
     *
     * @param usuarioService       servicio para obtener el usuario autenticado por
     *                             email
     * @param cultivoRepo          repositorio que consulta V_CULTIVOS_CON_UMBRALES
     * @param siembraOptimaService servicio que analiza la ventana optima de siembra
     */
    public SiembraOptimaController(UsuarioService usuarioService,
            CultivoConUmbralesRepository cultivoRepo,
            SiembraOptimaService siembraOptimaService) {
        this.usuarioService = usuarioService;
        this.cultivoRepo = cultivoRepo;
        this.siembraOptimaService = siembraOptimaService;
    }

    /**
     * Renderiza la vista de siembra optima para el agricultor autenticado.
     *
     * Si el agricultor no tiene cultivos registrados, la vista muestra
     * un estado vacio con un enlace para registrar cultivos.
     *
     * Si se recibe un cultivoId por parametro, analiza ese cultivo especifico.
     * Si no se recibe, analiza el primer cultivo de la lista por defecto.
     *
     * Atributos que se agregan al modelo:
     * cultivos - lista de CultivoConUmbralesDTO del agricultor autenticado
     * cultivoId - id del cultivo actualmente seleccionado
     * resultado - ResultadoSiembra con el analisis completo, o null si fallo
     *
     * @param userDetails usuario autenticado inyectado por Spring Security
     * @param cultivoId   id del cultivo a analizar, opcional
     * @param model       modelo de Spring MVC
     * @return nombre de la plantilla Thymeleaf: cultivos/siembra-optima
     */
    @GetMapping
    public String siembraOptima(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) Long cultivoId,
            Model model) {

        Usuario usuario = usuarioService.buscarPorEmail(userDetails.getUsername());
        List<CultivoConUmbralesDTO> cultivos = cultivoRepo.findByUsuarioId(usuario.getId());

        model.addAttribute("cultivos", cultivos);

        if (cultivos.isEmpty()) {
            model.addAttribute("cultivoId", null);
            model.addAttribute("resultado", null);
            return "cultivos/siembra-optima";
        }

        // Selecciona el cultivo solicitado o el primero por defecto
        CultivoConUmbralesDTO seleccionado = cultivos.stream()
                .filter(c -> c.getId().equals(cultivoId))
                .findFirst()
                .orElse(cultivos.get(0));

        model.addAttribute("cultivoId", seleccionado.getId());

        ResultadoSiembra resultado = siembraOptimaService.analizar(seleccionado);

        if (resultado == null) {
            log.warn("[SiembraOptima] Analisis fallido para cultivo={} usuario={}",
                    seleccionado.getCultivo(), usuario.getId());
        }

        model.addAttribute("resultado", resultado);
        return "cultivos/siembra-optima";
    }
}