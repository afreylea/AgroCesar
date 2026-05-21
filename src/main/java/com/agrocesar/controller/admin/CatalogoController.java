package com.agrocesar.controller.admin;

import com.agrocesar.model.CultivoCatalogo;
import com.agrocesar.service.CatalogoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/catalogo")
public class CatalogoController {

    private static final Logger log = LoggerFactory.getLogger(CatalogoController.class);
    private final CatalogoService catalogoService;

    public CatalogoController(CatalogoService catalogoService) {
        this.catalogoService = catalogoService;
    }

    // ── Listar ────────────────────────────────────────────────────────────────

    @GetMapping
    public String listar(Model model) {
        log.info("=== Cargando página de catálogo ===");

        List<CultivoCatalogo> catalogos = catalogoService.listarTodos();
        log.info("Cultivos encontrados: {}", catalogos.size());

        model.addAttribute("catalogos", catalogos);

        // Calcular y agregar estadísticas
        calcularEstadisticas(catalogos, model);

        // DEBUG: Verificar que se agregaron los atributos
        log.info("totalCultivos agregado: {}", model.getAttribute("totalCultivos"));
        log.info("tempPromedio agregado: {}", model.getAttribute("tempPromedio"));

        return "admin/catalogo";
    }

    // ── Método para calcular estadísticas ─────────────────────────────────────

    private void calcularEstadisticas(List<CultivoCatalogo> catalogos, Model model) {
        if (catalogos == null || catalogos.isEmpty()) {
            log.warn("No hay cultivos para calcular estadísticas");
            model.addAttribute("totalCultivos", 0);
            model.addAttribute("tempPromedio", "0°C");
            model.addAttribute("humedadPromedio", "0%");
            model.addAttribute("lluviaPromedio", "0 mm");
            return;
        }

        // Total de cultivos
        int total = catalogos.size();

        // Temperatura promedio (promedio entre min y max de cada cultivo)
        double tempProm = catalogos.stream()
                .mapToDouble(c -> (c.getTempMin() + c.getTempMax()) / 2.0)
                .average()
                .orElse(0.0);

        // Humedad promedio
        double humedadProm = catalogos.stream()
                .mapToDouble(c -> (c.getHumedadMin() + c.getHumedadMax()) / 2.0)
                .average()
                .orElse(0.0);

        // Lluvia promedio
        double lluviaProm = catalogos.stream()
                .mapToDouble(c -> (c.getLluviaMin() + c.getLluviaMax()) / 2.0)
                .average()
                .orElse(0.0);

        // Agregar al modelo con formato
        model.addAttribute("totalCultivos", total);
        model.addAttribute("tempPromedio", String.format("%.1f°C", tempProm));
        model.addAttribute("humedadPromedio", String.format("%.1f%%", humedadProm));
        model.addAttribute("lluviaPromedio", String.format("%.1f mm", lluviaProm));

        log.info("Estadísticas calculadas - Total: {}, Temp: {}°C, Humedad: {}%, Lluvia: {} mm",
                total, tempProm, humedadProm, lluviaProm);
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
            log.error("Error al crear cultivo: {}", e.getMessage());
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

    @PostMapping("/nuevo")
    public String registrar(@ModelAttribute CultivoCatalogo catalogo,
            RedirectAttributes redirectAttributes) {
        catalogoService.crear(catalogo);
        redirectAttributes.addFlashAttribute("mensaje", "Cultivo registrado correctamente.");
        redirectAttributes.addFlashAttribute("tipo", "success");
        return "redirect:/admin/catalogo";
    }

    @PostMapping("/{id}/editar")
    public String actualizar(@PathVariable Long id, @ModelAttribute CultivoCatalogo catalogo,
            RedirectAttributes redirectAttributes) {
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
}