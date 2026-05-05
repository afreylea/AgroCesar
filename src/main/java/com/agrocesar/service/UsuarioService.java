package com.agrocesar.service;

import com.agrocesar.model.Usuario;
import com.agrocesar.repository.MunicipioRepository;
import com.agrocesar.repository.UsuarioRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Profile("!nobd")
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final MunicipioRepository municipioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository,
                          PasswordEncoder passwordEncoder,
                          MunicipioRepository municipioRepository) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder   = passwordEncoder;
        this.municipioRepository = municipioRepository;
    }

    public void registrar(Long id, String nombre, String email,
                          String passwordPlano, Long municipioId, String telefono) {

        if (id == null || id <= 0)
            throw new IllegalArgumentException("La cédula es obligatoria.");

        if (nombre == null || nombre.isBlank())
            throw new IllegalArgumentException("El nombre es obligatorio.");

        if (email == null || email.isBlank())
            throw new IllegalArgumentException("El correo es obligatorio.");

        if (passwordPlano == null || passwordPlano.isBlank())
            throw new IllegalArgumentException("La contraseña es obligatoria.");

        if (passwordPlano.length() < 8)
            throw new IllegalArgumentException("La contraseña debe tener al menos 8 caracteres.");

        if (municipioId != null && municipioRepository.findById(municipioId).isEmpty()) 
            throw new IllegalArgumentException("El municipio ingresado no existe");

        if (usuarioRepository.findById(id).isPresent())
            throw new IllegalArgumentException("La cédula ya está registrada.");

        if (usuarioRepository.findByEmail(email).isPresent())
            throw new IllegalArgumentException("El correo ya está registrado.");
        
        

        Usuario nuevo = Usuario.builder()
            .id(id)
            .nombre(nombre.trim())
            .email(email.trim())
            .passwordHash(passwordEncoder.encode(passwordPlano))
            .rol("AGRICULTOR")
            .municipioId(municipioId)
            .telefono(telefono)
            .build();

        usuarioRepository.insert(nuevo);
    }
}