package com.inmobiliaria.apigateway.filter; // Subpaquete "filter" dentro del gateway

// ============================================================
// FILTRO GLOBAL DE LOGS - API GATEWAY
// ============================================================
// Un "filtro" en el Gateway es un componente que intercepta
// TODAS las peticiones HTTP antes de redirigirlas y/o después
// de recibir la respuesta. Es como un "middleware".
//
// Este filtro específico registra en el log:
//   ENTRADA: método HTTP y ruta de cada petición
//   SALIDA:  código de respuesta HTTP (200, 404, 500, etc.)
//
// Así puedes ver en la consola todo el tráfico del sistema.
// ============================================================

import org.slf4j.Logger;                           // Interfaz estándar de logging en Java
import org.slf4j.LoggerFactory;                    // Fábrica para crear instancias de Logger
import org.springframework.cloud.gateway.filter.GatewayFilterChain; // Cadena de filtros del Gateway
import org.springframework.cloud.gateway.filter.GlobalFilter;        // Interfaz para filtros globales
import org.springframework.core.Ordered;           // Permite definir el orden de ejecución
import org.springframework.stereotype.Component;   // Marca esta clase como componente Spring
import org.springframework.web.server.ServerWebExchange; // Objeto que contiene request + response
import reactor.core.publisher.Mono;                // Tipo reactivo (para programación asíncrona)

// @Component: Spring detecta esta clase automáticamente y la registra
// como un bean. Al ser GlobalFilter, el Gateway la aplica a TODAS las rutas.
@Component
public class LoggingFilter implements GlobalFilter, Ordered {
    // GlobalFilter: indica que este filtro se aplica a todas las peticiones
    // Ordered: permite definir en qué orden se ejecuta respecto a otros filtros

    // Logger: objeto para escribir mensajes en la consola/archivo de log.
    // LoggerFactory.getLogger(clase) crea un logger asociado a esta clase.
    // Los mensajes aparecen con el nombre de la clase como prefijo.
    private static final Logger log = LoggerFactory.getLogger(LoggingFilter.class);

    // filter() es el método principal que Spring llama para CADA petición HTTP.
    // Parámetros:
    //   exchange = contiene el request (petición) y el response (respuesta)
    //   chain    = la cadena de filtros; llamar chain.filter() pasa al siguiente
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        // Registra en el log antes de redirigir la petición al microservicio.
        // exchange.getRequest().getMethod() = método HTTP (GET, POST, etc.)
        // exchange.getRequest().getURI().getPath() = la ruta, ej: /api/v1/propiedades
        log.info("REQUEST → Metodo: {} | Ruta: {}",
                exchange.getRequest().getMethod(),
                exchange.getRequest().getURI().getPath());

        // chain.filter(exchange) pasa la petición al siguiente paso (el microservicio).
        // .then() ejecuta el código DESPUÉS de que el microservicio responde.
        // Mono.fromRunnable() envuelve código normal en el contexto reactivo.
        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            // Registra el código de estado HTTP de la respuesta.
            // Ejemplo: 200 OK, 400 Bad Request, 404 Not Found, 500 Internal Server Error
            log.info("RESPONSE → Status: {}",
                    exchange.getResponse().getStatusCode());
        }));
    }

    // getOrder() define la PRIORIDAD de este filtro.
    // -1 significa que se ejecuta ANTES que los filtros predeterminados de Spring.
    // Cuanto menor el número, mayor la prioridad (se ejecuta primero).
    @Override
    public int getOrder() {
        return -1;
    }
}