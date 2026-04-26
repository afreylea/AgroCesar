package com.agrocesar;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AgrocesarApplication {

	public static void main(String[] args) {
		SpringApplication.run(AgrocesarApplication.class, args);
	}
}