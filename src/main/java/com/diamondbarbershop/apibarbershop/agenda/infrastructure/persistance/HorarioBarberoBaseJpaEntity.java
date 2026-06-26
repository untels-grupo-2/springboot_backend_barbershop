package com.diamondbarbershop.apibarbershop.agenda.infrastructure.persistance;

import com.diamondbarbershop.apibarbershop.personal.infrastructure.persistance.BarberoJpaEntity;
import com.diamondbarbershop.apibarbershop.util.DiaSemana;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidad JPA — tabla `horario_barbero_base`.
 *
 * Plantilla semanal recurrente del horario de un barbero (BC Agenda).
 * Tiene relación cross-BC con BarberoJpaEntity (BC Personal) — eso es
 * acoplamiento físico de JPA que NO contamina el dominio (los modelos
 * de dominio siguen usando solo Long barberoId).
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "horario_barbero_base",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"barbero_id", "tipoHorario_id", "dia"})
        })
public class HorarioBarberoBaseJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "horario_barbero_base_id")
    private Long horarioBarberoBase_id;

    @ManyToOne
    @JoinColumn(name = "barbero_id", nullable = false)
    private BarberoJpaEntity barbero;

    @ManyToOne
    @JoinColumn(name = "tipoHorario_id", nullable = false)
    private TipoHorarioJpaEntity tipoHorario;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DiaSemana dia;

    /** 1 = trabaja en este turno habitualmente; null = descansa. */
    private Integer est_id;

    /** 1 = entrada activa; 0 = inactiva (baja lógica). */
    private Integer estado;
}
