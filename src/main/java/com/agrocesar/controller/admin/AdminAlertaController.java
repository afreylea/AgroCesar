package com.agrocesar.controller.admin;

import com.agrocesar.dto.AlertaVistaDTO;
import com.agrocesar.service.AlertaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class AdminAlertaController {

    private final AlertaService alertaService;

    public AdminAlertaController(AlertaService alertaService) {
        this.alertaService  = alertaService;
    }

    @GetMapping("/admin/alertas")
    public String alertasAdmin(
            @RequestParam(required = false) String severidad,
            @RequestParam(required = false) String tipo,
            @RequestParam(required = false) Long municipioId,
            @RequestParam(required = false) Long catalogoId,
            @RequestParam(required = false) Long cultivoId,
            @RequestParam(required = false) Long usuarioId,
            Model model) {

        List<AlertaVistaDTO> alertas;

        if (severidad != null && !severidad.isBlank()) {
            alertas = alertaService.findBySeveridad(severidad);
        } else if (tipo != null && !tipo.isBlank()) {
            alertas = alertaService.findByTipo(tipo);
        } else if (municipioId != null) {
            alertas = alertaService.findByMunicipioId(municipioId);
        } else if (catalogoId != null) {
            alertas = alertaService.findByCatalogoId(catalogoId);
        } else if (cultivoId != null) {
            alertas = alertaService.findByCultivoId(cultivoId);
        } else if (usuarioId != null) {
            alertas = alertaService.findByUsuarioId(usuarioId);
        } else {
            alertas = alertaService.findAll();
        }

        model.addAttribute("alertas", alertas);
        model.addAttribute("severidadFiltro", severidad);
        model.addAttribute("tipoFiltro", tipo);
        model.addAttribute("municipioIdFiltro", municipioId);
        model.addAttribute("catalogoIdFiltro", catalogoId);
        model.addAttribute("cultivoIdFiltro", cultivoId);
        model.addAttribute("usuarioIdFiltro", usuarioId);
        return "admin/alertas";
    }
}