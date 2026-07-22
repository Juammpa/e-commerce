package com.micompany.ecommerce.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.Instant;

/*
 * Este componente se ejecuta cuando una petición intenta acceder
 * a un recurso protegido sin una autenticación válida.
 *
 * Ejemplos:
 *
 * - La petición no contiene JWT.
 * - El JWT está vencido.
 * - El JWT tiene una firma incorrecta.
 * - El JWT está mal formado.
 *
 * En todos esos casos corresponde responder:
 *
 * HTTP 401 Unauthorized
 */
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    /*
     * Spring Boot proporciona un ObjectMapper configurado.
     *
     * ObjectMapper convierte objetos Java a JSON.
     */
    public JwtAuthenticationEntryPoint(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {

        // Indicamos que la peticion no posee una autenticacion valida.
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        // La respuesta sera enviada en formato JSON
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        // Evita problemas al escribir caracteres especiales
        response.setCharacterEncoding("UTF-8");

        SecurityErrorResponse errorResponse =
                new SecurityErrorResponse(
                        Instant.now(),
                        HttpServletResponse.SC_UNAUTHORIZED,
                        "Unauthorized",
                        "Authentication is required or the token is invalid",
                        request.getRequestURI()
                );

        /*
         * Convertimos SecurityErrorResponse a JSON y lo escribimos
         * directamente en el cuerpo de la respuesta HTTP.
         */
        objectMapper.writeValue(
                response.getOutputStream(),
                errorResponse
        );

    }
}
