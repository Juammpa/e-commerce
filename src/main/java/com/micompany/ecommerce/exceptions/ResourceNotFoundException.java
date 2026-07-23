package com.micompany.ecommerce.exceptions;

import lombok.Getter;

/*
 * Representa cualquier recurso que no pudo encontrarse.
 *
 * Ejemplos:
 *
 * Product not found with id: '5'
 * User not found with email: 'user@email.com'
 */
@Getter
public class ResourceNotFoundException extends RuntimeException {

    private final String resourceName;
    private final String fieldName;
    private final Object fieldValue;

    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {

        super(String.format("%s with %s %s not found", resourceName, fieldName, fieldValue));

        this.resourceName = resourceName;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }
}
