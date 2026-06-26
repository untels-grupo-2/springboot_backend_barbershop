package com.diamondbarbershop.apibarbershop.reservas.domain.port.in;

/**
 * Puerto de entrada — verifica si el cliente tiene una recompensa de
 * fidelidad disponible (>= 7 reservas con estRecompensa = 0).
 *
 * Devuelve true si puede usar la recompensa. La app móvil cliente consulta
 * esto al cargar el formulario de reservar y muestra el botón "Reservar
 * gratis con tu recompensa" si es true.
 */
public interface VerificarRecompensaDisponibleUseCase {

    boolean tieneRecompensa(String username);
}
