package com.diamondbarbershop.apibarbershop.reservas.application.chain;

import com.diamondbarbershop.apibarbershop.reservas.domain.port.in.CrearReservaUseCase.CrearReservaCommand;

/**
 * Clase abstracta base del patrón Chain of Responsibility (PB-15).
 *
 * Cada validación que ocurre al crear una reserva se encapsula en una subclase
 * concreta que implementa doValidar(). La clase base se encarga de:
 *   1. Ejecutar la validación de la subclase (doValidar).
 *   2. Si pasa, delegar al siguiente handler de la cadena.
 *   3. Si falla, lanzar ReservaValidacionException y cortar la cadena.
 *
 * Esto es un Template Method dentro del patrón Chain of Responsibility:
 * el flujo (validar y delegar) está fijo; lo variable es la regla concreta
 * de cada subclase.
 *
 * Beneficio Open/Closed:
 *   Agregar una nueva validación = crear una nueva subclase + agregarla en
 *   CadenaValidacionReservaConfig. Nunca se modifica este archivo ni el
 *   CrearReservaApplicationService.
 */
public abstract class ValidacionReservaHandler {

    private ValidacionReservaHandler siguiente;

    /**
     * Encadena el siguiente handler. Retorna el handler pasado por parámetro
     * para permitir encadenamiento fluido:
     *
     *   handlerA.setNext(handlerB).setNext(handlerC);
     */
    public ValidacionReservaHandler setNext(ValidacionReservaHandler siguiente) {
        this.siguiente = siguiente;
        return siguiente;
    }

    /**
     * Punto de entrada del handler. No marcar como final si en el futuro
     * algún handler necesita comportamiento especial (ej. omitir validación
     * según una condición).
     */
    public void validar(CrearReservaCommand command) {
        doValidar(command);
        if (siguiente != null) {
            siguiente.validar(command);
        }
    }

    /**
     * Cada subclase implementa su regla concreta aquí.
     * Si la validación falla, debe lanzar ReservaValidacionException.
     */
    protected abstract void doValidar(CrearReservaCommand command);
}
