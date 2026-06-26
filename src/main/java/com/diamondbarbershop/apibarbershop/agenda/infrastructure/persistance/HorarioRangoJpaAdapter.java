package com.diamondbarbershop.apibarbershop.agenda.infrastructure.persistance;

import com.diamondbarbershop.apibarbershop.agenda.domain.model.HorarioRango;
import com.diamondbarbershop.apibarbershop.agenda.domain.port.out.HorarioRangoRepository;
import com.diamondbarbershop.apibarbershop.agenda.infrastructure.persistance.IHorarioRangoJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Adapter — HorarioRangoRepository delegando en el JPA legacy.
 */
@Component
@RequiredArgsConstructor
public class HorarioRangoJpaAdapter implements HorarioRangoRepository {

    private final IHorarioRangoJpaRepository horarioRangoJpaRepository;

    @Override
    public List<HorarioRango> findAll() {
        return horarioRangoJpaRepository.findAll().stream()
                .map(jpa -> HorarioRango.reconstitute(
                        jpa.getHorarioRango_id(),
                        jpa.getRango(),
                        jpa.getTipoHorario().getId(),
                        jpa.getTipoHorario().getNombre()))
                .toList();
    }

    @Override
    public Optional<HorarioRango> findById(Long id) {
        return horarioRangoJpaRepository.findById(id)
                .map(jpa -> HorarioRango.reconstitute(
                        jpa.getHorarioRango_id(),
                        jpa.getRango(),
                        jpa.getTipoHorario().getId(),
                        jpa.getTipoHorario().getNombre()));
    }
}
