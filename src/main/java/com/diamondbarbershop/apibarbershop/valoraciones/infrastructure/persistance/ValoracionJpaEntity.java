package com.diamondbarbershop.apibarbershop.valoraciones.infrastructure.persistance;

import com.diamondbarbershop.apibarbershop.identidad.infrastructure.persistance.UsuarioJpaEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidad JPA — tabla `valoraciones`.
 *
 * Tiene relación cross-BC con UsuarioJpaEntity (BC Identidad).
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "valoraciones")
public class ValoracionJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "valoracion_id")
    private Long valoracion_id;

    private Integer valoracion;
    private Boolean util;
    private String mensaje;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private UsuarioJpaEntity usuario;

    private Integer estado;
}
