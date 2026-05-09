package com.agrocesar.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.session.HttpSessionEventPublisher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(authz -> authz
                //Recursos estáticos y públicos
                .requestMatchers(
                    "/", "/login", "/registro",
                    "/css/**", "/js/**", "/images/**",
                    "/test-publico", "/test-anyrequest"
                    ,"/error", "/access-denied"
                ).permitAll()
                
                //Agricultor
                .requestMatchers(
                    "/dashboard", "/cultivos/**", "/alertas/**", "/test-agricultor"
                ).hasRole("AGRICULTOR")
                
                //Admin
                .requestMatchers("/admin", "/admin/", "/admin/**").hasRole("ADMIN")
                
                //Todo lo demás requiere login
                .anyRequest().authenticated()
            )
            
            //Form Login
            .formLogin(form -> form
                .loginPage("/login")
                .successHandler((request, response, authentication) -> {
                    boolean isAdmin = authentication.getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
                    
                    response.sendRedirect(isAdmin ? "/admin/dashboard" : "/dashboard");
                })
                .failureUrl("/login?error")
                .permitAll()
            )
            
            //Logout
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            )
            
            //Session Management
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .maximumSessions(1)  // 1 dispositivo por usuario
                .maxSessionsPreventsLogin(false)
            )
            
            //Access Denied
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