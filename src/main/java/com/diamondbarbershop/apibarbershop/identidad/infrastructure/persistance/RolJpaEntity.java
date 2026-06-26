package com.diamondbarbershop.apibarbershop.identidad.infrastructure.persistance;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidad JPA — tabla `roles`.
 *
 * Detalle de infraestructura del BC Identidad. NO debe filtrarse hacia
 * domain ni application.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "roles")
public class RolJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rol_id")
    private Long rol_id;

    @Column(nullable = false)
    private String name;
}
