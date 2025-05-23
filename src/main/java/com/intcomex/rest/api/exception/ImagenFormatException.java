package com.intcomex.rest.api.exception;

public class ImagenFormatException extends RuntimeException {
    public ImagenFormatException(String message) {
        super(message);
    }
    public ImagenFormatException(String message, Exception ex) {
        super(message, ex);
    }
}
