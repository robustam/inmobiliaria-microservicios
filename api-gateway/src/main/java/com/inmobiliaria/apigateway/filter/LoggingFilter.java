package com.inmobiliaria.apigateway.filter;



import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


// @Component registra este filtro automaticamente en Spring
// OncePerRequestFilter garantiza que el filtro
// se ejecute una sola vez por peticion
@Component
public class LoggingFilter extends OncePerRequestFilter {

    // Logger para registrar eventos en consola
    private static final Logger log =
            LoggerFactory.getLogger(LoggingFilter.class);

    @Override
    protected void doFilterInternal(
            // Contiene la peticion HTTP entrante
            HttpServletRequest request,
            // Contiene la respuesta HTTP saliente
            HttpServletResponse response,
            // Permite continuar al siguiente filtro
            FilterChain filterChain)
            throws ServletException, IOException {

        // Log de la peticion antes de procesarla
        log.info("REQUEST → Metodo: {} | Ruta: {}",
                request.getMethod(),
                request.getRequestURI());

        // Continua con el siguiente filtro o microservicio
        filterChain.doFilter(request, response);

        // Log de la respuesta despues de procesarla
        log.info("RESPONSE → Status: {}",
                response.getStatus());
    }
}