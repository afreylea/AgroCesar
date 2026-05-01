package com.agrocesar.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SecurityTestController {
    
    @GetMapping("/test-publico")
    public String publico() {
        return "✅ PÚBLICO OK (permitAll)";
    }
    
    @GetMapping("/test-agricultor")
    public String agricultor(Authentication auth) {
        return "❌ REQUIERE AGRICULTOR (sin login → 302)";
    }
    
    @GetMapping("/test-admin") 
    public String admin(Authentication auth) {
        return "❌ REQUIERE ADMIN (sin login → 302)";
    }
    
    @GetMapping("/test-anyrequest")
    public String anyRequest() {
        return "❌ PROTEGIDO por .anyRequest().authenticated()";
    }
}