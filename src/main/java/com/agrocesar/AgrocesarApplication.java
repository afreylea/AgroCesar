package com.agrocesar;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;
import org.springframework.boot.jdbc.autoconfigure.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.jdbc.autoconfigure.JdbcTemplateAutoConfiguration;

//TODO Sprint 2: quitar estos exclude cuando Oracle XE esté conectado
@SpringBootApplication(exclude = {
    DataSourceAutoConfiguration.class,
    DataSourceTransactionManagerAutoConfiguration.class,
    JdbcTemplateAutoConfiguration.class
})
public class AgrocesarApplication {

    public static void main(String[] args) {
        SpringApplication.run(AgrocesarApplication.class, args);

		    try {
            java.awt.Desktop.getDesktop().browse(
                new java.net.URI("http://localhost:8080/test-publico")
            );
        } catch (Exception e) {
            // Si falla, simplemente no abre — no afecta la app
        }
    }
}