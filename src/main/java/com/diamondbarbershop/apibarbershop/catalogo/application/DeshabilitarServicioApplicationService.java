package com.diamondbarbershop.apibarbershop.catalogo.application;

import com.diamondbarbershop.apibarbershop.catalogo.domain.model.Servicio;
import com.diamondbarbershop.apibarbershop.catalogo.domain.port.in.DeshabilitarServicioUseCase;
import com.diamondbarbershop.apibarbershop.catalogo.domain.port.out.ServicioRepository;
import com.diamondbarbershop.apibarbershop.catalogo.domain.exception.ServicioNoEncontradoException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Application service para deshabilitar un servicio (baja lógica).
 */
@Service
@RequiredArgsConstructor
public class DeshabilitarServicioApplicationService implements DeshabilitarServicioUseCase {

    private final ServicioRepository servicioRepository;

    @Override
    @Transactional
    public void deshabilitar(Long servicioId) {
        Servicio servicio = servicioRepository.findById(servicioId)
                .orElseThrow(() -> new ServicioNoEncontradoException(
                        "No se puede deshabilitar. Servicio no encontrado con Id: " + servicioId));

        servicio.deshabilitar();    // regla del dominio
        servicioRepository.save(servicio);
    }
}
