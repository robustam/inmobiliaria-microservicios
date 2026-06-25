package com.inmobiliaria.busquedaservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Búsqueda Service API")
                        .description("Servicio de búsqueda y agregación de propiedades con filtros avanzados por región, tipo y precio")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Inmobiliaria Microservicios")
                                .email("soporte@inmobiliaria.cl")))
                .servers(List.of(
                        new Server().url("http://localhost:8089").description("Local"),
                        new Server().url("http://localhost:8080").description("API Gateway")));
    }
}