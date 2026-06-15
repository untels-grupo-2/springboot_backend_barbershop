package com.diamondbarbershop.apibarbershop.reservas.infrastructure.persistance;


import com.diamondbarbershop.apibarbershop.models.Barbero;
import com.diamondbarbershop.apibarbershop.models.ReservaEntity;
import com.diamondbarbershop.apibarbershop.models.Usuario;
import com.diamondbarbershop.apibarbershop.repositories.*;
import com.diamondbarbershop.apibarbershop.reservas.domain.model.Precio;
import com.diamondbarbershop.apibarbershop.reservas.domain.model.Reserva;
import com.diamondbarbershop.apibarbershop.reservas.domain.port.in.FiltroReservaQuery;
import com.diamondbarbershop.apibarbershop.reservas.domain.port.out.ReservaListadoView;
import com.diamondbarbershop.apibarbershop.reservas.domain.port.out.ReservaRepository;
import com.diamondbarbershop.apibarbershop.reservas.infrastructure.specification.ReservaSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Adaptador de salida (Outbound Adapter) — implementa el puerto ReservaRepository.
 *
 * ¿Por qué existe esta clase?
 *   El dominio define lo que necesita (el puerto ReservaRepository),
 *   pero no sabe CÓMO se persiste. Esta clase es la que "sabe cómo":
 *   usa Spring Data JPA e IReservaRepository internamente.
 *
 *   Si mañana cambiamos de MySQL a MongoDB, solo cambia esta clase.
 *   El dominio y los casos de uso no se tocan.
 *
 * Patrón: Adapter (de Gang of Four) aplicado a la capa de persistencia.
 */
@Component
@RequiredArgsConstructor
public class ReservaJpaAdapter implements ReservaRepository {

    private final IReservaRepository reservaRepository;
    private final IBarberoRepository barberoRepository;
    private final IUsuariosRepository usuariosRepository;
    private final IHorarioRangoRepository horarioRangoRepository;
    private final IServicioRepository servicioRepository;

    @Override
    public Reserva save(Reserva reserva) {
        ReservaEntity jpa = toJpa(reserva);
        ReservaEntity saved = reservaRepository.save(jpa);
        return toDomain(saved);
    }

    @Override
    public Optional<Reserva> findById(Long id) {
        return reservaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Reserva> findByBarberoIdAndFecha(Long barberoId, LocalDate fecha) {
        Barbero barbero = barberoRepository.findById(barberoId)
                .orElseThrow(() -> new RuntimeException("Barbero no encontrado: " + barberoId));
        return reservaRepository.findByBarberoAndFechaReserva(barbero, fecha)
                .stream().map(this::toDomain).toList();
    }

    @Override
    public List<Reserva> findByClienteId(Long clienteId) {
        Usuario usuario = usuariosRepository.findById(clienteId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + clienteId));
        return reservaRepository.findByUsuario(usuario)
                .stream().map(this::toDomain).toList();
    }

    @Override
    public List<Reserva> findRealizadasEntreFechas(LocalDate desde, LocalDate hasta) {
        return reservaRepository
                .findByFechaReservaBetweenAndEstado(
                        desde, hasta,
                        com.diamondbarbershop.apibarbershop.util.EstadoReserva.REALIZADA)
                .stream().map(this::toDomain).toList();
    }

    @Override
    public List<Reserva> findAll() {
        return reservaRepository.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public Page<ReservaListadoView> buscarParaListado(FiltroReservaQuery filtro, Pageable pageable) {
        // PB-14 + PB-20: Specifications + Pagination combinados.
        // El JpaSpecificationExecutor hace un solo query con WHERE dinámico,
        // LIMIT/OFFSET y los JOINs implícitos hacia barbero, usuario, servicio
        // y horarioRango — sin caer en N+1.
        Specification<ReservaEntity> spec = ReservaSpecifications.componer(filtro);
        return reservaRepository.findAll(spec, pageable).map(this::toListadoView);
    }

    // ── Mappers ──────────────────────────────────────────────────────────────────

    /**
     * Convierte una ReservaEntity (JPA, con relaciones cargadas) a la proyección
     * de listado optimizada para la UI. Solo se usa en consultas de lectura.
     */
    private ReservaListadoView toListadoView(ReservaEntity jpa) {
        return new ReservaListadoView(
                jpa.getReserva_id(),
                jpa.getBarbero().getNombre(),
                jpa.getUsuario().getUsuario_id(),
                jpa.getUsuario().getNombre(),
                jpa.getServicioEntity().getNombre(),
                jpa.getHorarioRango().getRango(),
                jpa.getEstado(),
                jpa.getPrecioServicio(),
                jpa.getFechaReserva()
        );
    }

    /**
     * Convierte el modelo de dominio a entidad JPA.
     * Necesita cargar los objetos relacionados porque la entidad JPA
     * trabaja con objetos completos, no con IDs sueltos.
     */

    private ReservaEntity toJpa(Reserva reserva) {
        ReservaEntity jpa = new ReservaEntity();

        if (reserva.getId() != null) {
            jpa.setReserva_id(reserva.getId());
        }

        jpa.setBarbero(barberoRepository.findById(reserva.getBarberoId())
                .orElseThrow(() -> new RuntimeException("Barbero no encontrado")));
        jpa.setUsuario(usuariosRepository.findById(reserva.getClienteId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado")));
        jpa.setHorarioRango(horarioRangoRepository.findById(reserva.getHorarioRangoId())
                .orElseThrow(() -> new RuntimeException("HorarioRango no encontrado")));
        jpa.setServicioEntity(servicioRepository.findById(reserva.getServicioId())
                .orElseThrow(() -> new RuntimeException("ServicioEntity no encontrado")));

        jpa.setEstado(reserva.getEstado());
        jpa.setPrecioServicio(reserva.getPrecio().getMonto());
        jpa.setMotivoDescripcion(reserva.getMotivoDescripcion());
        jpa.setAdicionales(reserva.getAdicionales());
        jpa.setFechaCreacion(reserva.getFechaCreacion());
        jpa.setFechaReserva(reserva.getFechaReserva());
        jpa.setEstRecompensa(reserva.getEstRecompensa());
        jpa.setUrlPago(reserva.getUrlPago());

        return jpa;
    }

    /**
     * Convierte la entidad JPA al modelo de dominio.
     * Usa reconstitute() — no revalida reglas de negocio porque
     * el dato ya existe en la BD (fue validado cuando se creó).
     */
    private Reserva toDomain(ReservaEntity jpa) {
        return Reserva.reconstitute(
                jpa.getReserva_id(),
                jpa.getBarbero().getBarbero_id(),
                jpa.getUsuario().getUsuario_id(),
                jpa.getServicioEntity().getServicio_id(),
                jpa.getHorarioRango().getHorarioRango_id(),
                jpa.getEstado(),
                new Precio(jpa.getPrecioServicio()),
                jpa.getMotivoDescripcion(),
                jpa.getAdicionales(),
                jpa.getFechaCreacion(),
                jpa.getFechaReserva(),
                jpa.getEstRecompensa(),
                jpa.getUrlPago()
        );
    }
}
