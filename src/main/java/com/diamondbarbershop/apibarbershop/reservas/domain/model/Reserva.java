package com.diamondbarbershop.apibarbershop.reservas.domain.model;

import com.diamondbarbershop.apibarbershop.reservas.domain.event.*;
import com.diamondbarbershop.apibarbershop.shared.domain.event.DomainEvent;
import com.diamondbarbershop.apibarbershop.util.EstadoReserva;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * AGGREGATE ROOT del Bounded Context "Reservas".
 *
 * ¿Qué significa ser Aggregate Root?
 *   - Es el punto de entrada ÚNICO al aggregate. Nadie modifica su estado
 *     sin pasar por los métodos de esta clase.
 *   - Define la FRONTERA DE TRANSACCIÓN: todo lo que está dentro se guarda
 *     o falla junto (un save, una transacción).
 *   - Protege las INVARIANTES del negocio (reglas que siempre deben cumplirse).
 *
 * Invariantes que protege esta clase:
 *   1. Una reserva solo puede pasar CREADA → CONFIRMADA (no saltar estados).
 *   2. Una reserva solo puede pasar CONFIRMADA → REALIZADA.
 *   3. Una reserva REALIZADA no puede cancelarse.
 *   4. No se puede crear una reserva con fecha en el pasado.
 *   5. El precio debe ser mayor o igual a cero (delegado al VO Precio).
 *
 * NOTA IMPORTANTE sobre las anotaciones:
 *   Esta clase NO tiene @Entity, @Table ni ninguna anotación de JPA.
 *   Es dominio PURO — no sabe que existe una base de datos.
 *   La persistencia se maneja en la capa de infraestructura mediante
 *   ReservaJpaEntity (que se creará en Sprint 2 al migrar).
 *
 * NOTA sobre EstadoReserva:
 *   Temporalmente importamos del paquete util existente.
 *   En Sprint 2 moveremos el enum a reservas.domain.model.EstadoReserva.
 */
public class Reserva {

    private Long id;

    // Referencias a otros Bounded Contexts — SOLO los IDs, nunca el objeto completo.
    // Esto es la regla de oro de los Aggregates: no cruces fronteras con objetos,
    // solo con identificadores.
    private Long barberoId;    // referencia a BC Personal
    private Long clienteId;    // referencia a BC Identidad
    private Long servicioId;   // referencia a BC Catálogo
    private Long horarioRangoId; // referencia a BC Agenda

    private EstadoReserva estado;
    private Precio precio;           // Value Object — con validación incorporada
    private String motivoDescripcion;
    private String adicionales;
    private LocalDateTime fechaCreacion;
    private LocalDate fechaReserva;
    private Integer estRecompensa;
    private String urlPago;

    /**
     * Lista interna de eventos de dominio acumulados.
     * El Application Service los extrae y publica DESPUÉS del save.
     * Esto garantiza que los eventos solo se emiten si la persistencia fue exitosa.
     */
    private final List<DomainEvent> eventos = new ArrayList<>();

    // Constructor privado — la creación siempre pasa por el factory method.
    // Así controlamos que ningún objeto externo cree una Reserva en estado inválido.
    private Reserva() {}

    // ── Factory Method ─────────────────────────────────────────────────────────

