package com.micompany.ecommerce.exceptions;

/*
 * Se lanza cuando una cantidad es nula, cero o negativa.
 *
 * Corresponde a HTTP 400 Bad Request.
 */
public class InvalidQuantityException extends RuntimeException {

    public InvalidQuantityException(Integer quantity) {
        super("Quantity must be greater than zero. Received: " + quantity);
    }
}
