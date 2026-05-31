package com.agrocesar.service;

import com.agrocesar.model.Usuario;
import com.agrocesar.repository.UsuarioRepository;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class PasswordResetService {

    private final UsuarioRepository usuarioRepository;
    private final JavaMailSender mailSender;
    private final PasswordEncoder passwordEncoder;

    public PasswordResetService(UsuarioRepository usuarioRepository,
                                JavaMailSender mailSender,
                                PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.mailSender = mailSender;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Genera un token único, lo guarda en BD y envía el email
     * con el link de recuperación.
     */
    public boolean enviarEmailRecuperacion(String email, String baseUrl) {
        Optional<Usuario> usuario = usuarioRepository.findByEmailAndActivo(email);
        if (usuario.isEmpty()) return false;

        String token = UUID.randomUUID().toString();
        LocalDateTime expiry = LocalDateTime.now().plusHours(1);

        usuarioRepository.guardarResetToken(email, token, expiry);

        String link = baseUrl + "/reset-password?token=" + token;

        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setTo(email);
        mensaje.setSubject("AgroCesar — Recuperación de contraseña");
        mensaje.setText(
                "Hola " + usuario.get().getNombre() + ",\n\n" +
                        "Recibimos una solicitud para restablecer tu contraseña.\n\n" +
                        "Haz clic en el siguiente enlace (válido por 1 hora):\n" +
                        link + "\n\n" +
                        "Si no solicitaste esto, ignora este mensaje.\n\n" +
                        "Equipo AgroCesar"
        );

        mailSender.send(mensaje);
        return true;
    }

    /**
     * Valida el token y actualiza la contraseña.
     */
    public boolean resetearPassword(String token, String nuevaPassword) {
        Optional<Usuario> usuario = usuarioRepository.findByResetToken(token);
        if (usuario.isEmpty()) return false;

        String hash = passwordEncoder.encode(nuevaPassword);
        usuarioRepository.actualizarPassword(usuario.get().getId(), hash);
        return true;
    }
}