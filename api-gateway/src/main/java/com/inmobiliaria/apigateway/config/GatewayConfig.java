
package com.inmobiliaria.apigateway.config;

import org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions;
import org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RequestPredicates;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

@Configuration
public class GatewayConfig {

    @Bean
    public RouterFunction<ServerResponse> authRoute() {
        return GatewayRouterFunctions.route("auth-service")
                .route(RequestPredicates.path("/api/v1/auth/**"),
                        HandlerFunctions.http("lb://auth-service"))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> usuarioRoute() {
        return GatewayRouterFunctions.route("usuario-service")
                .route(RequestPredicates.path("/api/v1/usuarios/**"),
                        HandlerFunctions.http("lb://usuario-service"))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> propiedadRoute() {
        return GatewayRouterFunctions.route("propiedad-service")
                .route(RequestPredicates.path("/api/v1/propiedades/**"),
                        HandlerFunctions.http("lb://propiedad-service"))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> reservaRoute() {
        return GatewayRouterFunctions.route("reserva-service")
                .route(RequestPredicates.path("/api/v1/reservas/**"),
                        HandlerFunctions.http("lb://reserva-service"))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> resenaRoute() {
        return GatewayRouterFunctions.route("resena-service")
                .route(RequestPredicates.path("/api/v1/resenas/**"),
                        HandlerFunctions.http("lb://resena-service"))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> busquedaRoute() {
        return GatewayRouterFunctions.route("busqueda-service")
                .route(RequestPredicates.path("/api/v1/busqueda/**"),
                        HandlerFunctions.http("lb://busqueda-service"))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> notificacionRoute() {
        return GatewayRouterFunctions.route("notificacion-service")
                .route(RequestPredicates.path("/api/v1/notificaciones/**"),
                        HandlerFunctions.http("lb://notificacion-service"))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> imagenRoute() {
        return GatewayRouterFunctions.route("imagen-service")
                .route(RequestPredicates.path("/api/v1/imagenes/**"),
                        HandlerFunctions.http("lb://imagen-service"))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> reporteRoute() {
        return GatewayRouterFunctions.route("reporte-service")
                .route(RequestPredicates.path("/api/v1/reportes/**"),
                        HandlerFunctions.http("lb://reporte-service"))
                .build();
    }
}