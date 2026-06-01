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
import org.springframework.mail.javamail.MimeMessageHelper;
import jakarta.mail.internet.MimeMessage;

@Service
public class PasswordResetService {

    private final UsuarioRepository usuarioRepository;
    private final JavaMailSender mailSender;
    private final PasswordEncoder passwordEncoder;
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(PasswordResetService.class);

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
        if (usuario.isEmpty())
            return false;

        String token = UUID.randomUUID().toString();
        LocalDateTime expiry = LocalDateTime.now().plusHours(1);

        usuarioRepository.guardarResetToken(email, token, expiry);

        String link = baseUrl + "/reset-password?token=" + token;
        String nombre = usuario.get().getNombre();

        String html = """
                <!DOCTYPE html>
                <html lang="es">
                <head><meta charset="UTF-8"><meta name="viewport" content="width=device-width,initial-scale=1.0"></head>
                <body style="margin:0;padding:0;background:#f1f5f1;font-family:'Helvetica Neue',Helvetica,Arial,sans-serif;">

                  <table width="100%%" cellpadding="0" cellspacing="0" style="background:#f1f5f1;padding:40px 0;">
                    <tr>
                      <td align="center">
                        <table width="600" cellpadding="0" cellspacing="0" style="max-width:600px;width:100%%;">

                          <!-- Header verde -->
                          <tr>
                            <td style="background:linear-gradient(135deg,#052e16 0%%,#14532d 100%%);border-radius:16px 16px 0 0;padding:36px 40px;text-align:center;">
                              <div style="display:inline-flex;align-items:center;gap:12px;">
                                <div style="background:rgba(255,255,255,0.15);border:1px solid rgba(255,255,255,0.2);border-radius:12px;width:48px;height:48px;display:inline-flex;align-items:center;justify-content:center;">
                                  <img src="cid:logoAgrocesar" alt="Agrocesar" style="width:40px;height:40px;object-fit:contain;border-radius:8px;">
                                </div>
                                <span style="color:#ffffff;font-size:22px;font-weight:900;letter-spacing:-0.5px;">Agrocesar</span>
                              </div>
                              <p style="color:#86efac;font-size:12px;font-weight:600;letter-spacing:2px;text-transform:uppercase;margin:12px 0 0;">Sistema de Monitoreo de Cultivos</p>
                            </td>
                          </tr>

                          <!-- Cuerpo -->
                          <tr>
                            <td style="background:#ffffff;padding:40px;">

                              <!-- Icono central -->
                              <div style="text-align:center;margin-bottom:24px;">
                                <div style="display:inline-block;background:#f0fdf4;border:1px solid #bbf7d0;border-radius:50%%;width:64px;height:64px;line-height:64px;font-size:28px;">🔑</div>
                              </div>

                              <h1 style="color:#052e16;font-size:22px;font-weight:900;text-align:center;margin:0 0 8px;">Recupera tu contraseña</h1>
                              <p style="color:#6b7280;font-size:14px;text-align:center;margin:0 0 28px;">Recibimos una solicitud para restablecer el acceso a tu cuenta.</p>

                              <!-- Saludo -->
                              <p style="color:#374151;font-size:15px;margin:0 0 20px;">Hola <strong style="color:#052e16;">%s</strong>,</p>
                              <p style="color:#6b7280;font-size:14px;line-height:1.6;margin:0 0 28px;">
                                Alguien solicitó restablecer la contraseña de tu cuenta en Agrocesar. Si fuiste tú, haz clic en el botón a continuación. El enlace es válido por <strong style="color:#052e16;">1 hora</strong>.
                              </p>

                              <!-- Botón CTA -->
                              <div style="text-align:center;margin:0 0 28px;">
                                <a href="%s"
                                   style="display:inline-block;background:#15803d;color:#ffffff;text-decoration:none;font-size:15px;font-weight:700;padding:14px 36px;border-radius:12px;letter-spacing:0.3px;">
                                  Restablecer contraseña →
                                </a>
                              </div>

                              <!-- Enlace alternativo -->
                              <div style="background:#f8fafc;border:1px solid #e2e8f0;border-radius:12px;padding:16px;margin-bottom:28px;">
                                <p style="color:#9ca3af;font-size:12px;margin:0 0 6px;">O copia este enlace en tu navegador:</p>
                                <p style="color:#16a34a;font-size:12px;word-break:break-all;margin:0;">%s</p>
                              </div>

                              <!-- Aviso seguridad -->
                              <div style="background:#fefce8;border:1px solid #fde68a;border-radius:12px;padding:16px;margin-bottom:28px;display:flex;gap:12px;">
                                <span style="font-size:18px;flex-shrink:0;">⚠️</span>
                                <p style="color:#92400e;font-size:13px;margin:0;line-height:1.5;">
                                  Si <strong>no solicitaste</strong> este cambio, ignora este mensaje. Tu contraseña no cambiará y el enlace expirará automáticamente.
                                </p>
                              </div>

                              <p style="color:#9ca3af;font-size:13px;margin:0;">Con gusto,<br><strong style="color:#374151;">Equipo Agrocesar</strong></p>
                            </td>
                          </tr>

                          <!-- Footer -->
                          <tr>
                            <td style="background:#f8fafc;border-radius:0 0 16px 16px;padding:24px 40px;border-top:1px solid #e5e7eb;text-align:center;">
                              <p style="color:#9ca3af;font-size:12px;margin:0 0 4px;">
                                🌱 Agrocesar · Sistema de Monitoreo de Cultivos y Alertas Climáticas
                              </p>
                              <p style="color:#d1d5db;font-size:11px;margin:0;">Cesar, Colombia © 2026</p>
                            </td>
                          </tr>

                        </table>
                      </td>
                    </tr>
                  </table>

                </body>
                </html>
                """
                .formatted(nombre, link, link);

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setTo(email);
            helper.setSubject("AgroCesar — Recuperación de contraseña");
            helper.setText(html, true);

            // Logo embebido como CID
            org.springframework.core.io.ClassPathResource logo = new org.springframework.core.io.ClassPathResource(
                    "static/images/logo-agrocesar.png");
            helper.addInline("logoAgrocesar", logo);

            mailSender.send(mimeMessage);
        } catch (Exception e) {
            log.error("Error enviando email de recuperacion: {}", e.getMessage());
            return false;
        }

        return true;
    }

    /**
     * Valida el token y actualiza la contraseña.
     */
    public boolean resetearPassword(String token, String nuevaPassword) {
        Optional<Usuario> usuario = usuarioRepository.findByResetToken(token);
        if (usuario.isEmpty())
            return false;

        String hash = passwordEncoder.encode(nuevaPassword);
        usuarioRepository.actualizarPassword(usuario.get().getId(), hash);
        return true;
    }

}