package com.agrocesar.controller;

import com.agrocesar.service.MunicipioService;
import com.agrocesar.service.UsuarioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    private final UsuarioService usuarioService;
    private final MunicipioService municipioService;

    public AuthController(UsuarioService usuarioService,
                          MunicipioService municipioService) {
        this.usuarioService      = usuarioService;
        this.municipioService = municipioService;
    }

    @GetMapping("/login")
    public String loginPage() {
        return "auth/login";
    }

    @GetMapping("/registro")
    public String registroPage(Model model) {
        model.addAttribute("municipios", municipioService.findAllActivos());
        return "auth/registro";
    }

    @PostMapping("/registro")
    public String registrar(
            @RequestParam String nombre,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam(required = false) Long municipioId,
            @RequestParam(required = false) String telefono,
            Model model,
            RedirectAttributes redirectAttrs) {

        try {
            usuarioService.registrar(nombre, email, password, municipioId, telefono);
            redirectAttrs.addFlashAttribute("registroExitoso",
                    "Cuenta creada. Ya puedes iniciar sesión.");
            return "redirect:/login";

        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("municipios", municipioService.findAllActivos());
            return "auth/registro";
        }
    }

    @GetMapping("/access-denied")
    public String accessDenied() {
        return "auth/access-denied";
    }
}