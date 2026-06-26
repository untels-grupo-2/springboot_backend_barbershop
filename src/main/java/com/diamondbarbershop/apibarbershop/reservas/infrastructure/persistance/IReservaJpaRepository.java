package com.diamondbarbershop.apibarbershop.reservas.infrastructure.persistance;

import com.diamondbarbershop.apibarbershop.agenda.infrastructure.persistance.HorarioRangoJpaEntity;
import com.diamondbarbershop.apibarbershop.identidad.infrastructure.persistance.UsuarioJpaEntity;
import com.diamondbarbershop.apibarbershop.personal.infrastructure.persistance.BarberoJpaEntity;
import com.diamondbarbershop.apibarbershop.util.EstadoReserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Spring Data repository para ReservaJpaEntity.
 *
 * Implementa JpaSpecificationExecutor (PB-14) → habilita findAll(spec, pageable)
 * para consultas dinámicas con filtros componibles + paginación.
 */
@Repository
public interface IReservaJpaRepository extends JpaRepository<ReservaJpaEntity, Long>,
        JpaSpecificationExecutor<ReservaJpaEntity> {

    boolean existsByBarberoAndFechaReservaAndHorarioRango(
            BarberoJpaEntity barbero, LocalDate fechaReserva, HorarioRangoJpaEntity horarioRango);

    List<ReservaJpaEntity> findByFechaReservaAndHorarioRango(LocalDate fecha, HorarioRangoJpaEntity horarioRango);

    List<ReservaJpaEntity> findByFechaReserva(LocalDate fecha);

    List<ReservaJpaEntity> findByUsuario(UsuarioJpaEntity usuario);

    List<ReservaJpaEntity> findByEstado(EstadoReserva estado);

    List<ReservaJpaEntity> findByFechaReservaAndEstado(LocalDate fecha, EstadoReserva estado);

    List<ReservaJpaEntity> findByFechaReservaAndEstadoAndUsuario(LocalDate fecha, EstadoReserva estado, UsuarioJpaEntity usuario);

    List<ReservaJpaEntity> findByFechaReservaAndUsuario(LocalDate fecha, UsuarioJpaEntity usuario);

    List<ReservaJpaEntity> findByEstadoAndUsuario(EstadoReserva estado, UsuarioJpaEntity usuario);

    List<ReservaJpaEntity> findByFechaReservaBetweenAndEstado(LocalDate fechaInicio, LocalDate fechaFin, EstadoReserva estado);

    List<ReservaJpaEntity> findByBarberoAndFechaReserva(BarberoJpaEntity barbero, LocalDate fechaReserva);
}
