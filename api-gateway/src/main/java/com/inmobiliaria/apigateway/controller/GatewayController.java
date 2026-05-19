
package com.inmobiliaria.apigateway.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

@RestController
public class GatewayController {

    // El cliente HTTP moderno y oficial de Spring Boot 4
    private final RestClient restClient = RestClient.create();

    // Redirecciona todo lo de /api/v1/auth/** hacia el puerto 8081
    @RequestMapping("/api/v1/auth/**")
    public ResponseEntity<?> proxyAuth(HttpServletRequest request, @RequestBody(required = false) Object body) {
        String targetUrl = "http://localhost:8081" + request.getRequestURI();
        return executeProxy(request, targetUrl, body);
    }

    // Redirecciona todo lo de /api/v1/usuarios/** hacia el puerto 8082
    @RequestMapping("/api/v1/usuarios/**")
    public ResponseEntity<?> proxyUsuarios(HttpServletRequest request, @RequestBody(required = false) Object body) {
        String targetUrl = "http://localhost:8082" + request.getRequestURI();
        return executeProxy(request, targetUrl, body);
    }

    // Método maestro que traspasa la petición de forma segura
    private ResponseEntity<?> executeProxy(HttpServletRequest request, String targetUrl, Object body) {
        RestClient.RequestBodySpec requestSpec = restClient
                .method(HttpMethod.valueOf(request.getMethod()))
                .uri(targetUrl)
                .contentType(MediaType.APPLICATION_JSON);

        if (body != null) {
            requestSpec.body(body);
        }

        // Retorna exactamente la respuesta (201, 400, etc.) que mande el microservicio
        return requestSpec.retrieve().toEntity(Object.class);
    }
}