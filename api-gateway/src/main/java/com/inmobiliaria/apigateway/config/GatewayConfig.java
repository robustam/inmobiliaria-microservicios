package com.inmobiliaria.apigateway.config;

import org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions;
import org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RequestPredicates;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;
import java.net.URI; // Importante añadir este import

@Configuration
public class GatewayConfig {

    @Bean
    public RouterFunction<ServerResponse> routes() {
        return GatewayRouterFunctions.route("auth-route")
                .route(RequestPredicates.path("/api/v1/auth/**"), HandlerFunctions.http(URI.create("http://auth-service")))

                .route(RequestPredicates.path("/api/v1/usuarios/**"), HandlerFunctions.http(URI.create("http://usuario-service")))

                .route(RequestPredicates.path("/api/v1/propiedades/**"), HandlerFunctions.http(URI.create("http://propiedad-service")))

                .route(RequestPredicates.path("/api/v1/reservas/**"), HandlerFunctions.http(URI.create("http://reserva-service")))

                .route(RequestPredicates.path("/api/v1/resenas/**"), HandlerFunctions.http(URI.create("http://resena-service")))

                .route(RequestPredicates.path("/api/v1/busqueda/**"), HandlerFunctions.http(URI.create("http://busqueda-service")))

                .route(RequestPredicates.path("/api/v1/notificaciones/**"), HandlerFunctions.http(URI.create("http://notificacion-service")))

                .route(RequestPredicates.path("/api/v1/imagenes/**"), HandlerFunctions.http(URI.create("http://imagen-service")))

                .route(RequestPredicates.path("/api/v1/reportes/**"), HandlerFunctions.http(URI.create("http://reporte-service")))

                .build();
    }
}