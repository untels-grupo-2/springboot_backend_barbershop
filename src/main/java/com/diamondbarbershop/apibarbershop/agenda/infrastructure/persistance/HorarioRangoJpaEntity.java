package com.diamondbarbershop.apibarbershop.agenda.infrastructure.persistance;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidad JPA — tabla `horario_rangos`. Franjas horarias del BC Agenda.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "horario_rangos")
public class HorarioRangoJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "horarioRango_id")
    private Long horarioRango_id;

    @Column(nullable = false)
    private String rango;

    @ManyToOne
    @JoinColumn(name = "tipoHorario_id", nullable = false)
    private TipoHorarioJpaEntity tipoHorario;
}
