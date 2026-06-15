package com.diamondbarbershop.apibarbershop.notificaciones.infrastructure.persistance;

import com.diamondbarbershop.apibarbershop.notificaciones.domain.port.out.NotificacionAdmin;
import com.diamondbarbershop.apibarbershop.notificaciones.domain.port.out.NotificacionAdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

/**
 * Adapter — implementa el puerto NotificacionAdminRepository delegando en
 * el Spring Data repo. Traduce entre JpaEntity y el record de dominio.
 */
@Component
@RequiredArgsConstructor
public class NotificacionAdminJpaAdapter implements NotificacionAdminRepository {

    private final INotificacionAdminJpaRepository jpaRepository;

    @Override
    public NotificacionAdmin guardar(String titulo, String cuerpo, String tipo) {
        NotificacionAdminJpaEntity entity = new NotificacionAdminJpaEntity();
        entity.setTitulo(titulo);
        entity.setCuerpo(cuerpo);
        entity.setTipo(tipo);
        entity.setLeida(false);
        NotificacionAdminJpaEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Page<NotificacionAdmin> listar(Boolean soloNoLeidas, Pageable pageable) {
        Page<NotificacionAdminJpaEntity> page = Boolean.TRUE.equals(soloNoLeidas)
                ? jpaRepository.findByLeida(false, pageable)
                : jpaRepository.findAll(pageable);
        return page.map(this::toDomain);
    }

    @Override
    public void marcarComoLeida(Long notificacionId) {
        jpaRepository.findById(notificacionId).ifPresent(entity -> {
            if (Boolean.FALSE.equals(entity.getLeida())) {
                entity.setLeida(true);
                jpaRepository.save(entity);
            }
        });
    }

    private NotificacionAdmin toDomain(NotificacionAdminJpaEntity entity) {
        return new NotificacionAdmin(
                entity.getId(),
                entity.getTitulo(),
                entity.getCuerpo(),
                entity.getTipo(),
                Boolean.TRUE.equals(entity.getLeida()),
                entity.getCreatedAt()
        );
    }
}
