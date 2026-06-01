package com.agrocesar.config;

import com.agrocesar.service.CustomUserDetailsService;
import com.agrocesar.service.UsuarioService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.session.HttpSessionEventPublisher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;
    private final UsuarioService usuarioService;
    private final PasswordEncoder passwordEncoder;

    public SecurityConfig(CustomUserDetailsService customUserDetailsService,
            UsuarioService usuarioService,
            PasswordEncoder passwordEncoder) {
        this.customUserDetailsService = customUserDetailsService;
        this.usuarioService = usuarioService;
        this.passwordEncoder = passwordEncoder;
    }

    // Proveedor de autenticacion: delega en CustomUserDetailsService
    // para cargar el usuario desde Oracle y verificar la contrasena con BCrypt
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(customUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authenticationProvider(authenticationProvider())
                .authorizeHttpRequests(authz -> authz

                        // Rutas publicas: login, registro, recursos estaticos
                        .requestMatchers(
                                "/", "/login", "/registro",
                                "/forgot-password", "/reset-password",
                                "/css/**", "/js/**", "/images/**", "/imagenes/**",
                                "/error", "/access-denied")
                        .permitAll()

                        // API REST interna: usada por Alpine.js via fetch desde el navegador
                        // Requiere sesion activa de cualquier rol autenticado
                        // Se declara antes que las rutas de vista para evitar conflictos
                        .requestMatchers("/api/**")
                        .hasAnyRole("AGRICULTOR", "ADMIN")

                        // Rutas del agricultor
                        .requestMatchers("/dashboard", "/cultivos/**", "/alertas/**")
                        .hasRole("AGRICULTOR")

                        // Rutas del administrador
                        .requestMatchers("/admin", "/admin/", "/admin/**")
                        .hasRole("ADMIN")

                        // Cualquier otra ruta requiere autenticacion
                        .anyRequest().authenticated())

                // Formulario de login personalizado con redireccion por rol
                .formLogin(form -> form
                        .loginPage("/login")
                        .successHandler((request, response, authentication) -> {
                            usuarioService.actualizarUltimoLogin(authentication.getName());
                            boolean isAdmin = authentication.getAuthorities().stream()
                                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
                            response.sendRedirect(isAdmin ? "/admin/dashboard" : "/dashboard");
                        })
                        .failureUrl("/login?error")
                        .permitAll())

                // Logout: invalida sesion y elimina cookie JSESSIONID
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll())

                // Control de sesiones concurrentes: un usuario activo a la vez
                // maxSessionsPreventsLogin(false) expulsa la sesion anterior
                // en lugar de bloquear el nuevo login
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                        .maximumSessions(1)
                        .maxSessionsPreventsLogin(false))

                // Redireccion personalizada al intentar acceder sin permisos
                .exceptionHandling(ex -> ex
                        .accessDeniedPage("/access-denied"));

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    // Necesario para que Spring Security detecte eventos de sesion HTTP
    // como creacion y destruccion, usado por el control de sesiones concurrentes
    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }
}