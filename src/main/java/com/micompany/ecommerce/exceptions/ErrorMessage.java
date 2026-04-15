package com.micompany.ecommerce.exceptions;

public record ErrorMessage(
        int status,
        String message,
        long timestamp
) {}
