package com.diamondbarbershop.apibarbershop.identidad.domain.exception;

public class TokenInvalidoOExpiradoException extends RuntimeException{
    public TokenInvalidoOExpiradoException (String message){
        super(message);
    }

}
