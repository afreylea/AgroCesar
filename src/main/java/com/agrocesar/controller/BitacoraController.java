package com.agrocesar.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

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
    
}
