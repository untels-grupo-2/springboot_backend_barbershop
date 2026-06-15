package com.diamondbarbershop.apibarbershop.notificaciones.infrastructure.persistance;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entidad JPA del token FCM. Solo vive en infrastructure — el dominio no la conoce.
 *
 * Mapea a la tabla `device_tokens_fcm` creada en V3.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "device_tokens_fcm")
public class DeviceTokenFcmJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    @Column(nullable = false, unique = true, length = 500)
    private String token;

    @Column(nullable = false, length = 20)
    private String plataforma;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private LocalDateTime updatedAt;
}
