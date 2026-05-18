package com.agrocesar.controller.admin;

import com.agrocesar.model.CultivoCatalogo;
import com.agrocesar.service.CatalogoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/catalogo")
public class CatalogoController {

    private final CatalogoService catalogoService;

    public CatalogoController(CatalogoService catalogoService) {
        this.catalogoService = catalogoService;
    }

    // ── Listar ────────────────────────────────────────────────────────────────

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("catalogos", catalogoService.listarTodos());
        return "admin/catalogo";
    }

    // ── Formulario Nuevo ──────────────────────────────────────────────────────

    @GetMapping("/nuevo")
    public String formularioNuevo(Model model) {
        model.addAttribute("catalogo", new CultivoCatalogo());
        model.addAttribute("accion", "nuevo");
        return "admin/catalogo-form";
    }

    // ── Guardar Nuevo ─────────────────────────────────────────────────────────

    @PostMapping("/nuevo")
    public String guardarNuevo(@ModelAttribute CultivoCatalogo catalogo,
                               RedirectAttributes redirectAttributes) {
        try {
            catalogoService.crear(catalogo);
            redirectAttributes.addFlashAttribute("mensaje", "Cultivo creado correctamente.");
            redirectAttributes.addFlashAttribute("tipo", "success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje", "Error al crear el cultivo.");
            redirectAttributes.addFlashAttribute("tipo", "error");
        }
        return "redirect:/admin/catalogo";
    }

    // ── Formulario Editar ─────────────────────────────────────────────────────

    @GetMapping("/editar/{id}")
    public String formularioEditar(@PathVariable Long id, Model model,
                                   RedirectAttributes redirectAttributes) {
        return catalogoService.buscarPorId(id)
                .map(catalogo -> {
                    model.addAttribute("catalogo", catalogo);
                    model.addAttribute("accion", "editar");
                    return "admin/catalogo-form";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("mensaje", "Cultivo no encontrado.");
                    redirectAttributes.addFlashAttribute("tipo", "error");
                    return "redirect:/admin/catalogo";
                });
    }

    // ── Guardar Edición ───────────────────────────────────────────────────────

    @PostMapping("/editar/{id}")
    public String guardarEdicion(@PathVariable Long id,
                                 @ModelAttribute CultivoCatalogo catalogo,
                                 RedirectAttributes redirectAttributes) {
        catalogo.setId(id);
        boolean actualizado = catalogoService.actualizar(catalogo);

        if (actualizado) {
            redirectAttributes.addFlashAttribute("mensaje", "Cultivo actualizado correctamente.");
            redirectAttributes.addFlashAttribute("tipo", "success");
        } else {
            redirectAttributes.addFlashAttribute("mensaje", "No se encontró el cultivo a actualizar.");
            redirectAttributes.addFlashAttribute("tipo", "error");
        }
        return "redirect:/admin/catalogo";
    }

    // ── Desactivar ────────────────────────────────────────────────────────────

    @PostMapping("/desactivar/{id}")
    public String desactivar(@PathVariable Long id,
                             RedirectAttributes redirectAttributes) {
        boolean resultado = catalogoService.desactivar(id);

        if (resultado) {
            redirectAttributes.addFlashAttribute("mensaje", "Cultivo desactivado correctamente.");
            redirectAttributes.addFlashAttribute("tipo", "success");
        } else {
            redirectAttributes.addFlashAttribute("mensaje", "No se encontró el cultivo.");
            redirectAttributes.addFlashAttribute("tipo", "error");
        }
        return "redirect:/admin/catalogo";
    }

    // ── Activar ───────────────────────────────────────────────────────────────

    @PostMapping("/activar/{id}")
    public String activar(@PathVariable Long id,
                          RedirectAttributes redirectAttributes) {
        boolean resultado = catalogoService.activar(id);

        if (resultado) {
            redirectAttributes.addFlashAttribute("mensaje", "Cultivo activado correctamente.");
            redirectAttributes.addFlashAttribute("tipo", "success");
        } else {
            redirectAttributes.addFlashAttribute("mensaje", "No se encontró el cultivo.");
            redirectAttributes.addFlashAttribute("tipo", "error");
        }
        return "redirect:/admin/catalogo";
    }
}