package com.diamondbarbershop.apibarbershop.personal.application;

import com.diamondbarbershop.apibarbershop.personal.domain.exception.BarberoNoEncontradoException;
import com.diamondbarbershop.apibarbershop.personal.domain.model.Barbero;
import com.diamondbarbershop.apibarbershop.personal.domain.port.in.DeshabilitarBarberoUseCase;
import com.diamondbarbershop.apibarbershop.personal.domain.port.out.BarberoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Application service para deshabilitar un barbero (baja lógica).
 */
@Service
@RequiredArgsConstructor
public class DeshabilitarBarberoApplicationService implements DeshabilitarBarberoUseCase {

    private final BarberoRepository barberoRepository;

    @Override
    @Transactional
    public void deshabilitar(Long barberoId) {
        Barbero barbero = barberoRepository.findById(barberoId)
                .orElseThrow(() -> new BarberoNoEncontradoException(
                        "Barbero no encontrado con el id: " + barberoId));
        barbero.deshabilitar();
        barberoRepository.save(barbero);
    }
}
