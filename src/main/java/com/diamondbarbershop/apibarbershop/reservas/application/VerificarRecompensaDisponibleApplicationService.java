package com.diamondbarbershop.apibarbershop.reservas.application;

import com.diamondbarbershop.apibarbershop.identidad.domain.exception.UsuarioExistenteException;
import com.diamondbarbershop.apibarbershop.identidad.infrastructure.persistance.UsuarioJpaEntity;
import com.diamondbarbershop.apibarbershop.reservas.infrastructure.persistance.IReservaJpaRepository;
import com.diamondbarbershop.apibarbershop.identidad.infrastructure.persistance.IUsuarioJpaRepository;
import com.diamondbarbershop.apibarbershop.reservas.domain.port.in.VerificarRecompensaDisponibleUseCase;
import com.diamondbarbershop.apibarbershop.util.MensajeError;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Application service — verifica si el cliente tiene recompensa disponible.
 *
 * Regla: el cliente tiene recompensa si ha acumulado ≥ 7 reservas con
 * estRecompensa = 0 (no consumidas previamente).
 */
@Service
@RequiredArgsConstructor
public class VerificarRecompensaDisponibleApplicationService implements VerificarRecompensaDisponibleUseCase {

    private static final int RESERVAS_PARA_RECOMPENSA = 7;

    private final IUsuarioJpaRepository usuariosRepository;
    private final IReservaJpaRepository reservaJpaRepository;

    @Override
    @Transactional(readOnly = true)
    public boolean tieneRecompensa(String username) {
        UsuarioJpaEntity usuario = usuariosRepository.findByUsername(username)
                .orElseThrow(() -> new UsuarioExistenteException(MensajeError.USUARIO_NO_EXISTENTE));

        long reservasPendientes = reservaJpaRepository.findByUsuario(usuario).stream()
                .filter(r -> r.getEstRecompensa() != null && r.getEstRecompensa() == 0)
                .count();

        return reservasPendientes >= RESERVAS_PARA_RECOMPENSA;
    }
}
