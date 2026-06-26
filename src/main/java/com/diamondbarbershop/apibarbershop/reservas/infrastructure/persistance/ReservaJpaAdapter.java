package com.diamondbarbershop.apibarbershop.reservas.infrastructure.persistance;


import com.diamondbarbershop.apibarbershop.agenda.infrastructure.persistance.HorarioRangoJpaEntity;
import com.diamondbarbershop.apibarbershop.agenda.infrastructure.persistance.IHorarioRangoJpaRepository;
import com.diamondbarbershop.apibarbershop.catalogo.infrastructure.persistance.IServicioJpaRepository;
import com.diamondbarbershop.apibarbershop.identidad.infrastructure.persistance.IUsuarioJpaRepository;
import com.diamondbarbershop.apibarbershop.identidad.infrastructure.persistance.UsuarioJpaEntity;
import com.diamondbarbershop.apibarbershop.personal.infrastructure.persistance.BarberoJpaEntity;
import com.diamondbarbershop.apibarbershop.personal.infrastructure.persistance.IBarberoJpaRepository;
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
 *   usa Spring Data JPA e IReservaJpaRepository internamente.
 *
 *   Si mañana cambiamos de MySQL a MongoDB, solo cambia esta clase.
 *   El dominio y los casos de uso no se tocan.
 *
 * Patrón: Adapter (de Gang of Four) aplicado a la capa de persistencia.
 */
@Component
@RequiredArgsConstructor
public class ReservaJpaAdapter implements ReservaRepository {

    private final IReservaJpaRepository reservaRepository;
    private final IBarberoJpaRepository barberoRepository;
    private final IUsuarioJpaRepository usuariosRepository;
    private final IHorarioRangoJpaRepository horarioRangoRepository;
    private final IServicioJpaRepository servicioRepository;

    @Override
    public Reserva save(Reserva reserva) {
        ReservaJpaEntity jpa = toJpa(reserva);
        ReservaJpaEntity saved = reservaRepository.save(jpa);
        return toDomain(saved);
    }

    @Override
    public Optional<Reserva> findById(Long id) {
        return reservaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Reserva> findByBarberoIdAndFecha(Long barberoId, LocalDate fecha) {
        BarberoJpaEntity barbero = barberoRepository.findById(barberoId)
                .orElseThrow(() -> new RuntimeException("BarberoJpaEntity no encontrado: " + barberoId));
        return reservaRepository.findByBarberoAndFechaReserva(barbero, fecha)
                .stream().map(this::toDomain).toList();
    }

    @Override
    public List<Reserva> findByClienteId(Long clienteId) {
        UsuarioJpaEntity usuario = usuariosRepository.findById(clienteId)
                .orElseThrow(() -> new RuntimeException("UsuarioJpaEntity no encontrado: " + clienteId));
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
        Specification<ReservaJpaEntity> spec = ReservaSpecifications.componer(filtro);
        return reservaRepository.findAll(spec, pageable).map(this::toListadoView);
    }

    // ── Mappers ──────────────────────────────────────────────────────────────────

    /**
     * Convierte una ReservaJpaEntity (JPA, con relaciones cargadas) a la proyección
     * de listado optimizada para la UI. Solo se usa en consultas de lectura.
     */
    private ReservaListadoView toListadoView(ReservaJpaEntity jpa) {
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

    private ReservaJpaEntity toJpa(Reserva reserva) {
        ReservaJpaEntity jpa = new ReservaJpaEntity();

        if (reserva.getId() != null) {
            jpa.setReserva_id(reserva.getId());
        }

        jpa.setBarbero(barberoRepository.findById(reserva.getBarberoId())
                .orElseThrow(() -> new RuntimeException("BarberoJpaEntity no encontrado")));
        jpa.setUsuario(usuariosRepository.findById(reserva.getClienteId())
                .orElseThrow(() -> new RuntimeException("UsuarioJpaEntity no encontrado")));
        jpa.setHorarioRango(horarioRangoRepository.findById(reserva.getHorarioRangoId())
                .orElseThrow(() -> new RuntimeException("HorarioRangoJpaEntity no encontrado")));
        jpa.setServicioEntity(servicioRepository.findById(reserva.getServicioId())
                .orElseThrow(() -> new RuntimeException("ServicioJpaEntity no encontrado")));

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
    private Reserva toDomain(ReservaJpaEntity jpa) {
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
