package com.diamondbarbershop.apibarbershop.reservas.infrastructure.specification;

import com.diamondbarbershop.apibarbershop.models.ReservaEntity;
import com.diamondbarbershop.apibarbershop.reservas.domain.port.in.FiltroReservaQuery;
import com.diamondbarbershop.apibarbershop.util.EstadoReserva;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

/**
 * Specifications de Spring Data JPA para consultar ReservaEntity (PB-14).
 *
 * Cada método estático devuelve una Specification componible (o null si el
 * filtro no aplica). Se combinan con Specification.where(...).and(...) en
 * el método componer().
 *
 * Beneficio:
 *   El repositorio Spring Data ya NO necesita 8 métodos findByXxx distintos
 *   para cada combinación de filtros (lo que existía en ReservaService legacy).
 *   Un único findAll(spec, pageable) cubre todas las combinaciones presentes
 *   y futuras.
 *
 * Patrón Specification (GoF + DDD):
 *   Las reglas de filtrado son objetos componibles que encapsulan
 *   "predicados de negocio" reutilizables.
 */
public final class ReservaSpecifications {

    private ReservaSpecifications() {
        // utility class — no instanciable
    }

    /**
     * Compone una Specification dinámica a partir del FiltroReservaQuery.
     * Cada campo nulo del filtro se ignora; los presentes se combinan con AND.
     */
    public static Specification<ReservaEntity> componer(FiltroReservaQuery filtro) {
        return Specification
                .where(porBarberoId(filtro.barberoId()))
                .and(porClienteId(filtro.clienteId()))
                .and(porEstado(filtro.estado()))
                .and(desdeFecha(filtro.fechaDesde()))
                .and(hastaFecha(filtro.fechaHasta()));
    }

    /** Filtra por barbero. Retorna null si barberoId es null (no aplicar filtro). */
    public static Specification<ReservaEntity> porBarberoId(Long barberoId) {
        if (barberoId == null) return null;
        return (root, query, cb) ->
                cb.equal(root.get("barbero").get("barbero_id"), barberoId);
    }

    /** Filtra por cliente (usuario). */
    public static Specification<ReservaEntity> porClienteId(Long clienteId) {
        if (clienteId == null) return null;
        return (root, query, cb) ->
                cb.equal(root.get("usuario").get("usuario_id"), clienteId);
    }

    /** Filtra por estado de la reserva. */
    public static Specification<ReservaEntity> porEstado(EstadoReserva estado) {
        if (estado == null) return null;
        return (root, query, cb) ->
                cb.equal(root.get("estado"), estado);
    }

    /** Reservas con fechaReserva >= desde. */
    public static Specification<ReservaEntity> desdeFecha(LocalDate desde) {
        if (desde == null) return null;
        return (root, query, cb) ->
                cb.greaterThanOrEqualTo(root.get("fechaReserva"), desde);
    }

    /** Reservas con fechaReserva <= hasta. */
    public static Specification<ReservaEntity> hastaFecha(LocalDate hasta) {
        if (hasta == null) return null;
        return (root, query, cb) ->
                cb.lessThanOrEqualTo(root.get("fechaReserva"), hasta);
    }
}
