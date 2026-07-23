package com.micompany.ecommerce.exceptions;

import lombok.Getter;

/*
 * Se lanza cuando la cantidad solicitada supera
 * el stock disponible.
 *
 * Corresponde a HTTP 409 Conflict.
 */
@Getter
public class InsufficientStockException extends RuntimeException {

    private final Long productId;
    private final Integer requestedQuantity;
    private final Integer availableStock;

    public InsufficientStockException(Long productId, String productName, Integer requestedQuantity, Integer availableStock) {

        super("Insufficient stock for product " + productName + ". Requested: " + requestedQuantity + ", available: " + availableStock);

        this.productId = productId;
        this.requestedQuantity = requestedQuantity;
        this.availableStock = availableStock;
    }
}
