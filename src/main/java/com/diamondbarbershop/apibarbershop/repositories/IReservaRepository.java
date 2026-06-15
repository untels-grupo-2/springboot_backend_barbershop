package com.diamondbarbershop.apibarbershop.repositories;

import com.diamondbarbershop.apibarbershop.models.Barbero;
import com.diamondbarbershop.apibarbershop.models.HorarioRango;
import com.diamondbarbershop.apibarbershop.models.ReservaEntity;
import com.diamondbarbershop.apibarbershop.models.Usuario;
import com.diamondbarbershop.apibarbershop.util.EstadoReserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Spring Data repository para ReservaEntity.
 *
 * Implementa JpaSpecificationExecutor (PB-14) → habilita findAll(spec, pageable)
 * para consultas dinámicas con filtros componibles + paginación.
 */
@Repository
public interface IReservaRepository extends JpaRepository<ReservaEntity, Long>,
        JpaSpecificationExecutor<ReservaEntity> {
    boolean existsByBarberoAndFechaReservaAndHorarioRango(Barbero barbero, LocalDate fechaReserva, HorarioRango horarioRango);
    List<ReservaEntity> findByFechaReservaAndHorarioRango(LocalDate fecha, HorarioRango horarioRango);
    List<ReservaEntity> findByFechaReserva(LocalDate fecha);
    List<ReservaEntity> findByUsuario(Usuario usuario);
    List<ReservaEntity> findByEstado(EstadoReserva estado);
    List<ReservaEntity> findByFechaReservaAndEstado(LocalDate fecha, EstadoReserva estado);
    List<ReservaEntity> findByFechaReservaAndEstadoAndUsuario(LocalDate fecha, EstadoReserva estado, Usuario usuario);
    List<ReservaEntity> findByFechaReservaAndUsuario(LocalDate fecha, Usuario usuario);
    List<ReservaEntity> findByEstadoAndUsuario(EstadoReserva estado, Usuario usuario);
    List<ReservaEntity> findByFechaReservaBetweenAndEstado(LocalDate fechaInicio, LocalDate fechaFin, EstadoReserva estado);
    List<ReservaEntity> findByBarberoAndFechaReserva(Barbero barbero, LocalDate fechaReserva);
}
