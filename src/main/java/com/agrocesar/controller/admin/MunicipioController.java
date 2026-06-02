package com.agrocesar.controller.admin;

import com.agrocesar.model.Municipio;
import com.agrocesar.service.MunicipioService;
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
@RequestMapping("/admin/municipios")
public class MunicipioController {

    private final MunicipioService municipioService;

    public MunicipioController(MunicipioService municipioService) {
        this.municipioService = municipioService;
    }

    @GetMapping
    public String listar(@RequestParam(required = false, defaultValue = "0") int filtro,
                         Model model) {

        List<Municipio> municipios = switch (filtro) {
            case 1  -> municipioService.findActivos();
            case 2  -> municipioService.findInactivos();
            default -> municipioService.findAll();
        };

        model.addAttribute("municipios", municipios);
        model.addAttribute("filtro", filtro);

        return "admin/municipios";
    }

    @GetMapping("/{id}")
    public String detalle(@PathVariable Long id,
                          Model model,
                          RedirectAttributes redirectAttributes) {

        Municipio municipio = municipioService.findById(id).orElse(null);

        if (municipio == null) {
            redirectAttributes.addFlashAttribute("mensaje", "Municipio no encontrado.");
            redirectAttributes.addFlashAttribute("tipo", "error");
            return "redirect:/admin/municipios";
        }

        model.addAttribute("municipio", municipio);
        return "admin/municipio-detalle";
    }

    @PostMapping("/crear")
    public String crear(
            @RequestParam String nombre,
            @RequestParam String departamento,
            @RequestParam Double latitud,
            @RequestParam Double longitud,
            RedirectAttributes redirectAttributes) {

        try {
            municipioService.insertar(nombre, departamento, latitud, longitud);
            redirectAttributes.addFlashAttribute("mensaje", "Municipio creado correctamente.");
            redirectAttributes.addFlashAttribute("tipo", "success");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("mensaje", e.getMessage());
            redirectAttributes.addFlashAttribute("tipo", "error");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje", "Error inesperado al crear el municipio.");
            redirectAttributes.addFlashAttribute("tipo", "error");
        }

        return "redirect:/admin/municipios";
    }

    @PostMapping("/{id}/actualizar")
    public String actualizar(
            @PathVariable Long id,
            @RequestParam String nombre,
            @RequestParam String departamento,
            @RequestParam Double latitud,
            @RequestParam Double longitud,
            @RequestParam Integer activo,
            RedirectAttributes redirectAttributes) {

        try {
            boolean actualizado = municipioService.actualizar(id, nombre, departamento,
                                                              latitud, longitud, activo);
            if (actualizado) {
                redirectAttributes.addFlashAttribute("mensaje", "Municipio actualizado correctamente.");
                redirectAttributes.addFlashAttribute("tipo", "success");
            } else {
                redirectAttributes.addFlashAttribute("mensaje", "No se pudo actualizar el municipio.");
                redirectAttributes.addFlashAttribute("tipo", "error");
            }
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("mensaje", e.getMessage());
            redirectAttributes.addFlashAttribute("tipo", "error");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje", "Error inesperado al actualizar el municipio.");
            redirectAttributes.addFlashAttribute("tipo", "error");
        }

        return "redirect:/admin/municipios";
    }

    @PostMapping("/{id}/activar")
    public String activar(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        boolean resultado = municipioService.activar(id);

        if (resultado) {
            redirectAttributes.addFlashAttribute("mensaje", "Municipio activado correctamente.");
            redirectAttributes.addFlashAttribute("tipo", "success");
        } else {
            redirectAttributes.addFlashAttribute("mensaje", "No se encontró el municipio.");
            redirectAttributes.addFlashAttribute("tipo", "error");
        }

        return "redirect:/admin/municipios";
    }

    @PostMapping("/{id}/desactivar")
    public String desactivar(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        boolean resultado = municipioService.desactivar(id);

        if (resultado) {
            redirectAttributes.addFlashAttribute("mensaje", "Municipio desactivado correctamente.");
            redirectAttributes.addFlashAttribute("tipo", "warning");
        } else {
            redirectAttributes.addFlashAttribute("mensaje", "No se encontró el municipio.");
            redirectAttributes.addFlashAttribute("tipo", "error");
        }

        return "redirect:/admin/municipios";
    }
}