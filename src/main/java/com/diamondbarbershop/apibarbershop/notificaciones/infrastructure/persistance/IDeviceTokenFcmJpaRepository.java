package com.diamondbarbershop.apibarbershop.notificaciones.infrastructure.persistance;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data repo para DeviceTokenFcmJpaEntity. Es DETALLE de infraestructura,
 * por eso vive en notificaciones/infrastructure (no en la carpeta legacy
 * `repositories/`).
 */
@Repository
public interface IDeviceTokenFcmJpaRepository extends JpaRepository<DeviceTokenFcmJpaEntity, Long> {

    Optional<DeviceTokenFcmJpaEntity> findByToken(String token);

    List<DeviceTokenFcmJpaEntity> findByUsuarioIdIn(List<Long> usuarioIds);
}
