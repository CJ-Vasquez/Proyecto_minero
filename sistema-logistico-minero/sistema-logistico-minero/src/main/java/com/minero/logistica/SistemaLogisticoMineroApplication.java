package com.minero.logistica;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SistemaLogisticoMineroApplication {

	public static void main(String[] args) {
		SpringApplication.run(SistemaLogisticoMineroApplication.class, args);
        System.out.println("=== SISTEMA LOGÍSTICO MINERO - BACKEND INICIADO ===");
        System.out.println("API disponible en: http://localhost:8080");
        System.out.println("Swagger UI: http://localhost:8080/swagger-ui.html");
	}

}
