package com.micompany.ecommerce.security;

import java.time.Instant;

/*
 * Representa el formato JSON utilizado para los errores
 * producidos por Spring Security.
 *
 * Ejemplo:
 *
 * {
 *   "timestamp": "2026-07-21T14:30:00Z",
 *   "status": 401,
 *   "error": "Unauthorized",
 *   "message": "Authentication is required",
 *   "path": "/api/cart"
 * }
 */
public record SecurityErrorResponse (

        // Momento exacto en el que ocurrió el error.
        Instant timestamp,

        // Código HTTP numérico, por ejemplo 401 o 403.
        int status,

        // Nombre general del error HTTP.
        String error,

        // Explicación comprensible del problema.
        String message,

        // Endpoint solicitado.
        String path
) {}
