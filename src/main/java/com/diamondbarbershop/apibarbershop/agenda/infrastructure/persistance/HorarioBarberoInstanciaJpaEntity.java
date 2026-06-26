package com.diamondbarbershop.apibarbershop.agenda.infrastructure.persistance;

import com.diamondbarbershop.apibarbershop.personal.infrastructure.persistance.BarberoJpaEntity;
import com.diamondbarbershop.apibarbershop.util.DiaSemana;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Entidad JPA — tabla `horario_barbero_instancias`.
 *
 * Instancia concreta de horario para una fecha específica del BC Agenda.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "horario_barbero_instancias")
public class HorarioBarberoInstanciaJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "horarioBarberoInstancia_id")
    private Long horarioBarberoInstancia_id;

    @ManyToOne
    @JoinColumn(name = "barbero_id", nullable = false)
    private BarberoJpaEntity barbero;

    @ManyToOne
    @JoinColumn(name = "tipoHorario_id", nullable = false)
    private TipoHorarioJpaEntity tipoHorario;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DiaSemana dia;

    @Column(nullable = false)
    private LocalDate fecha;

    private Integer est_id;
}
