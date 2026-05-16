package com.inmobiliaria.apigateway.config;

import org.springframework.cloud.gateway.server.mvc.filter.LoadBalancerFilterFunctions;
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
    public RouterFunction<ServerResponse> routes() {
        return GatewayRouterFunctions.route("auth-service")
                .route(RequestPredicates.path("/api/v1/auth/**"), HandlerFunctions.http())
                .filter(LoadBalancerFilterFunctions.lb("auth-service"))

                .route(RequestPredicates.path("/api/v1/usuarios/**"), HandlerFunctions.http())
                .filter(LoadBalancerFilterFunctions.lb("usuario-service"))

                .route(RequestPredicates.path("/api/v1/propiedades/**"), HandlerFunctions.http())
                .filter(LoadBalancerFilterFunctions.lb("propiedad-service"))

                .route(RequestPredicates.path("/api/v1/reservas/**"), HandlerFunctions.http())
                .filter(LoadBalancerFilterFunctions.lb("reserva-service"))

                .route(RequestPredicates.path("/api/v1/resenas/**"), HandlerFunctions.http())
                .filter(LoadBalancerFilterFunctions.lb("resena-service"))

                .route(RequestPredicates.path("/api/v1/busqueda/**"), HandlerFunctions.http())
                .filter(LoadBalancerFilterFunctions.lb("busqueda-service"))

                .route(RequestPredicates.path("/api/v1/notificaciones/**"), HandlerFunctions.http())
                .filter(LoadBalancerFilterFunctions.lb("notificacion-service"))

                .route(RequestPredicates.path("/api/v1/imagenes/**"), HandlerFunctions.http())
                .filter(LoadBalancerFilterFunctions.lb("imagen-service"))

                .route(RequestPredicates.path("/api/v1/reportes/**"), HandlerFunctions.http())
                .filter(LoadBalancerFilterFunctions.lb("reporte-service"))

                .build();
    }
}