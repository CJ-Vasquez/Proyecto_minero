package com.minero.logistica.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API Sistema Logístico Minero - Tauro S.A.C.")
                        .version("1.0.0")
                        .description("API REST para la gestión logística de la empresa minera Tauro S.A.C.\n\n" +
                                     "Módulos disponibles:\n" +
                                     "- Gestión de Compras (CUS01-CUS10)\n" +
                                     "- Gestión de Almacenamiento (CUS11-CUS20)\n" +
                                     "- Gestión de Seguridad (CUS21-CUS23)")
                        .contact(new Contact()
                                .name("Luis Hider Manco Berrocal")
                                .email("lmanco@cibertec.edu.pe"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")))
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                .components(new Components()
                        .addSecuritySchemes("Bearer Authentication", new SecurityScheme()
                                .name("Authorization")
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Ingrese el token JWT en el formato: Bearer {token}")));
    }
}