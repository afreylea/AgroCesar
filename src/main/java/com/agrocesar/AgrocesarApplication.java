package com.agrocesar;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AgrocesarApplication {

    public static void main(String[] args) {
        SpringApplication.run(AgrocesarApplication.class, args);

		    try {
            java.awt.Desktop.getDesktop().browse(
                new java.net.URI("http://localhost:8080/login")
            );
        } catch (Exception e) {
            // Si falla, simplemente no abre — no afecta la app
        }
    }
}