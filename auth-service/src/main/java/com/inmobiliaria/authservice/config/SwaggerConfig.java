package com.inmobiliaria.authservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    private static final String SCHEME_NAME = "bearerAuth";

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Auth Service API")
                        .description("Autenticación y autorización JWT: login, registro y validación de tokens")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Inmobiliaria Microservicios")
                                .email("soporte@inmobiliaria.cl")))
                .servers(List.of(
                        new Server().url("http://localhost:8081").description("Local"),
                        new Server().url("http://localhost:8080").description("API Gateway")))
                .addSecurityItem(new SecurityRequirement().addList(SCHEME_NAME))
                .schemaRequirement(SCHEME_NAME, new SecurityScheme()
                        .name(SCHEME_NAME)
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                        .description("Token JWT obtenido en /api/v1/auth/login"));
    }
}