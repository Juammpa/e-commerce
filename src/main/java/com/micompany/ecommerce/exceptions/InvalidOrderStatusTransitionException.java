package com.micompany.ecommerce.exceptions;

import com.micompany.ecommerce.models.enums.Status;

/*
 * Se lanza cuando se intenta realizar una transición
 * de estado no permitida.
 *
 * Corresponde a HTTP 409 Conflict.
 */
public class InvalidOrderStatusTransitionException extends RuntimeException {

  public InvalidOrderStatusTransitionException(Status currentStatus, Status requestedStatus) {

    super("Order status cannot change from " + currentStatus + " to " + requestedStatus);
  }
}
