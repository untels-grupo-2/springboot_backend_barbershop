package com.diamondbarbershop.apibarbershop.notificaciones.infrastructure.persistance;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data repo para NotificacionAdminJpaEntity.
 */
@Repository
public interface INotificacionAdminJpaRepository extends JpaRepository<NotificacionAdminJpaEntity, Long> {

    Page<NotificacionAdminJpaEntity> findByLeida(Boolean leida, Pageable pageable);
}
