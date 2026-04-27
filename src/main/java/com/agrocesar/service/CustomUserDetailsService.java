package com.agrocesar.service;

import com.agrocesar.model.Usuario;
import com.agrocesar.repository.UsuarioRepository;

import org.springframework.context.annotation.Profile;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.time.LocalDateTime;

@Service
@Profile("!nobd")
public class CustomUserDetailsService implements UserDetailsService {
    
    private final UsuarioRepository usuarioRepository;

    public CustomUserDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByEmailAndActivo(email)
            .orElseThrow(() -> new UsernameNotFoundException(
                "Usuario no encontrado o inactivo: " + email));
        
        usuarioRepository.actualizarUltimoLogin(email);
        usuario.setUltimoLogin(LocalDateTime.now());

        //Transforma a formato Spring Security
        return new User(
            usuario.getEmail(),
            usuario.getPasswordHash(),  //BCrypt ya en BD
            List.of(new SimpleGrantedAuthority("ROLE_" + usuario.getRol()))
        );
    }
}