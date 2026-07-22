package com.micompany.ecommerce.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.Instant;

/*
 * Este componente se ejecuta cuando el usuario está autenticado,
 * pero no posee el rol necesario para utilizar un endpoint.
 *
 * Ejemplo:
 *
 * - Un CUSTOMER intenta crear un producto.
 * - El endpoint requiere el rol ADMIN.
 *
 * En ese caso corresponde responder:
 *
 * HTTP 403 Forbidden
 */
@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    public JwtAccessDeniedHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {

        // El usuario esta autenticado, pero no tiene autorizacion.
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);

        // La respuesta se enviara como JSON
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        SecurityErrorResponse errorResponse =
                new SecurityErrorResponse(
                        Instant.now(),
                        HttpServletResponse.SC_FORBIDDEN,
                        "Forbidden",
                        "You do not have permission to access this resource",
                        request.getRequestURI()
                );

        // Convertimos el objeto en JSON y escribimos la respuesta.
        objectMapper.writeValue(
                response.getOutputStream(),
                errorResponse
        );
    }
}
