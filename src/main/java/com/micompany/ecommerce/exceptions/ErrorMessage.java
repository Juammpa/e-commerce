package com.micompany.ecommerce.exceptions;

import java.time.LocalDateTime;

public record ErrorMessage(

        // Codigo HTTP numerico
        int status,

        // Descripcion estandar del error
        String error,

        // Explicacion especifica del error
        String message,

        // Momento del error
        LocalDateTime timestamp,

        // Endpoint donde ocurrio el error
        String path
) {}
