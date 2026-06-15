package com.diamondbarbershop.apibarbershop.reservas.application;

import com.diamondbarbershop.apibarbershop.dtos.reserva.response.DtoReservaResponse;
import com.diamondbarbershop.apibarbershop.reservas.domain.port.in.ConsultarReservasUseCase;
import com.diamondbarbershop.apibarbershop.reservas.domain.port.in.FiltroReservaQuery;
import com.diamondbarbershop.apibarbershop.reservas.domain.port.out.ReservaListadoView;
import com.diamondbarbershop.apibarbershop.reservas.domain.port.out.ReservaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Caso de uso: Consultar Reservas con filtros combinables y paginación.
 * Implementa PB-14 (Specification) + PB-20 (Pagination).
 *
 * Flujo:
 *   1. Recibe FiltroReservaQuery y Pageable desde el adaptador REST.
 *   2. Delega al puerto la búsqueda paginada (el adapter usa Specifications
 *      compuestas dinámicamente).
 *   3. Convierte cada ReservaListadoView (Read Model) a DtoReservaResponse
 *      (DTO de salida HTTP) — mapeo trivial 1 a 1.
 *
 * El @Transactional con readOnly=true le da una pista a Hibernate de que la
 * transacción es de lectura (puede optimizar el manejo de la sesión).
 */
@Service
@RequiredArgsConstructor
public class ConsultarReservasApplicationService implements ConsultarReservasUseCase {

    private final ReservaRepository reservaRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<DtoReservaResponse> listar(FiltroReservaQuery filtro, Pageable pageable) {
        return reservaRepository
                .buscarParaListado(filtro, pageable)
                .map(this::toDto);
    }

    /**
     * Traduce Read Model interno a DTO de respuesta HTTP.
     * Nota: montoTotal de DtoReservaResponse aquí se usa como "monto de esta
     * reserva en particular", no como suma agregada (que era el uso del legacy).
     */
    private DtoReservaResponse toDto(ReservaListadoView view) {
        DtoReservaResponse dto = new DtoReservaResponse();
        dto.setReservaId(view.reservaId());
        dto.setBarberoNombre(view.barberoNombre());
        dto.setUsuarioId(view.usuarioId());
        dto.setUsuarioNombre(view.usuarioNombre());
        dto.setServicioNombre(view.servicioNombre());
        dto.setHorarioRango(view.horarioRango());
        dto.setEstado(view.estado().name());
        dto.setMontoTotal(view.precio());
        return dto;
    }
}
