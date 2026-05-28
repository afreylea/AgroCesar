package com.agrocesar.controller.admin;

import com.agrocesar.model.CultivoCatalogo;
import com.agrocesar.service.CatalogoService;
import com.agrocesar.service.ImagenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/catalogo")
public class CatalogoController {

    private static final Logger log = LoggerFactory.getLogger(CatalogoController.class);
    private final CatalogoService catalogoService;
    private final ImagenService imagenService;

    public CatalogoController(CatalogoService catalogoService, ImagenService imagenService) {
        this.catalogoService = catalogoService;
        this.imagenService = imagenService;
    }

    @GetMapping
    public String listar(Model model) {
        List<CultivoCatalogo> catalogos = catalogoService.listarTodos();
        model.addAttribute("catalogos", catalogos);
        calcularEstadisticas(catalogos, model);
        return "admin/catalogo";
    }

    private void calcularEstadisticas(List<CultivoCatalogo> catalogos, Model model) {
        if (catalogos == null || catalogos.isEmpty()) {
            model.addAttribute("totalCultivos", 0);
            model.addAttribute("tempPromedio", "0°C");
            model.addAttribute("humedadPromedio", "0%");
            model.addAttribute("lluviaPromedio", "0 mm");
            return;
        }
        model.addAttribute("totalCultivos", catalogos.size());
        model.addAttribute("tempPromedio", String.format("%.1f°C",
                catalogos.stream().mapToDouble(c -> (c.getTempMin() + c.getTempMax()) / 2.0).average().orElse(0)));
        model.addAttribute("humedadPromedio", String.format("%.1f%%",
                catalogos.stream().mapToDouble(c -> (c.getHumedadMin() + c.getHumedadMax()) / 2.0).average()
                        .orElse(0)));
        model.addAttribute("lluviaPromedio", String.format("%.1f mm",
                catalogos.stream().mapToDouble(c -> (c.getLluviaMin() + c.getLluviaMax()) / 2.0).average().orElse(0)));
    }

    @GetMapping("/nuevo")
    public String formularioNuevo(Model model) {
        model.addAttribute("catalogo", new CultivoCatalogo());
        model.addAttribute("accion", "nuevo");
        return "admin/catalogo-form";
    }

    @PostMapping("/nuevo")
    public String guardarNuevo(@ModelAttribute CultivoCatalogo catalogo,
            @RequestParam(value = "imagen", required = false) MultipartFile imagen,
            RedirectAttributes redirectAttributes) {
        try {
            // Si subio imagen, guardarla y asignar nombre
            if (imagen != null && !imagen.isEmpty()) {
                String nombreArchivo = imagenService.guardar(imagen);
                catalogo.setImagenUrl(nombreArchivo);
            }
            catalogoService.crear(catalogo);
            redirectAttributes.addFlashAttribute("exito", "Cultivo creado correctamente.");
        } catch (Exception e) {
            log.error("Error al crear cultivo: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Error al crear el cultivo.");
        }
        return "redirect:/admin/catalogo";
    }

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
                    redirectAttributes.addFlashAttribute("error", "Cultivo no encontrado.");
                    return "redirect:/admin/catalogo";
                });
    }

    @PostMapping("/editar/{id}")
    public String guardarEdicion(@PathVariable Long id,
            @ModelAttribute CultivoCatalogo catalogo,
            @RequestParam(value = "imagen", required = false) MultipartFile imagen,
            RedirectAttributes redirectAttributes) {
        try {
            catalogo.setId(id);

            // Si subió imagen nueva, eliminar la anterior y guardar la nueva
            if (imagen != null && !imagen.isEmpty()) {
                catalogoService.buscarPorId(id)
                        .ifPresent(anterior -> imagenService.eliminar(anterior.getImagenUrl()));
                String nombreArchivo = imagenService.guardar(imagen);
                catalogo.setImagenUrl(nombreArchivo);
            } else {
                // Conservar la imagen anterior si no subió una nueva
                catalogoService.buscarPorId(id)
                        .ifPresent(anterior -> catalogo.setImagenUrl(anterior.getImagenUrl()));
            }

            boolean actualizado = catalogoService.actualizar(catalogo);
            if (actualizado) {
                redirectAttributes.addFlashAttribute("exito", "Cultivo actualizado correctamente.");
            } else {
                redirectAttributes.addFlashAttribute("error", "No se encontró el cultivo.");
            }
        } catch (Exception e) {
            log.error("Error al actualizar cultivo: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Error al actualizar el cultivo.");
        }
        return "redirect:/admin/catalogo";
    }

    @PostMapping("/desactivar/{id}")
    public String desactivar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        boolean resultado = catalogoService.desactivar(id);
        redirectAttributes.addFlashAttribute(resultado ? "exito" : "error",
                resultado ? "Cultivo desactivado correctamente." : "No se encontró el cultivo.");
        return "redirect:/admin/catalogo";
    }

    @PostMapping("/activar/{id}")
    public String activar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        boolean resultado = catalogoService.activar(id);
        redirectAttributes.addFlashAttribute(resultado ? "exito" : "error",
                resultado ? "Cultivo activado correctamente." : "No se encontró el cultivo.");
        return "redirect:/admin/catalogo";
    }
}