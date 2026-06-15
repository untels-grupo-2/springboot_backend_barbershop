package com.diamondbarbershop.apibarbershop.reservas.domain.port.in;

import com.diamondbarbershop.apibarbershop.dtos.reserva.response.DtoReservaResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Puerto de ENTRADA — caso de uso "Consultar Reservas con filtros y paginación".
 * Soporta PB-14 (Specification — filtros combinables) y PB-20 (Pagination).
 *
 * El admin combina filtros opcionales en FiltroReservaQuery y la respuesta
 * viene paginada usando los estándares de Spring Data (Page&lt;T&gt;).
 *
 * Nota sobre Pageable/Page en el dominio:
 *   En arquitectura hexagonal estricta, estos tipos serían infraestructura
 *   y se traducirían a tipos propios del dominio. Para mantener la simplicidad
 *   y aprovechar Spring Data, los usamos directamente aquí — práctica común
 *   en proyectos Spring profesionales y aceptada por el equipo.
 */
public interface ConsultarReservasUseCase {

    /**
     * Lista reservas que matcheen el filtro, paginadas.
     *
     * @param filtro   criterios opcionales (cualquier campo null = no filtrar)
     * @param pageable parámetros de paginación y ordenamiento (Spring Data)
     * @return página de DTOs lista para responder al cliente HTTP
     */
    Page<DtoReservaResponse> listar(FiltroReservaQuery filtro, Pageable pageable);
}
