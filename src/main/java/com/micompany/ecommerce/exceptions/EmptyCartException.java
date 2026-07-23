package com.micompany.ecommerce.exceptions;

/*
 * Se lanza cuando se intenta crear una orden
 * utilizando un carrito vacío.
 *
 * Corresponde a HTTP 422 Unprocessable Entity.
 */
public class EmptyCartException extends RuntimeException {

    public EmptyCartException() {
        super("The cart does not contain any items");
    }
}
