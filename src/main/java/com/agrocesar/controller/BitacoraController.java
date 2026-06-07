package com.agrocesar.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.agrocesar.dto.CultivoResumen;
import com.agrocesar.model.BitacoraCultivo;
import com.agrocesar.model.Usuario;
import com.agrocesar.repository.TipoActividadRepository;
import com.agrocesar.service.BitacoraService;
import com.agrocesar.service.CultivoAgricultorService;
import com.agrocesar.service.UsuarioService;
import org.springframework.ui.Model;


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
    private final CultivoController cultivoController;

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
            TipoActividadRepository tipoActividadRepository, CultivoController cultivoController) {
        this.bitacoraService = bitacoraService;
        this.usuarioService = usuarioService;
        this.cultivoService = cultivoService;
        this.tipoActividadRepository = tipoActividadRepository;
        this.cultivoController = cultivoController;
    }

    /**
     * Lista todas las entradas de bitacora del agricultor autenticado.
     * Si se recibe un cultivoId, filtra por ese cultivo.
     *
     * Atributos del modelo:
     * entradas  - lista de BitacoraCultivo del agricultor
     * cultivos  - lista de cultivos para el filtro por cultivo
     * cultivoId - id del cultivo seleccionado en el filtro, null si es todos
     *
     * @param userDetails usuario autenticado inyectado por Spring Security
     * @param cultivoId   filtro opcional por cultivo especifico
     * @param model       modelo de Spring MVC
     * @return vista cultivos/bitacora
     */

    @GetMapping
    public String listar ( @AuthenticationPrincipal UserDetails userDetails,@RequestParam(required = false) Long cultivoId, Model model){
        Usuario usuario = usuarioService.buscarPorEmail(userDetails.getUsername());
        
        List<BitacoraCultivo> entradas;
        if(cultivoId != null ){
            entradas = bitacoraService.listarPorCultivo(cultivoId);
        }else{
            entradas = bitacoraService.listarPorUsuario(usuario.getId());
        }

        List<CultivoResumen> cultivos = cultivoService.listarResumenPorUsuario(usuario.getId());

        model.addAttribute("entradas", entradas);
        model.addAttribute("cultivos", cultivos);
        model.addAttribute("cultivoId", cultivoId);

        return "cultivos/bitacora";
    }
}
