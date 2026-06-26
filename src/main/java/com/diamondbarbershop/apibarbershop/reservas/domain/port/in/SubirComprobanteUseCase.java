package com.diamondbarbershop.apibarbershop.reservas.domain.port.in;

import org.springframework.web.multipart.MultipartFile;

/**
 * Puerto de entrada — el cliente sube el comprobante de pago de su reserva.
 *
 * Solo el dueño de la reserva puede subir el comprobante; el use case valida
 * que el username del Authentication coincida con el clienteId de la reserva.
 */
public interface SubirComprobanteUseCase {

    void subir(Long reservaId, MultipartFile imagen, String username);
}
