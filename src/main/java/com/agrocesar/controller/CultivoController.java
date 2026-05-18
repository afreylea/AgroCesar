package com.agrocesar.controller;

import com.agrocesar.model.CultivoAgricultor;
import com.agrocesar.model.Usuario;
import com.agrocesar.service.CatalogoService;
import com.agrocesar.service.MunicipioService;
import com.agrocesar.service.UsuarioService;
import com.agrocesar.service.CultivoAgricultorService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.Optional;

@Controller
@RequestMapping("/cultivos")
public class CultivoController {

    private final CultivoAgricultorService cultivoService;
    private final CatalogoService       catalogoService;
    private final MunicipioService      municipioService;
    private final UsuarioService        usuarioService;

    public CultivoController(CultivoAgricultorService cultivoService,
                             CatalogoService catalogoService,
                             MunicipioService municipioService,
                             UsuarioService usuarioService) {

        this.cultivoService   = cultivoService;
        this.catalogoService  = catalogoService;
        this.municipioService = municipioService;
        this.usuarioService   = usuarioService;
    }

    private Usuario getUsuarioActual(Authentication auth) {
        return usuarioService.findByEmail(auth.getName())
                .orElseThrow(() -> new IllegalStateException(
                        "Usuario autenticado no encontrado en BD."));
    }

    @GetMapping("/activos")
    public String activos(Model model, Authentication auth) {
        Usuario usuario = getUsuarioActual(auth);

        model.addAttribute("cultivos",
                cultivoService.obtenerActivosPorUsuario(usuario.getId()));

        return "cultivos/activos";
    }

    @GetMapping("/inactivos")
    public String inactivos(Model model, Authentication auth) {
        Usuario usuario = getUsuarioActual(auth);

        model.addAttribute("cultivos",
                cultivoService.obtenerInactivosPorUsuario(usuario.getId()));

        return "cultivos/inactivos";
    }

    @GetMapping("/nuevo")
    public String nuevoForm(Model model) {
        model.addAttribute("catalogos",  catalogoService.listarActivos());
        model.addAttribute("municipios", municipioService.findAllActivos());

        return "cultivos/nuevo";
    }

    @PostMapping("/nuevo")
    public String registrar(@RequestParam Long      catalogoId,
                            @RequestParam Long      municipioId,
                            @RequestParam double    hectareas,
                            @RequestParam LocalDate fechaSiembra,
                            @RequestParam(required = false) Double latitudCultivo,
                            @RequestParam(required = false) Double longitudCultivo,
                            @RequestParam(required = false) String tipoSuelo,
                            Authentication auth,
                            RedirectAttributes redirectAttrs) {
        try {
            Usuario usuario = getUsuarioActual(auth);

            cultivoService.registrar(
                    usuario.getId(), catalogoId, municipioId,
                    hectareas, fechaSiembra,
                    latitudCultivo, longitudCultivo, tipoSuelo);

            redirectAttrs.addFlashAttribute("exito", "Cultivo registrado correctamente.");

            return "redirect:/cultivos/activos";
        } catch (IllegalArgumentException e) {
            redirectAttrs.addFlashAttribute("error", e.getMessage());

            return "redirect:/cultivos/nuevo";
        } catch (RuntimeException e) {
            redirectAttrs.addFlashAttribute("error", "Error interno al registrar el cultivo.");

            return "redirect:/cultivos/nuevo";
        }
    }

    @GetMapping("/editar/{id}")
    public String editarForm(@PathVariable Long id,
                             Model model,
                             Authentication auth,
                             RedirectAttributes redirectAttrs) {

        Usuario usuario = getUsuarioActual(auth);

        Optional<CultivoAgricultor> cultivoOpt = cultivoService.obtenerPorId(id);

        if (cultivoOpt.isEmpty() || !cultivoOpt.get().getUsuarioId().equals(usuario.getId())) {
            redirectAttrs.addFlashAttribute("error", "Cultivo no encontrado.");
            
            return "redirect:/cultivos/activos";
        }

        model.addAttribute("cultivo",    cultivoOpt.get());
        model.addAttribute("municipios", municipioService.findAllActivos());

        return "cultivos/editar";
    }

    @PostMapping("/editar/{id}")
    public String actualizar(@PathVariable long   id,
                             @RequestParam double hectareas,
                             @RequestParam(required = false) Double tempMinOverride,
                             @RequestParam(required = false) Double tempMaxOverride,
                             @RequestParam(required = false) Double lluviaMinOverride,
                             @RequestParam(required = false) Double lluviaMaxOverride,
                             @RequestParam(required = false) Double humedadMinOverride,
                             @RequestParam(required = false) Double humedadMaxOverride,
                             @RequestParam(required = false) Double latitudCultivo,
                             @RequestParam(required = false) Double longitudCultivo,
                             @RequestParam(required = false) String tipoSuelo,
                             Authentication auth,
                             RedirectAttributes redirectAttrs) {
        try {
            Usuario usuario = getUsuarioActual(auth);

            cultivoService.actualizar(
                    id, usuario.getId(), hectareas,
                    tempMinOverride, tempMaxOverride,
                    lluviaMinOverride, lluviaMaxOverride,
                    humedadMinOverride, humedadMaxOverride,
                    latitudCultivo, longitudCultivo, tipoSuelo);

            redirectAttrs.addFlashAttribute("exito", "Cultivo actualizado correctamente.");

            return "redirect:/cultivos/activos";
        } catch (IllegalArgumentException e) {
            redirectAttrs.addFlashAttribute("error", e.getMessage());

            return "redirect:/cultivos/editar/" + id;
        } catch (RuntimeException e) {
            redirectAttrs.addFlashAttribute("error", "Error interno al actualizar el cultivo.");

            return "redirect:/cultivos/editar/" + id;
        }
    }

    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id,
                           Authentication auth,
                           RedirectAttributes redirectAttrs) {
        try {
            Usuario usuario = getUsuarioActual(auth);

            cultivoService.eliminar(id, usuario.getId());
            
            redirectAttrs.addFlashAttribute("exito", "Cultivo eliminado correctamente.");
        } catch (IllegalArgumentException e) {
            redirectAttrs.addFlashAttribute("error", e.getMessage());
        } catch (RuntimeException e) {
            redirectAttrs.addFlashAttribute("error", "Error interno al eliminar el cultivo.");
        }
        return "redirect:/cultivos/activos";
    }
}