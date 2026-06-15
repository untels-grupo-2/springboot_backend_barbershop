package com.diamondbarbershop.apibarbershop.notificaciones.infrastructure.persistance;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entidad JPA de notificación al admin. Solo vive en infrastructure.
 *
 * Mapea a la tabla `notificaciones_admin` creada en V3.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "notificaciones_admin")
public class NotificacionAdminJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titulo;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String cuerpo;

    @Column(nullable = false, length = 50)
    private String tipo;

    @Column(nullable = false)
    private Boolean leida = false;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;
}
