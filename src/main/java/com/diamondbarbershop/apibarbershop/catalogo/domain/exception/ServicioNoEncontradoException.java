package com.diamondbarbershop.apibarbershop.catalogo.domain.exception;

public class ServicioNoEncontradoException extends  RuntimeException {
    public ServicioNoEncontradoException (String message) {
        super(message);
    }
}
