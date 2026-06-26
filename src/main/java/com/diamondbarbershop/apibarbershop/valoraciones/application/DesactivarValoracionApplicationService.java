package com.diamondbarbershop.apibarbershop.valoraciones.application;

import com.diamondbarbershop.apibarbershop.valoraciones.domain.model.Valoracion;
import com.diamondbarbershop.apibarbershop.valoraciones.domain.port.in.DesactivarValoracionUseCase;
import com.diamondbarbershop.apibarbershop.valoraciones.domain.port.out.ValoracionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Application service para desactivar una valoración (moderación).
 */
@Service
@RequiredArgsConstructor
public class DesactivarValoracionApplicationService implements DesactivarValoracionUseCase {

    private final ValoracionRepository valoracionRepository;

    @Override
    @Transactional
    public void desactivar(Long valoracionId) {
        Valoracion valoracion = valoracionRepository.findById(valoracionId)
                .orElseThrow(() -> new RuntimeException("Valoración no encontrada: " + valoracionId));
        valoracion.desactivar();
        valoracionRepository.save(valoracion);
    }
}
