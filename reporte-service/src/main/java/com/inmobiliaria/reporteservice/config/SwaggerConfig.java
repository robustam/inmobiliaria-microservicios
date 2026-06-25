package com.inmobiliaria.reporteservice.config;

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
                        .title("Reporte Service API")
                        .description("Generación y consulta de reportes estadísticos: propiedades más reservadas, ingresos, etc.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Inmobiliaria Microservicios")
                                .email("soporte@inmobiliaria.cl")))
                .servers(List.of(
                        new Server().url("http://localhost:8085").description("Local"),
                        new Server().url("http://localhost:8080").description("API Gateway")));
    }
}