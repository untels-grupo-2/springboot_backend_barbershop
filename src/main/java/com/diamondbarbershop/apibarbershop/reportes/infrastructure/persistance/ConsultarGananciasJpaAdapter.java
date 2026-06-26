package com.diamondbarbershop.apibarbershop.reportes.infrastructure.persistance;

import com.diamondbarbershop.apibarbershop.reportes.domain.model.GananciaPorBarbero;
import com.diamondbarbershop.apibarbershop.reportes.domain.model.GananciaPorDia;
import com.diamondbarbershop.apibarbershop.reportes.domain.model.GananciaPorServicio;
import com.diamondbarbershop.apibarbershop.reportes.domain.port.out.ConsultarGananciasPort;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ConsultarGananciasJpaAdapter implements ConsultarGananciasPort {

    private final EntityManager entityManager;

    @Override
    public List<GananciaPorDia> obtenerGananciasPorDia(LocalDate fechaInicio, LocalDate fechaFin, String servicioNombre) {
        StringBuilder jpql = new StringBuilder(
                "SELECT new com.diamondbarbershop.apibarbershop.reportes.domain.model.GananciaPorDia(" +
                "r.fechaReserva, COUNT(r), SUM(r.precioServicio)) " +
                "FROM ReservaJpaEntity r " +
                "WHERE r.estado = 'REALIZADA' " +
                "AND r.fechaReserva BETWEEN :fechaInicio AND :fechaFin"
        );

        if (servicioNombre != null) {
            jpql.append(" AND r.servicioEntity.nombre = :servicioNombre");
        }

        jpql.append(" GROUP BY r.fechaReserva ORDER BY r.fechaReserva ASC");

        TypedQuery<GananciaPorDia> query = entityManager.createQuery(jpql.toString(), GananciaPorDia.class);
        query.setParameter("fechaInicio", fechaInicio);
        query.setParameter("fechaFin", fechaFin);

        if (servicioNombre != null) {
            query.setParameter("servicioNombre", servicioNombre);
        }

        return query.getResultList();
    }

    @Override
    public List<GananciaPorServicio> obtenerGananciasPorServicio(LocalDate fechaInicio, LocalDate fechaFin) {
        String jpql =
                "SELECT new com.diamondbarbershop.apibarbershop.reportes.domain.model.GananciaPorServicio(" +
                "r.servicioEntity.servicio_id, r.servicioEntity.nombre, COUNT(r), SUM(r.precioServicio)) " +
                "FROM ReservaJpaEntity r " +
                "WHERE r.estado = 'REALIZADA' " +
                "AND r.fechaReserva BETWEEN :fechaInicio AND :fechaFin " +
                "GROUP BY r.servicioEntity.servicio_id, r.servicioEntity.nombre " +
                "ORDER BY SUM(r.precioServicio) DESC";

        TypedQuery<GananciaPorServicio> query = entityManager.createQuery(jpql, GananciaPorServicio.class);
        query.setParameter("fechaInicio", fechaInicio);
        query.setParameter("fechaFin", fechaFin);

        return query.getResultList();
    }

    @Override
    public List<GananciaPorBarbero> obtenerGananciasPorBarbero(LocalDate fechaInicio, LocalDate fechaFin, String servicioNombre) {
        StringBuilder jpql = new StringBuilder(
                "SELECT new com.diamondbarbershop.apibarbershop.reportes.domain.model.GananciaPorBarbero(" +
                "r.barbero.barbero_id, r.barbero.nombre, COUNT(r), SUM(r.precioServicio)) " +
                "FROM ReservaJpaEntity r " +
                "WHERE r.estado = 'REALIZADA' " +
                "AND r.fechaReserva BETWEEN :fechaInicio AND :fechaFin"
        );

        if (servicioNombre != null) {
            jpql.append(" AND r.servicioEntity.nombre = :servicioNombre");
        }

        jpql.append(" GROUP BY r.barbero.barbero_id, r.barbero.nombre ORDER BY SUM(r.precioServicio) DESC");

        TypedQuery<GananciaPorBarbero> query = entityManager.createQuery(jpql.toString(), GananciaPorBarbero.class);
        query.setParameter("fechaInicio", fechaInicio);
        query.setParameter("fechaFin", fechaFin);

        if (servicioNombre != null) {
            query.setParameter("servicioNombre", servicioNombre);
        }

        return query.getResultList();
    }
}
