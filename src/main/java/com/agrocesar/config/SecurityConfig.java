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
                .requestMatchers(
                    "/", "/login", "/registro",
                    "/css/**", "/js/**", "/images/**",
                    "/error", "/access-denied"
                ).permitAll()
                .requestMatchers("/dashboard", "/cultivos/**", "/alertas/**")
                    .hasRole("AGRICULTOR")
                .requestMatchers("/admin", "/admin/", "/admin/**")
                    .hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            
            .formLogin(form -> form
                .loginPage("/login")
                .successHandler((request, response, authentication) -> {
                    usuarioService.actualizarUltimoLogin(authentication.getName());
                    boolean isAdmin = authentication.getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
                    response.sendRedirect(isAdmin ? "/admin/dashboard" : "/dashboard");
                })
                .failureUrl("/login?error")
                .permitAll()
            )

            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )

            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .maximumSessions(1)
                .maxSessionsPreventsLogin(false)
            )

            .exceptionHandling(ex -> ex
                .accessDeniedPage("/access-denied")
            );

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }
}