    /**
     * Crea una nueva Reserva en estado CREADA.
     *
     * ¿Por qué un factory method en lugar de un constructor público?
     * Porque la creación tiene lógica de negocio:
     *   - Valida que la fecha no sea en el pasado.
     *   - Establece el estado inicial correcto.
     *   - Registra el timestamp de creación.
     *   - Emite el evento ReservaCreada.
     * Un constructor no puede expresar esa intención tan claramente.
     *
     * Parámetro usaRecompensa (PB-11 Strategy):
     *   Indica si esta reserva se está creando consumiendo la recompensa de fidelidad
     *   del cliente. El monto ya viene calculado en el Value Object Precio (0 si es
     *   con recompensa, precio del servicio si es normal) — el agregado solo necesita
     *   saberlo para marcar estRecompensa = 1, que el resto del sistema usa como
     *   indicador de que esta reserva fue una recompensa consumida.
     */
    public static Reserva crear(
            Long barberoId,
            Long clienteId,
            Long servicioId,
            Long horarioRangoId,
            Precio precio,
            LocalDate fechaReserva,
            String adicionales,
            boolean usaRecompensa
    ) {
        // Invariante de negocio: no se reservan fechas pasadas
        if (fechaReserva.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException(
                    "No se puede crear una reserva en el pasado. Fecha recibida: " + fechaReserva
            );
        }

        Reserva reserva = new Reserva();
        reserva.barberoId       = barberoId;
        reserva.clienteId       = clienteId;
        reserva.servicioId      = servicioId;
        reserva.horarioRangoId  = horarioRangoId;
        reserva.precio          = precio;
        reserva.estado          = EstadoReserva.CREADA;
        reserva.fechaCreacion   = LocalDateTime.now();
        reserva.fechaReserva    = fechaReserva;
        reserva.adicionales     = adicionales;
        reserva.estRecompensa   = usaRecompensa ? 1 : 0;

        // El aggregate emite el evento — no lo publica directamente.
        // Quien lo publica es el Application Service, después del save exitoso.
        reserva.eventos.add(new ReservaCreada(
                null,           // id aún no asignado (viene del repositorio)
                barberoId, clienteId, servicioId,
                fechaReserva, horarioRangoId,
                usaRecompensa,
                LocalDateTime.now()
        ));

        return reserva;
    }

    /**
     * Marca esta reserva como "consumida" por el sistema de recompensas (PB-13).
     *
     * Cuando un cliente usa su recompensa de fidelidad para reservar gratis,
     * el sistema debe marcar las 7 reservas que acumuló como "ya consumidas"
     * para que no las pueda volver a usar. Eso lo dispara RecompensaListener
     * cuando recibe un ReservaCreada con usaRecompensa = true.
     *
     * No es setter público de estRecompensa: es lógica de negocio con nombre
     * descriptivo. Los setters públicos romperían la integridad del aggregate.
     */
    public void consumirParaRecompensa() {
        if (this.estRecompensa != null && this.estRecompensa == 1) {
            // Ya estaba marcada — no-op, mantiene idempotencia.
            return;
        }
        this.estRecompensa = 1;
    }

    // ── Transiciones de estado (el núcleo del negocio) ─────────────────────────

    /**
     * Confirma la reserva. Solo válido si está en estado CREADA.
     * Representa la aprobación del admin o del barbero.
     */
    public void confirmar() {
        if (this.estado != EstadoReserva.CREADA) {
            throw new IllegalStateException(
                    "Solo una reserva CREADA puede confirmarse. Estado actual: " + this.estado
            );
        }
        this.estado = EstadoReserva.CONFIRMADA;
        this.eventos.add(new ReservaConfirmada(
                this.id, this.clienteId, this.fechaReserva, LocalDateTime.now()
        ));
    }

    /**
     * Marca la reserva como realizada. Solo válido si está CONFIRMADA.
     * Representa que el servicio fue prestado exitosamente.
     */
    public void marcarComoRealizada() {
        if (this.estado != EstadoReserva.CONFIRMADA) {
            throw new IllegalStateException(
                    "Solo una reserva CONFIRMADA puede marcarse como realizada. Estado actual: " + this.estado
            );
        }
        this.estado = EstadoReserva.REALIZADA;
        this.eventos.add(new ReservaRealizada(
                this.id, this.clienteId,
                this.precio.getMonto(),
                this.estRecompensa != null && this.estRecompensa > 0,
                LocalDateTime.now()
        ));
    }

    /**
     * Cancela la reserva. Solo inválido si ya está REALIZADA.
     * Puede cancelarse desde CREADA o CONFIRMADA.
     */
    public void cancelar(String motivo) {
        if (this.estado == EstadoReserva.REALIZADA) {
            throw new IllegalStateException(
                    "No se puede cancelar una reserva que ya fue realizada"
            );
        }
        if (this.estado == EstadoReserva.CANCELADA) {
            throw new IllegalStateException("La reserva ya está cancelada");
        }
        this.estado = EstadoReserva.CANCELADA;
        this.motivoDescripcion = motivo;
        this.eventos.add(new ReservaCancelada(
                this.id, this.clienteId, motivo, LocalDateTime.now()
        ));
    }

