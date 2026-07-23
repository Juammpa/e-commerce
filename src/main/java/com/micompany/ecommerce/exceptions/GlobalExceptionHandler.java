package com.micompany.ecommerce.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

/*
 * Intercepta las excepciones producidas por controladores
 * y servicios, y las convierte en respuestas HTTP consistentes.
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /*
     * Maneja los errores producidos por @Valid en los DTOs.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorMessage> handleValidationErrors(MethodArgumentNotValidException exception,HttpServletRequest request
    ) {

        ErrorMessage error = new ErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                "One or more fields contain invalid values",
                LocalDateTime.now(),
                request.getRequestURI()
        );

        return ResponseEntity.badRequest().body(error);
    }

    /*
     * Maneja JSON mal formado, tipos incorrectos, enums inválidos
     * y campos que no existen en el DTO.
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorMessage> handleUnreadableMessage(HttpMessageNotReadableException exception, HttpServletRequest request) {

        return buildError(
                HttpStatus.BAD_REQUEST,
                "The request body is invalid or contains unsupported fields",
                request
        );
    }

    /*
     * Recurso inexistente. --> Ej. Producto inexistente
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorMessage> handleResourceNotFound(ResourceNotFoundException exception, HttpServletRequest request){

        log.debug("Resource not found. path={}, resource={}, field={}, value={}",
                request.getRequestURI(),
                exception.getResourceName(),
                exception.getFieldName(),
                exception.getFieldValue()
        );

        return buildError(
                HttpStatus.NOT_FOUND,
                exception.getMessage(),
                request
        );
    }

    /*
     * Email ya registrado.
     */
    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ErrorMessage> handleEmailAlreadyExists(EmailAlreadyExistsException exception, HttpServletRequest request) {

        return buildError(HttpStatus.CONFLICT, exception.getMessage(), request);
    }

    /*
     * Carrito vacío al intentar crear una orden.
     */
    @ExceptionHandler(EmptyCartException.class)
    public ResponseEntity<ErrorMessage> handleEmptyCart(EmptyCartException exception, HttpServletRequest request) {

        return buildError(HttpStatus.UNPROCESSABLE_CONTENT, exception.getMessage(), request);
    }

    /*
     * Stock insuficiente para completar una operación.
     */
    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<ErrorMessage> handleInsufficientStock(InsufficientStockException exception, HttpServletRequest request) {

        return buildError(HttpStatus.CONFLICT, exception.getMessage(), request);
    }

    /*
     * Cantidad nula, cero o negativa.
     */
    @ExceptionHandler(InvalidQuantityException.class)
    public ResponseEntity<ErrorMessage> handleInvalidQuantity(InvalidQuantityException exception, HttpServletRequest request) {

        return buildError(HttpStatus.BAD_REQUEST, exception.getMessage(), request);
    }

    /*
     * Transición inválida del estado de una orden.
     */
    @ExceptionHandler(InvalidOrderStatusTransitionException.class)
    public ResponseEntity<ErrorMessage> handleInvalidOrderTransition(InvalidOrderStatusTransitionException exception, HttpServletRequest request) {

        return buildError(HttpStatus.CONFLICT, exception.getMessage(), request);
    }

    /*
     * Captura cualquier error no contemplado.
     *
     * El error completo se registra en el servidor, pero la respuesta
     * no expone información interna.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorMessage> handleUnexpectedException(Exception exception, HttpServletRequest request) {

        log.error("Unexpected error processing path={}", request.getRequestURI(), exception);

        return buildError(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected internal error occurred", request);
    }

    /*
     * Captura error de autenticación
     *
     * No revelamos si fue contraseña o email.
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorMessage> handleAuthentication(AuthenticationException exception, HttpServletRequest request) {

        return buildError(HttpStatus.UNAUTHORIZED, "The email or password is incorrect", request);
    }


    private ResponseEntity<ErrorMessage>  buildError(
            HttpStatus status, String message, HttpServletRequest request) {

        ErrorMessage error = new ErrorMessage(
                status.value(),
                status.getReasonPhrase(),
                message,
                LocalDateTime.now(),
                request.getRequestURI()
        );

        return ResponseEntity.status(status).body(error);

    }


}
