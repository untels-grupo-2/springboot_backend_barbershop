package com.diamondbarbershop.apibarbershop.identidad.application;

import com.diamondbarbershop.apibarbershop.identidad.infrastructure.rest.dto.DtoRegistro;
import com.diamondbarbershop.apibarbershop.identidad.domain.exception.RolNoEncontradoException;
import com.diamondbarbershop.apibarbershop.identidad.domain.exception.UsuarioExistenteException;
import com.diamondbarbershop.apibarbershop.identidad.domain.port.in.RegistrarUsuarioUseCase;
import com.diamondbarbershop.apibarbershop.identidad.infrastructure.mapper.UsuarioMapper;
import com.diamondbarbershop.apibarbershop.identidad.infrastructure.persistance.RolJpaEntity;
import com.diamondbarbershop.apibarbershop.identidad.infrastructure.persistance.UsuarioJpaEntity;
import com.diamondbarbershop.apibarbershop.identidad.infrastructure.persistance.IRolJpaRepository;
import com.diamondbarbershop.apibarbershop.identidad.infrastructure.persistance.IUsuarioJpaRepository;
import com.diamondbarbershop.apibarbershop.util.MensajeError;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

/**
 * Application service — registra usuarios nuevos (clientes o administradores).
 *
 * NOTA: usa los JPA repos legacy directamente como atajo pragmático — el BC
 * Identidad mantiene esta deuda menor por la complejidad del dominio (Spring
 * Security, roles ManyToMany, password hashing). Será refactorizado a puerto
 * UsuarioRepository en una iteración futura si la complejidad crece.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RegistrarUsuarioApplicationService implements RegistrarUsuarioUseCase {

    private final PasswordEncoder passwordEncoder;
    private final IRolJpaRepository rolesRepository;
    private final IUsuarioJpaRepository usuariosRepository;

    @Override
    @Transactional
    public void registrar(DtoRegistro dtoRegistro, String rolNombre) {
        log.info("Intentando registrar usuario: {} con rol: {}", dtoRegistro.getUsername(), rolNombre);

        if (usuariosRepository.existsByUsername(dtoRegistro.getUsername())) {
            throw new UsuarioExistenteException(MensajeError.USUARIO_EXISTENTE);
        }
        if (usuariosRepository.existsByEmail(dtoRegistro.getEmail())) {
            throw new UsuarioExistenteException(MensajeError.CORREO_EXISTENTE);
        }

        UsuarioJpaEntity usuario = UsuarioMapper.toEntity(dtoRegistro);
        usuario.setPassword(passwordEncoder.encode(dtoRegistro.getPassword()));

        RolJpaEntity rol = rolesRepository.findByName(rolNombre)
                .orElseThrow(() -> new RolNoEncontradoException(
                        String.format(MensajeError.ROL_NO_ENCONTRADO, rolNombre)));
        usuario.setRoles(Collections.singletonList(rol));

        usuariosRepository.save(usuario);
        log.info("UsuarioJpaEntity registrado exitosamente: {}", dtoRegistro.getUsername());
    }
}