    // ── Consultas de estado (expresan el Lenguaje Ubicuo) ──────────────────────

    public boolean estaRealizada()  { return this.estado == EstadoReserva.REALIZADA; }
    public boolean estaCancelada()  { return this.estado == EstadoReserva.CANCELADA; }
    public boolean estaConfirmada() { return this.estado == EstadoReserva.CONFIRMADA; }
    public boolean estaCreada()     { return this.estado == EstadoReserva.CREADA; }

    // ── Lógica de dominio estática ──────────────────────────────────────────────

    /**
     * Calcula las ganancias de un conjunto de reservas.
     * REGLA DE NEGOCIO: solo se contabilizan las reservas en estado REALIZADA.
     * (Corrige el bug de PB-08 en la capa de dominio.)
     */
    public static Long calcularGanancia(List<Reserva> reservas) {
        return reservas.stream()
                .filter(Reserva::estaRealizada)
                .mapToLong(r -> r.getPrecio().getMonto())
                .sum();
    }

    // ── Gestión de Domain Events ────────────────────────────────────────────────

    /**
     * Extrae y limpia los eventos acumulados por el aggregate.
     *
     * Patrón: "collect and dispatch"
     *   1. Aggregate acumula eventos en su lista interna.
     *   2. Application Service llama save() → eventos persisten en BD.
     *   3. Application Service llama pullEvents() → obtiene los eventos.
     *   4. Application Service los publica en el EventBus.
     *
     * ¿Por qué no publicar directamente desde el aggregate?
     *   Porque si el save falla, los eventos NO deben emitirse.
     *   Publicar desde el aggregate rompería esa garantía.
     */
    public List<DomainEvent> pullEvents() {
        List<DomainEvent> emitir = new ArrayList<>(this.eventos);
        this.eventos.clear();
        return Collections.unmodifiableList(emitir);
    }

    // ── Getters ─────────────────────────────────────────────────────────────────
    // No hay setters públicos para campos de negocio.
    // El estado solo cambia a través de los métodos de negocio (confirmar, cancelar, etc.)

    public Long getId()               { return id; }
    public Long getBarberoId()        { return barberoId; }
    public Long getClienteId()        { return clienteId; }
    public Long getServicioId()       { return servicioId; }
    public Long getHorarioRangoId()   { return horarioRangoId; }
    public EstadoReserva getEstado()  { return estado; }
    public Precio getPrecio()         { return precio; }
    public String getMotivoDescripcion() { return motivoDescripcion; }
    public String getAdicionales()    { return adicionales; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public LocalDate getFechaReserva()    { return fechaReserva; }
    public Integer getEstRecompensa() { return estRecompensa; }
    public String getUrlPago()        { return urlPago; }

    /**
     * Solo el repositorio asigna el ID después de persistir.
     * No es un setter de negocio — es infraestructura.
     */
    public void asignarId(Long id) { this.id = id; }

    public static Reserva reconstitute(
            Long id,
            Long barberoId,
            Long clienteId,
            Long servicioId,
            Long horarioRangoId,
            EstadoReserva estado,
            Precio precio,
            String motivoDescripcion,
            String adicionales,
            LocalDateTime fechaCreacion,
            LocalDate fechaReserva,
            Integer estRecompensa,
            String urlPago

    ) {
        Reserva reserva = new Reserva();
        reserva.id = id;
        reserva.barberoId = barberoId;
        reserva.clienteId = clienteId;
        reserva.servicioId = servicioId;
        reserva.horarioRangoId = horarioRangoId;
        reserva.estado = estado;
        reserva.precio = precio;
        reserva.motivoDescripcion = motivoDescripcion;
        reserva.adicionales = adicionales;
        reserva.fechaCreacion = fechaCreacion;
        reserva.fechaReserva = fechaReserva;
        reserva.estRecompensa = estRecompensa;
        reserva.urlPago = urlPago;
        return reserva;
    }
}
