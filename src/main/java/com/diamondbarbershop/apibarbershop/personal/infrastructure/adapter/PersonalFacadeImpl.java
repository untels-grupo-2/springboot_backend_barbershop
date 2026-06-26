package com.diamondbarbershop.apibarbershop.personal.infrastructure.adapter;

import com.diamondbarbershop.apibarbershop.personal.infrastructure.persistance.BarberoJpaEntity;
import com.diamondbarbershop.apibarbershop.personal.infrastructure.persistance.IBarberoJpaRepository;
import com.diamondbarbershop.apibarbershop.reservas.domain.port.out.PersonalFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Implementación de PersonalFacade — vive en el BC Personal.
 *
 * Única clase del sistema que conoce el detalle interno de cómo el BC
 * Personal almacena la información (entidad JPA BarberoJpaEntity, repositorio
 * IBarberoJpaRepository).
 *
 * NOTA TRANSITORIA:
 *   Cuando se complete la migración hexagonal del BC Personal,
 *   esta Facade leerá del nuevo BarberoRepository (puerto de salida)
 *   en lugar del repo JPA legacy. La interfaz PersonalFacade no cambia.
 */
@Component
@RequiredArgsConstructor
public class PersonalFacadeImpl implements PersonalFacade {

    private final IBarberoJpaRepository barberoRepository;

    @Override
    public boolean existeBarbero(Long barberoId) {
        return barberoRepository.findById(barberoId).isPresent();
    }

    @Override
    public boolean estaActivoBarbero(Long barberoId) {
        Optional<BarberoJpaEntity> barbero = barberoRepository.findById(barberoId);
        return barbero.isPresent()
                && Integer.valueOf(1).equals(barbero.get().getEstado());
    }
}
