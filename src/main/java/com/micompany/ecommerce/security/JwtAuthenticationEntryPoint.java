package com.micompany.ecommerce.security;

import com.micompany.ecommerce.exceptions.ErrorMessage;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.LocalDateTime;

/*
 * Responde HTTP 401 cuando una petición intenta acceder
 * a un recurso protegido sin una autenticación válida.
 *
 * Ejemplos:
 *
 * - No se envió el token.
 * - El token está vencido.
 * - El token está mal formado.
 * - La firma del token no es válida.
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint
        implements AuthenticationEntryPoint {

    /*
     * Convierte ErrorMessage a JSON.
     */
    private final ObjectMapper objectMapper;

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authenticationException
    ) throws IOException {

        // Código HTTP de autenticación requerida.
        response.setStatus(
                HttpServletResponse.SC_UNAUTHORIZED
        );

        response.setContentType(
                MediaType.APPLICATION_JSON_VALUE
        );

        response.setCharacterEncoding("UTF-8");

        /*
         * Utilizamos el mismo ErrorMessage que el resto de la API.
         */
        ErrorMessage errorMessage = new ErrorMessage(
                HttpServletResponse.SC_UNAUTHORIZED,
                "Unauthorized",
                "Authentication is required or the token is invalid",
                LocalDateTime.now(),
                request.getRequestURI()
        );

        objectMapper.writeValue(
                response.getOutputStream(),
                errorMessage
        );
    }
}