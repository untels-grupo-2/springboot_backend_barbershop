package com.diamondbarbershop.apibarbershop.agenda.infrastructure.persistance;

import com.diamondbarbershop.apibarbershop.agenda.infrastructure.rest.dto.DtoBarberoDisponible;
import com.diamondbarbershop.apibarbershop.agenda.infrastructure.rest.dto.DtoHorarioBarberoInstanciaResponse;
import com.diamondbarbershop.apibarbershop.agenda.infrastructure.persistance.IHorarioBarberoInstanciaJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

/**
 * Query adapter (Read Model interno del BC Agenda) — retorna DTOs ya armados
 * desde la base, evitando bajar al dominio para vistas administrativas.
 *
 * Mantenemos esto como detalle de infraestructura: el application service lo usa
 * cuando necesita una proyección lista para HTTP, y no consume el repositorio
 * de dominio directamente para vistas agregadas.
 */
@Component
@RequiredArgsConstructor
public class IHorarioBarberoInstanciaQueryAdapter {

    private final IHorarioBarberoInstanciaJpaRepository instanciaJpaRepository;

    public List<DtoHorarioBarberoInstanciaResponse> findResponseByFechaBetween(LocalDate desde, LocalDate hasta) {
        return instanciaJpaRepository.findByFechaBetween(desde, hasta).stream()
                .map(i -> new DtoHorarioBarberoInstanciaResponse(
                        i.getFecha(),
                        i.getDia().name(),
                        i.getTipoHorario().getNombre(),
                        i.getBarbero().getNombre()))
                .toList();
    }

    public List<DtoBarberoDisponible> findBarberosQueTrabajan(LocalDate fecha, Long tipoHorarioId) {
        return instanciaJpaRepository.findByFechaAndTipoHorario_Id(fecha, tipoHorarioId).stream()
                .map(i -> {
                    DtoBarberoDisponible dto = new DtoBarberoDisponible();
                    dto.setBarberoId(i.getBarbero().getBarbero_id());
                    dto.setNombre(i.getBarbero().getNombre());
                    dto.setUrlBarbero(i.getBarbero().getUrlBarbero());
                    // disponible se setea después en el application service
                    return dto;
                })
                .toList();
    }
}
