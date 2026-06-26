package com.diamondbarbershop.apibarbershop.valoraciones.application;

import com.diamondbarbershop.apibarbershop.valoraciones.domain.model.Valoracion;
import com.diamondbarbershop.apibarbershop.valoraciones.domain.port.in.CrearValoracionUseCase;
import com.diamondbarbershop.apibarbershop.valoraciones.domain.port.out.ValoracionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Application service para crear una valoración.
 *
 * Las invariantes (puntuación 1-5, estado inicial activo) viven en el
 * dominio puro Valoracion.crear() — este service solo orquesta.
 */
@Service
@RequiredArgsConstructor
public class CrearValoracionApplicationService implements CrearValoracionUseCase {

    private final ValoracionRepository valoracionRepository;

    @Override
    @Transactional
    public Long crear(CrearValoracionCommand command) {
        Valoracion valoracion = Valoracion.crear(
                command.puntuacion(),
                command.util(),
                command.mensaje(),
                command.clienteId()
        );
        Valoracion guardada = valoracionRepository.save(valoracion);
        return guardada.getId();
    }
}
