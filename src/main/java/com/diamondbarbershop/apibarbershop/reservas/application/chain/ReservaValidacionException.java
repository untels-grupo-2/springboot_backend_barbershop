package com.diamondbarbershop.apibarbershop.reservas.application.chain;

/**
 * Excepción lanzada cuando un handler de la cadena de validación rechaza
 * el comando de creación de reserva.
 *
 * Lleva información del handler que falló para facilitar el logging
 * y el debugging — útil cuando la cadena crezca a 5+ validaciones.
 */
public class ReservaValidacionException extends RuntimeException {

    private final String handlerOrigen;

    public ReservaValidacionException(String handlerOrigen, String mensaje) {
        super(mensaje);
        this.handlerOrigen = handlerOrigen;
    }

    public String getHandlerOrigen() {
        return handlerOrigen;
    }
}
