package com.diamondbarbershop.apibarbershop.agenda.infrastructure.persistance;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidad JPA — tabla `tipo_horarios`. Datos catálogo del BC Agenda.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tipo_horarios")
public class TipoHorarioJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tipoHorario_id")
    private Long id;

    @Column(nullable = false)
    private String nombre;
}
