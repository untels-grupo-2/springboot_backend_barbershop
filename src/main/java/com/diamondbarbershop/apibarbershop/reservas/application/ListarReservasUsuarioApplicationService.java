package com.diamondbarbershop.apibarbershop.reservas.application;

import com.diamondbarbershop.apibarbershop.reservas.infrastructure.rest.dto.DtoReservaResponse;
import com.diamondbarbershop.apibarbershop.identidad.domain.exception.UsuarioExistenteException;
import com.diamondbarbershop.apibarbershop.reservas.infrastructure.mapper.ReservaEntityMapper;
import com.diamondbarbershop.apibarbershop.identidad.infrastructure.persistance.UsuarioJpaEntity;
import com.diamondbarbershop.apibarbershop.reservas.infrastructure.persistance.IReservaJpaRepository;
import com.diamondbarbershop.apibarbershop.identidad.infrastructure.persistance.IUsuarioJpaRepository;
import com.diamondbarbershop.apibarbershop.reservas.domain.port.in.ListarReservasUsuarioUseCase;
import com.diamondbarbershop.apibarbershop.util.MensajeError;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Application service — lista todas las reservas del cliente autenticado.
 *
 * Usado por el endpoint /reservas/mis-reservas que la app móvil cliente
 * llama para mostrar el historial de reservas del usuario.
 */
@Service
@RequiredArgsConstructor
public class ListarReservasUsuarioApplicationService implements ListarReservasUsuarioUseCase {

    private final IUsuarioJpaRepository usuariosRepository;
    private final IReservaJpaRepository reservaJpaRepository;
    private final ReservaEntityMapper reservaEntityMapper;

    @Override
    @Transactional(readOnly = true)
    public List<DtoReservaResponse> listarPorUsername(String username) {
        UsuarioJpaEntity usuario = usuariosRepository.findByUsername(username)
                .orElseThrow(() -> new UsuarioExistenteException(MensajeError.USUARIO_NO_EXISTENTE));
        return reservaEntityMapper.toDtoList(reservaJpaRepository.findByUsuario(usuario));
    }
}
