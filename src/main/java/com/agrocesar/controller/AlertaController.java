package com.agrocesar.controller;

import com.agrocesar.dto.AlertaVistaDTO;
import com.agrocesar.model.Usuario;
import com.agrocesar.service.AlertaService;
import com.agrocesar.service.UsuarioService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class AlertaController {

    private final AlertaService alertaService;
    private final UsuarioService usuarioService;

    public AlertaController(AlertaService alertaService,
                            UsuarioService usuarioService) {
        this.alertaService  = alertaService;
        this.usuarioService = usuarioService;
    }

    @GetMapping("/alertas")
    public String alertasAgricultor(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) String severidad,
            @RequestParam(required = false) String tipo,
            @RequestParam(required = false) Long cultivoId,
            @RequestParam(required = false, defaultValue = "false") Boolean soloNoLeidas,
            Model model) {

        Usuario usuario = usuarioService.buscarPorEmail(userDetails.getUsername());
        List<AlertaVistaDTO> alertas;

        if (soloNoLeidas) {
            alertas = alertaService.findNoLeidasByUsuarioId(usuario.getId());
        } else if (severidad != null && !severidad.isBlank()) {
            alertas = alertaService.findByUsuarioIdAndSeveridad(usuario.getId(), severidad);
        } else if (tipo != null && !tipo.isBlank()) {
            alertas = alertaService.findByUsuarioIdAndTipo(usuario.getId(), tipo);
        } else if (cultivoId != null) {
            alertas = alertaService.findByUsuarioIdAndCultivoId(usuario.getId(), cultivoId);
        } else {
            alertas = alertaService.findByUsuarioId(usuario.getId());
        }

        model.addAttribute("alertas", alertas);
        model.addAttribute("severidadFiltro", severidad);
        model.addAttribute("tipoFiltro", tipo);
        model.addAttribute("cultivoIdFiltro", cultivoId);
        model.addAttribute("soloNoLeidas", soloNoLeidas);
        return "alertas";
    }

    @PostMapping("/alertas/{id}/marcar-leida")
    public String marcarLeida(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        boolean resultado = alertaService.marcarLeida(id);

        if (resultado) {
            redirectAttributes.addFlashAttribute("exito", "Alerta marcada como leída.");
        } else {
            redirectAttributes.addFlashAttribute("error", "No se encontró la alerta.");
        }

        return "redirect:/alertas";
    }
}