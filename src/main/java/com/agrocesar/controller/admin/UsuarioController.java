package com.agrocesar.controller.admin;

import com.agrocesar.dto.AlertaVistaDTO;
import com.agrocesar.dto.CultivoResumen;
import com.agrocesar.model.Usuario;
import com.agrocesar.service.AlertaService;
import com.agrocesar.service.CultivoAgricultorService;
import com.agrocesar.service.UsuarioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final CultivoAgricultorService cultivoService;
    private final AlertaService alertaService;

    public UsuarioController(UsuarioService usuarioService,
            CultivoAgricultorService cultivoService,
            AlertaService alertaService) {
        this.usuarioService = usuarioService;
        this.cultivoService = cultivoService;
        this.alertaService = alertaService;
    }

    // Listar

    @GetMapping
    public String listar(
            @RequestParam(required = false, defaultValue = "0") int filtro,
            Model model) {

        List<Usuario> usuarios = switch (filtro) {
            case 1 -> usuarioService.listarActivos();
            case 2 -> usuarioService.listarInactivos();
            default -> usuarioService.listarTodos();
        };

        model.addAttribute("usuarios", usuarios);
        model.addAttribute("filtro", filtro);

        return "admin/usuarios";
    }

    // Detalle

    @GetMapping("/{id}")
    public String detalle(@PathVariable Long id, Model model,
            RedirectAttributes redirectAttributes) {

        Usuario usuario = usuarioService.findById(id);

        if (usuario == null) {
            redirectAttributes.addFlashAttribute("mensaje", "Usuario no encontrado.");
            redirectAttributes.addFlashAttribute("tipo", "error");
            return "redirect:/admin/usuarios";
        }

        if ("ADMIN".equals(usuario.getRol())) {
            redirectAttributes.addFlashAttribute("mensaje", "Esta vista es solo para agricultores.");
            redirectAttributes.addFlashAttribute("tipo", "error");
            return "redirect:/admin/usuarios";
        }

        List<CultivoResumen> cultivos = cultivoService.listarResumenPorUsuario(id);
        List<AlertaVistaDTO> alertas = alertaService.findByUsuarioId(id);

        double totalHectareas = cultivos.stream()
                .mapToDouble(CultivoResumen::getHectareas).sum();
        long alertasNoLeidas = alertas.stream()
                .filter(a -> !a.isLeida()).count();
        long alertasAlta = alertas.stream()
                .filter(a -> "ALTA".equals(a.getSeveridad())).count();
        long alertasMedia = alertas.stream()
                .filter(a -> "MEDIA".equals(a.getSeveridad())).count();
        long alertasBaja = alertas.stream()
                .filter(a -> "BAJA".equals(a.getSeveridad())).count();

        model.addAttribute("usuario", usuario);
        model.addAttribute("totalCultivos", cultivos.size());
        model.addAttribute("totalHectareas", totalHectareas);
        model.addAttribute("totalAlertas", alertas.size());
        model.addAttribute("alertasNoLeidas", alertasNoLeidas);
        model.addAttribute("alertasAlta", alertasAlta);
        model.addAttribute("alertasMedia", alertasMedia);
        model.addAttribute("alertasBaja", alertasBaja);
        model.addAttribute("alertas", alertas);
        model.addAttribute("cultivos", cultivos);

        return "admin/usuario-detalle";
    }

    // Activar

    @PostMapping("/{id}/activar")
    public String activar(@PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        boolean resultado = usuarioService.activar(id);

        if (resultado) {
            redirectAttributes.addFlashAttribute("mensaje", "Usuario activado correctamente.");
            redirectAttributes.addFlashAttribute("tipo", "success");
        } else {
            redirectAttributes.addFlashAttribute("mensaje", "No se encontró el usuario.");
            redirectAttributes.addFlashAttribute("tipo", "error");
        }
        return "redirect:/admin/usuarios";
    }

    // Desactivar

    @PostMapping("/{id}/desactivar")
    public String desactivar(@PathVariable Long id, RedirectAttributes redirectAttributes) {

        boolean resultado = usuarioService.desactivar(id);

        if (resultado) {
            redirectAttributes.addFlashAttribute("mensaje",
                    "Usuario desactivado correctamente.");
            redirectAttributes.addFlashAttribute("tipo", "warning");
        } else {
            redirectAttributes.addFlashAttribute("mensaje", "No se encontró el usuario.");
            redirectAttributes.addFlashAttribute("tipo", "error");
        }
        return "redirect:/admin/usuarios";
    }
}