package com.inmobiliaria.apigateway.config;

import org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions;
import org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RequestPredicates;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

// @Configuration indica que esta clase contiene
// configuraciones que Spring carga al arrancar
// Define las rutas del API Gateway — a donde
// redirige cada peticion segun la URL
@Configuration
public class GatewayConfig {

    // RUTA 1 — auth-service
    // Peticiones a /api/v1/auth/** van a auth-service
    // Ejemplo: /api/v1/auth/login → auth-service:8081
    @Bean
    public RouterFunction<ServerResponse> authRoute() {
        return GatewayRouterFunctions.route("auth-service")
                .route(RequestPredicates.path("/api/v1/auth/**"),
                        HandlerFunctions.http())
                .filter((request, next) -> next.handle(request))
                .build();
    }

    // RUTA 2 — usuario-service
    // Peticiones a /api/v1/usuarios/** van a usuario-service
    // Ejemplo: /api/v1/usuarios/1 → usuario-service:8082
    @Bean
    public RouterFunction<ServerResponse> usuarioRoute() {
        return GatewayRouterFunctions.route("usuario-service")
                .route(RequestPredicates.path("/api/v1/usuarios/**"),
                        HandlerFunctions.http())
                .filter((request, next) -> next.handle(request))
                .build();
    }

    // RUTA 3 — propiedad-service
    // Peticiones a /api/v1/propiedades/** van a propiedad-service
    // Ejemplo: /api/v1/propiedades → propiedad-service:8083
    @Bean
    public RouterFunction<ServerResponse> propiedadRoute() {
        return GatewayRouterFunctions.route("propiedad-service")
                .route(RequestPredicates.path("/api/v1/propiedades/**"),
                        HandlerFunctions.http())
                .filter((request, next) -> next.handle(request))
                .build();
    }

    // RUTA 4 — reserva-service
    // Peticiones a /api/v1/reservas/** van a reserva-service
    // Ejemplo: /api/v1/reservas/1/aprobar → reserva-service:8084
    @Bean
    public RouterFunction<ServerResponse> reservaRoute() {
        return GatewayRouterFunctions.route("reserva-service")
                .route(RequestPredicates.path("/api/v1/reservas/**"),
                        HandlerFunctions.http())
                .filter((request, next) -> next.handle(request))
                .build();
    }

    // RUTA 5 — resena-service
    // Peticiones a /api/v1/resenas/** van a resena-service
    // Ejemplo: /api/v1/resenas/propiedad/1 → resena-service:8085
    @Bean
    public RouterFunction<ServerResponse> resenaRoute() {
        return GatewayRouterFunctions.route("resena-service")
                .route(RequestPredicates.path("/api/v1/resenas/**"),
                        HandlerFunctions.http())
                .filter((request, next) -> next.handle(request))
                .build();
    }

    // RUTA 6 — busqueda-service
    // Peticiones a /api/v1/busqueda/** van a busqueda-service
    // Ejemplo: /api/v1/busqueda/disponibles → busqueda-service:8086
    @Bean
    public RouterFunction<ServerResponse> busquedaRoute() {
        return GatewayRouterFunctions.route("busqueda-service")
                .route(RequestPredicates.path("/api/v1/busqueda/**"),
                        HandlerFunctions.http())
                .filter((request, next) -> next.handle(request))
                .build();
    }

    // RUTA 7 — notificacion-service
    // Peticiones a /api/v1/notificaciones/** van a notificacion-service
    // Ejemplo: /api/v1/notificaciones/usuario/1 → notificacion-service:8087
    @Bean
    public RouterFunction<ServerResponse> notificacionRoute() {
        return GatewayRouterFunctions.route("notificacion-service")
                .route(RequestPredicates.path("/api/v1/notificaciones/**"),
                        HandlerFunctions.http())
                .filter((request, next) -> next.handle(request))
                .build();
    }

    // RUTA 8 — imagen-service
    // Peticiones a /api/v1/imagenes/** van a imagen-service
    // Ejemplo: /api/v1/imagenes/entidad/1 → imagen-service:8088
    @Bean
    public RouterFunction<ServerResponse> imagenRoute() {
        return GatewayRouterFunctions.route("imagen-service")
                .route(RequestPredicates.path("/api/v1/imagenes/**"),
                        HandlerFunctions.http())
                .filter((request, next) -> next.handle(request))
                .build();
    }

    // RUTA 9 — reporte-service
    // Peticiones a /api/v1/reportes/** van a reporte-service
    // Ejemplo: /api/v1/reportes/generar → reporte-service:8089
    @Bean
    public RouterFunction<ServerResponse> reporteRoute() {
        return GatewayRouterFunctions.route("reporte-service")
                .route(RequestPredicates.path("/api/v1/reportes/**"),
                        HandlerFunctions.http())
                .filter((request, next) -> next.handle(request))
                .build();
    }
}