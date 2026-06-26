package com.diamondbarbershop.apibarbershop.identidad.application;

import com.diamondbarbershop.apibarbershop.identidad.infrastructure.rest.dto.DtoUsuarioResponse;
import com.diamondbarbershop.apibarbershop.identidad.domain.exception.UsuarioExistenteException;
import com.diamondbarbershop.apibarbershop.identidad.domain.port.in.ConsultarUsuariosUseCase;
import com.diamondbarbershop.apibarbershop.identidad.infrastructure.mapper.UsuarioMapper;
import com.diamondbarbershop.apibarbershop.identidad.infrastructure.persistance.UsuarioJpaEntity;
import com.diamondbarbershop.apibarbershop.identidad.infrastructure.persistance.IUsuarioJpaRepository;
import com.diamondbarbershop.apibarbershop.util.MensajeError;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Application service para consultas de usuario.
 */
@Service
@RequiredArgsConstructor
public class ConsultarUsuariosApplicationService implements ConsultarUsuariosUseCase {

    private static final String ROL_CLIENTE = "USER";

    private final IUsuarioJpaRepository usuariosRepository;

    @Override
    @Transactional(readOnly = true)
    public List<DtoUsuarioResponse> listarClientes() {
        return usuariosRepository.findAll().stream()
                .filter(u -> u.getRoles().stream().anyMatch(r -> ROL_CLIENTE.equals(r.getName())))
                .map(UsuarioMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public DtoUsuarioResponse obtener(Long id) {
        UsuarioJpaEntity usuario = usuariosRepository.findById(id)
                .orElseThrow(() -> new UsuarioExistenteException(MensajeError.USUARIO_NO_EXISTENTE));
        return UsuarioMapper.toDto(usuario);
    }

    @Override
    @Transactional(readOnly = true)
    public DtoUsuarioResponse obtenerPorUsername(String username) {
        UsuarioJpaEntity usuario = usuariosRepository.findByUsername(username)
                .orElseThrow(() -> new UsuarioExistenteException(MensajeError.USUARIO_NO_EXISTENTE));
        return UsuarioMapper.toDto(usuario);
    }
}
