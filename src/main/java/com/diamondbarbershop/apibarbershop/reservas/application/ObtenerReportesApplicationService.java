package com.diamondbarbershop.apibarbershop.reservas.application;

import com.diamondbarbershop.apibarbershop.reservas.infrastructure.rest.dto.DtoReporteResponse;
import com.diamondbarbershop.apibarbershop.reservas.infrastructure.persistance.ReservaJpaEntity;
import com.diamondbarbershop.apibarbershop.reservas.infrastructure.persistance.IReservaJpaRepository;
import com.diamondbarbershop.apibarbershop.reservas.domain.port.in.ObtenerReportesUseCase;
import com.diamondbarbershop.apibarbershop.util.EstadoReserva;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

/**
 * Application service — reporte de ganancias en un rango de fechas.
 *
 * Solo se contabilizan reservas en estado REALIZADA. Si se provee filtro
 * por nombre de servicio, restringe el conteo a ese servicio.
 */
@Service
@RequiredArgsConstructor
public class ObtenerReportesApplicationService implements ObtenerReportesUseCase {

    private final IReservaJpaRepository reservaJpaRepository;

    @Override
    @Transactional(readOnly = true)
    public DtoReporteResponse obtener(LocalDate fechaInicio, LocalDate fechaFin, String servicio) {
        List<ReservaJpaEntity> reservas =
                reservaJpaRepository.findByFechaReservaBetweenAndEstado(fechaInicio, fechaFin, EstadoReserva.REALIZADA);

        if (servicio != null) {
            reservas = reservas.stream()
                    .filter(r -> Objects.equals(r.getServicioEntity().getNombre(), servicio))
                    .toList();
        }

        long montoTotal = reservas.stream()
                .mapToLong(ReservaJpaEntity::getPrecioServicio)
                .sum();
        int cantidadReservas = reservas.size();

        DtoReporteResponse dto = new DtoReporteResponse();
        dto.setServicioNombre(servicio);
        dto.setMontoTotal(montoTotal);
        dto.setCantidadReservas(cantidadReservas);
        return dto;
    }
}
