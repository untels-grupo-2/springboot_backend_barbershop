package com.diamondbarbershop.apibarbershop.agenda.application;

import com.diamondbarbershop.apibarbershop.agenda.domain.port.in.ListarBarberosDisponiblesUseCase;
import com.diamondbarbershop.apibarbershop.agenda.domain.service.CalcularDisponibilidadBarberoService;
import com.diamondbarbershop.apibarbershop.agenda.infrastructure.persistance.IHorarioBarberoInstanciaQueryAdapter;
import com.diamondbarbershop.apibarbershop.agenda.infrastructure.rest.dto.DtoBarberoDisponible;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Application service — lista qué barberos están disponibles para reservar.
 *
 * Diseño:
 *   1. queryAdapter devuelve los barberos que trabajan ese día/tipo de horario
 *      (en una sola query).
 *   2. Para cada candidato, delega la regla "¿está disponible para ese rango?"
 *      al DOMAIN SERVICE `CalcularDisponibilidadBarberoService`, que encapsula
 *      la lógica de negocio pura (verificar horario activo + slot no ocupado).
 *
 * El Domain Service consume sus propios puertos (`HorarioInstanciaRepository`
 * y `ConsultarReservasActivasPort`); este application service no conoce esos
 * detalles, solo orquesta la llamada.
 */
@Service
@RequiredArgsConstructor
public class ListarBarberosDisponiblesApplicationService implements ListarBarberosDisponiblesUseCase {

    private final IHorarioBarberoInstanciaQueryAdapter queryAdapter;
    private final CalcularDisponibilidadBarberoService disponibilidadService;

    @Override
    @Transactional(readOnly = true)
    public List<DtoBarberoDisponible> listar(LocalDate fecha, Long tipoHorarioId, Long horarioRangoId) {
        List<DtoBarberoDisponible> candidatos = queryAdapter.findBarberosQueTrabajan(fecha, tipoHorarioId);

        candidatos.forEach(dto -> dto.setDisponible(
                disponibilidadService.estaDisponible(dto.getBarberoId(), fecha, horarioRangoId)
        ));

        return candidatos;
    }
}
