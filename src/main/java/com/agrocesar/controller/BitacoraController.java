package com.agrocesar.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.agrocesar.repository.TipoActividadRepository;
import com.agrocesar.service.BitacoraService;
import com.agrocesar.service.CultivoAgricultorService;
import com.agrocesar.service.UsuarioService;



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
}
