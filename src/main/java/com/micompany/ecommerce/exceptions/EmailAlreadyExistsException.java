package com.micompany.ecommerce.exceptions;

/*
 * Se lanza cuando se intenta registrar un email existente.
 *
 * Corresponde a HTTP 409 Conflict.
 */
public class EmailAlreadyExistsException extends RuntimeException {
    public EmailAlreadyExistsException(String email) {
        super("The email " + email + " is already registered");
    }
}
