package com.agrocesar.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.agrocesar.model.CultivoAgricultor;
import com.agrocesar.model.Usuario;
import com.agrocesar.repository.CatalogoRepository;
import com.agrocesar.repository.MunicipioRepository;
import com.agrocesar.service.CultivoAgricultorService;
import com.agrocesar.service.UsuarioService;

@Controller
@RequestMapping("/cultivos")
public class CultivoController {
    private final CultivoAgricultorService cultivoService;
    private final UsuarioService usuarioService;
    private final CatalogoRepository catalogoRepository;
    private final MunicipioRepository municipioRepository;

    public CultivoController(CultivoAgricultorService cultivoService,
            UsuarioService usuarioService,
            CatalogoRepository catalogoRepository,
            MunicipioRepository municipioRepository) {
        this.cultivoService = cultivoService;
        this.usuarioService = usuarioService;
        this.catalogoRepository = catalogoRepository;
        this.municipioRepository = municipioRepository;
    }

    // GET / cultivos - lista de cultivos pára agricultores autenticados
    @GetMapping
    public String lista(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        Usuario usuario = usuarioService.buscarPorEmail(userDetails.getUsername());
        model.addAttribute("cultivos", cultivoService.listarPorUsuario(usuario.getId()));
        return "cultivos/lista";
    }

    // GET / cultivos/nuevo - muestra el formulario de registro
    @GetMapping("/nuevo")
    public String formularioNuevo(Model model) {
        model.addAttribute("cultivo", new CultivoAgricultor());
        // Catolog y municipios necesarios para los selectores del formulario
        model.addAttribute("catalogo", catalogoRepository.findAllActivos());
        model.addAttribute("municipios", municipioRepository.findAllActivos());
        return "cultivos/nuevo";
    }

    // POST /cultivos/nuevo - procesa el formulario de registro
    @PostMapping("/nuevo")
    public String registrar(@ModelAttribute CultivoAgricultor cultivo,
            @AuthenticationPrincipal UserDetails userDetails, RedirectAttributes redirectAttributes) {
        try {
            Usuario usuario = usuarioService.buscarPorEmail(userDetails.getUsername());
            cultivo.setUsuarioId(usuario.getId());
            cultivoService.registrar(cultivo);
            redirectAttributes.addFlashAttribute("exito", "Cultivo registrado exitosamente");
            return "redirect:/cultivos";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/cultivos";
        }
    }

    // POST /cultivos/{id}/eliminar - baja logica del cultivo
    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes) {
        try {
            Usuario usuario = usuarioService.buscarPorEmail(userDetails.getUsername());
            cultivoService.eliminar(id, usuario.getId());
            redirectAttributes.addFlashAttribute("exito", "Cultivo eliminado correctamente");

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());

        }
        return "redirect:/cultivos";
    }

}
