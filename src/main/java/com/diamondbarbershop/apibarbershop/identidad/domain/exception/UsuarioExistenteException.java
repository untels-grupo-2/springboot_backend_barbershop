package com.diamondbarbershop.apibarbershop.identidad.domain.exception;

public class UsuarioExistenteException extends RuntimeException {
    public UsuarioExistenteException (String message) {
        super(message);
    }
}
