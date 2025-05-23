package com.intcomex.rest.api.exception;

import com.intcomex.rest.api.dto.ErrorResponse;
import com.intcomex.rest.api.util.DateTimeUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.NoHandlerFoundException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // Exception general
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllUnhandledExceptions(Exception ex, HttpServletRequest request) {
        String msg = "Error interno del servidor";
        if (ex instanceof NoHandlerFoundException) {
            msg = "Recurso no encontrado";
            log.debug(msg, ex);
            return buildErrorResponse(msg, HttpStatus.NOT_FOUND, request.getRequestURI());
        }
        log.error(msg, ex);
        return buildErrorResponse(msg, HttpStatus.INTERNAL_SERVER_ERROR, request.getRequestURI());
    }

    // Exceptions personalizadas
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        log.debug(ex.getMessage(), ex);
        return buildErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND, request.getRequestURI());
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusiness(BusinessException ex, HttpServletRequest request) {
        log.debug(ex.getMessage(), ex);
        return buildErrorResponse(ex.getMessage(), HttpStatus.CONFLICT, request.getRequestURI());
    }

    @ExceptionHandler(ImagenFormatException.class)
    public ResponseEntity<ErrorResponse> handleBusiness(ImagenFormatException ex, HttpServletRequest request) {
        log.debug(ex.getMessage(), ex);
        return buildErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST, request.getRequestURI());
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorResponse> handleBusiness(NoHandlerFoundException ex, HttpServletRequest request) {
        log.debug(ex.getMessage(), ex);
        return buildErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND, request.getRequestURI());
    }

    // Validaciones
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(MethodArgumentNotValidException ex, HttpServletRequest request) {
        StringBuilder message = new StringBuilder("Errores de validación: ");
        ex.getBindingResult().getFieldErrors().forEach(error ->
                message.append(String.format("[%s: %s] ", error.getField(), error.getDefaultMessage()))
        );
        log.debug(message.toString(), ex);
        return buildErrorResponse(message.toString().trim(), HttpStatus.BAD_REQUEST, request.getRequestURI());
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ErrorResponse> handleBindException(BindException ex, HttpServletRequest request) {
        StringBuilder message = new StringBuilder("Errores de validación: ");
        ex.getBindingResult().getFieldErrors().forEach(error ->
                message.append(String.format("[%s: %s] ", error.getField(), error.getDefaultMessage()))
        );
        log.debug(message.toString(), ex);
        return buildErrorResponse(message.toString().trim(), HttpStatus.BAD_REQUEST, request.getRequestURI());
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(BadCredentialsException ex, HttpServletRequest request) {
        String msg = "Usuario o contraseña inválidos";
        log.debug(msg, ex);
        return buildErrorResponse(msg, HttpStatus.UNAUTHORIZED, request.getRequestURI());
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthException(AuthenticationException ex, HttpServletRequest request) {
        log.debug(ex.getMessage(), ex);
        return buildErrorResponse(ex.getMessage(), HttpStatus.UNAUTHORIZED, request.getRequestURI());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex, HttpServletRequest request) {
        String msg = "Acceso denegado";
        log.debug(msg, ex);
        return buildErrorResponse(msg, HttpStatus.FORBIDDEN, request.getRequestURI());
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> handleResponseStatusException(ResponseStatusException ex, HttpServletRequest request) {
        log.debug(ex.getReason(), ex);
        HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());
        return buildErrorResponse(ex.getReason(), status, request.getRequestURI());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest request) {
        log.debug(ex.getMessage(), ex);
        return buildErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST, request.getRequestURI());
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(String message, HttpStatus status, String path) {
        ErrorResponse error = ErrorResponse.builder()
            .timestamp(DateTimeUtils.now())
            .status(String.valueOf(status.value()))
            .error(status.getReasonPhrase())
            .message(message)
            .path(path).build();

        return ResponseEntity.status(status).body(error);
    }
}
