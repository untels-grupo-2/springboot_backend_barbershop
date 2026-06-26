package com.diamondbarbershop.apibarbershop.valoraciones.domain.port.in;

/**
 * Puerto de entrada — desactivar (moderar) una valoración existente.
 *
 * El admin usa este caso de uso después de responder al cliente o cuando
 * la valoración tiene contenido inapropiado. No se elimina, solo se oculta.
 */
public interface DesactivarValoracionUseCase {

    void desactivar(Long valoracionId);
}
