package com.micompany.ecommerce.security;

import com.micompany.ecommerce.exceptions.ErrorMessage;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.LocalDateTime;

/*
 * Responde HTTP 403 cuando el usuario está autenticado,
 * pero no posee el rol necesario.
 *
 * Ejemplo:
 *
 * Un CUSTOMER intenta acceder a un endpoint de ADMIN.
 */
@Component
@RequiredArgsConstructor
public class JwtAccessDeniedHandler
        implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException
    ) throws IOException {

        // El usuario está autenticado, pero no está autorizado.
        response.setStatus(
                HttpServletResponse.SC_FORBIDDEN
        );

        response.setContentType(
                MediaType.APPLICATION_JSON_VALUE
        );

        response.setCharacterEncoding("UTF-8");

        /*
         * Utilizamos el mismo formato de error que toda la API.
         */
        ErrorMessage errorMessage = new ErrorMessage(
                HttpServletResponse.SC_FORBIDDEN,
                "Forbidden",
                "You do not have permission to access this resource",
                LocalDateTime.now(),
                request.getRequestURI()
        );

        objectMapper.writeValue(
                response.getOutputStream(),
                errorMessage
        );
    }
}