package com.diamondbarbershop.apibarbershop.notificaciones.infrastructure.persistance;

import com.diamondbarbershop.apibarbershop.notificaciones.domain.port.out.DeviceTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Adapter — implementa el puerto DeviceTokenRepository delegando en el
 * Spring Data repo (JPA).
 */
@Component
@RequiredArgsConstructor
public class DeviceTokenJpaAdapter implements DeviceTokenRepository {

    private final IDeviceTokenFcmJpaRepository jpaRepository;

    @Override
    public void guardarOActualizar(Long usuarioId, String token, String plataforma) {
        // Idempotente: si el token ya existe, solo actualiza usuarioId/plataforma.
        // La columna updated_at se actualiza sola gracias a ON UPDATE CURRENT_TIMESTAMP.
        Optional<DeviceTokenFcmJpaEntity> existente = jpaRepository.findByToken(token);
        DeviceTokenFcmJpaEntity entity = existente.orElseGet(DeviceTokenFcmJpaEntity::new);
        entity.setUsuarioId(usuarioId);
        entity.setToken(token);
        entity.setPlataforma(plataforma);
        jpaRepository.save(entity);
    }

    @Override
    public List<String> obtenerTokensDeUsuarios(List<Long> usuarioIds) {
        if (usuarioIds == null || usuarioIds.isEmpty()) {
            return Collections.emptyList();
        }
        return jpaRepository.findByUsuarioIdIn(usuarioIds)
                .stream()
                .map(DeviceTokenFcmJpaEntity::getToken)
                .toList();
    }
}
