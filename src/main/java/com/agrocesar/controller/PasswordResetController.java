package com.agrocesar.controller;

import com.agrocesar.service.PasswordResetService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PasswordResetController {

    private final PasswordResetService passwordResetService;

    public PasswordResetController(PasswordResetService passwordResetService) {
        this.passwordResetService = passwordResetService;
    }

    @GetMapping("/forgot-password")
    public String forgotPasswordForm() {
        return "auth/forgot-password";
    }

    @PostMapping("/forgot-password")
    public String forgotPasswordSubmit(@RequestParam String email,
                                       HttpServletRequest request,
                                       Model model) {
        String baseUrl = request.getScheme() + "://" + request.getServerName()
                + ":" + request.getServerPort();

        boolean enviado = passwordResetService.enviarEmailRecuperacion(email, baseUrl);

        if (enviado) {
            model.addAttribute("mensaje", "Te enviamos un email con las instrucciones.");
        } else {
            model.addAttribute("error", "No encontramos una cuenta con ese email.");
        }

        return "auth/forgot-password";
    }

    @GetMapping("/reset-password")
    public String resetPasswordForm(@RequestParam String token, Model model) {
        model.addAttribute("token", token);
        return "auth/reset-password";
    }

    @PostMapping("/reset-password")
    public String resetPasswordSubmit(@RequestParam String token,
                                      @RequestParam String password,
                                      @RequestParam String confirmPassword,
                                      Model model) {
        if (!password.equals(confirmPassword)) {
            model.addAttribute("token", token);
            model.addAttribute("error", "Las contraseñas no coinciden.");
            return "auth/reset-password";
        }

        if (password.length() < 8) {
            model.addAttribute("token", token);
            model.addAttribute("error", "La contraseña debe tener mínimo 8 caracteres.");
            return "auth/reset-password";
        }

        boolean reseteado = passwordResetService.resetearPassword(token, password);

        if (reseteado) {
            return "redirect:/login?passwordReseteado";
        } else {
            model.addAttribute("token", token);
            model.addAttribute("error", "El enlace expiró o no es válido.");
            return "auth/reset-password";
        }
    }
}