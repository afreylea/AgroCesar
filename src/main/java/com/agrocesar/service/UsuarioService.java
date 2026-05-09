package com.agrocesar.service;

import com.agrocesar.model.Usuario;
import com.agrocesar.repository.MunicipioRepository;
import com.agrocesar.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
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

    public void registrar(String nombre, String email,
                          String passwordPlano, Long municipioId, String telefono) {

        if (nombre == null || nombre.isBlank())
            throw new IllegalArgumentException("El nombre es obligatorio.");

        if (email == null || email.isBlank())
            throw new IllegalArgumentException("El correo es obligatorio.");

        if (!esEmailValido(email)) 
            throw new IllegalArgumentException("Formato de email invalido");

        if(!esTelefonoValido(telefono))
            throw new IllegalArgumentException("Formato de numero de telefono invalido");

        if (passwordPlano == null || passwordPlano.isBlank())
            throw new IllegalArgumentException("La contraseña es obligatoria.");

        if (passwordPlano.length() < 8)
            throw new IllegalArgumentException("La contraseña debe tener al menos 8 caracteres.");

        if (municipioId != null && municipioRepository.findById(municipioId).isEmpty()) 
            throw new IllegalArgumentException("El municipio ingresado no existe");

        if (usuarioRepository.findByEmail(email).isPresent())
            throw new IllegalArgumentException("El correo ya está registrado.");
        
        

        Usuario nuevo = Usuario.builder()
            .nombre(nombre.trim())
            .email(email.trim())
            .passwordHash(passwordEncoder.encode(passwordPlano))
            .rol("AGRICULTOR")
            .municipioId(municipioId)
            .telefono(telefono)
            .build();

        usuarioRepository.insert(nuevo);
    }

    private boolean esEmailValido (String email) {
        String EMAIL_REGEX = 
        "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

        Pattern pattern = Pattern.compile(EMAIL_REGEX);

        return pattern.matcher(email.trim().toLowerCase()).matches();
    }

    private boolean esTelefonoValido (String telefono) {
        String TELEFONO_REGEX = "^\\+?(57)?[0-9]{10}$";

        Pattern pattern = Pattern.compile(TELEFONO_REGEX);

        if(telefono == null)
            return true;

        return pattern.matcher(telefono.trim()).matches();
    }
}