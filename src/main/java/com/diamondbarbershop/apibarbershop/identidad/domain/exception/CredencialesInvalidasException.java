package com.diamondbarbershop.apibarbershop.identidad.domain.exception;

public class CredencialesInvalidasException extends RuntimeException {
    public CredencialesInvalidasException (String message) {
        super(message);
    }
}
