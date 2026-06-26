package com.diamondbarbershop.apibarbershop.reservas.infrastructure.persistance;

import com.diamondbarbershop.apibarbershop.agenda.infrastructure.persistance.HorarioRangoJpaEntity;
import com.diamondbarbershop.apibarbershop.catalogo.infrastructure.persistance.ServicioJpaEntity;
import com.diamondbarbershop.apibarbershop.identidad.infrastructure.persistance.UsuarioJpaEntity;
import com.diamondbarbershop.apibarbershop.personal.infrastructure.persistance.BarberoJpaEntity;
import com.diamondbarbershop.apibarbershop.util.EstadoReserva;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entidad JPA — tabla `reservas`.
 *
 * Core domain del sistema. Tiene 4 relaciones cross-BC con:
 *   - BarberoJpaEntity (BC Personal)
 *   - UsuarioJpaEntity (BC Identidad)
 *   - HorarioRangoJpaEntity (BC Agenda)
 *   - ServicioJpaEntity (BC Catálogo)
 *
 * Las relaciones @ManyToOne viven aquí en infraestructura — el dominio
 * (com.diamondbarbershop.apibarbershop.reservas.domain.model.Reserva) se
 * mantiene puro usando solo Long como referencias entre BCs.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "reservas")
public class ReservaJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reserva_id")
    private Long reserva_id;

    @ManyToOne
    @JoinColumn(name = "barbero_id", nullable = false)
    private BarberoJpaEntity barbero;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private UsuarioJpaEntity usuario;

    @ManyToOne
    @JoinColumn(name = "horarioRango_id", nullable = false)
    private HorarioRangoJpaEntity horarioRango;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoReserva estado;

    @ManyToOne
    @JoinColumn(name = "servicio_id", nullable = false)
    private ServicioJpaEntity servicioEntity;

    @Column(nullable = false)
    private Long precioServicio;

    private String motivoDescripcion;
    private String adicionales;

    @Column(nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(nullable = false)
    private LocalDate fechaReserva;

    private Integer estRecompensa;
    private String urlPago;
}
