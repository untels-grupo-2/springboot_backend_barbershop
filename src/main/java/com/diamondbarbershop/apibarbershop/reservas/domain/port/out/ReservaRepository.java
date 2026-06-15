package com.diamondbarbershop.apibarbershop.reservas.domain.port.out;

import com.diamondbarbershop.apibarbershop.reservas.domain.model.Reserva;
import com.diamondbarbershop.apibarbershop.reservas.domain.port.in.FiltroReservaQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Puerto de SALIDA (Outbound Port) — contrato de persistencia del aggregate ReservaEntity.
 *
 * ¿Por qué es una interfaz en el dominio y no una clase de Spring Data?
 *
 *   Con Spring Data JPA directamente:
 *     ReservaService → IReservaRepository (Spring Data) → BD
 *     El dominio depende de la infraestructura. Si cambiamos de MySQL a MongoDB,
 *     hay que modificar el dominio.
 *
 *   Con este puerto:
 *     ReservaService → ReservaRepository (esta interfaz) → [adaptador] → BD
 *     El dominio define el CONTRATO. La infraestructura lo implementa.
 *     Si cambiamos de MySQL a MongoDB, solo cambia el adaptador.
 *
 * Los métodos usan LENGUAJE DE DOMINIO, no lenguaje de base de datos.
 * Ejemplo:
 *   ✅ findByBarberoIdAndFecha()    ← intención de negocio clara
 *   ❌ SELECT * FROM reservas WHERE barbero_id = ? AND fecha_reserva = ?
 */
public interface ReservaRepository {

    /**
     * Persiste una reserva nueva o actualiza una existente.
     * Devuelve la reserva con el ID asignado (si era nueva).
     */
    Reserva save(Reserva reserva);

    Optional<Reserva> findById(Long id);

    /** Reservas de un barbero en una fecha específica — para verificar disponibilidad. */
    List<Reserva> findByBarberoIdAndFecha(Long barberoId, LocalDate fecha);

    /** Reservas de un cliente — para su historial. */
    List<Reserva> findByClienteId(Long clienteId);

    /** Reservas realizadas en un rango de fechas — para informes de ganancias. */
    List<Reserva> findRealizadasEntreFechas(LocalDate desde, LocalDate hasta);

    /** Todas las reservas — para reportes generales (usar con cuidado en producción). */
    List<Reserva> findAll();

    /**
     * Consulta paginada y filtrada de reservas para listados administrativos (PB-14 + PB-20).
     *
     * Retorna una proyección de solo lectura (ReservaListadoView) en lugar del
     * agregado completo, para incluir nombres relacionados sin caer en N+1.
     * El adapter usa Specifications dinámicas para componer la consulta JPA.
     */
    Page<ReservaListadoView> buscarParaListado(FiltroReservaQuery filtro, Pageable pageable);
}
