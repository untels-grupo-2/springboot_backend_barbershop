package com.diamondbarbershop.apibarbershop.identidad.infrastructure.adapter;

import com.diamondbarbershop.apibarbershop.identidad.infrastructure.persistance.UsuarioJpaEntity;
import com.diamondbarbershop.apibarbershop.identidad.infrastructure.persistance.IUsuarioJpaRepository;
import com.diamondbarbershop.apibarbershop.reservas.domain.port.out.IdentidadFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Implementación de IdentidadFacade — vive en el BC Identidad.
 *
 * Única clase del sistema que conoce los detalles internos del BC Identidad
 * (entidad JPA UsuarioJpaEntity, repositorio, roles, JWT, refresh tokens).
 *
 * NOTA sobre estaActivoUsuario:
 *   La entidad UsuarioJpaEntity actual NO tiene un campo "estado" — todo usuario
 *   que existe en BD se considera activo. La firma del método se mantiene
 *   por simetría con CatalogoFacade y PersonalFacade y para soportar
 *   evolución futura (cuando se agreguen estados como "suspendido" o
 *   "verificación pendiente", solo se actualiza este método).
 *
 * NOTA TRANSITORIA:
 *   Cuando se complete la migración hexagonal del BC Identidad,
 *   esta Facade leerá del nuevo UsuarioRepository (puerto de salida)
 *   en lugar del repo JPA legacy. La interfaz IdentidadFacade no cambia.
 */
@Component
@RequiredArgsConstructor
public class IdentidadFacadeImpl implements IdentidadFacade {

    private final IUsuarioJpaRepository usuariosRepository;

    @Override
    public boolean existeUsuario(Long usuarioId) {
        return usuariosRepository.findById(usuarioId).isPresent();
    }

    @Override
    public boolean estaActivoUsuario(Long usuarioId) {
        // Hoy: si existe, está activo. Cuando se agregue el campo estado,
        // se filtrará aquí.
        return existeUsuario(usuarioId);
    }

    @Override
    public Optional<String> obtenerEmail(Long usuarioId) {
        return usuariosRepository.findById(usuarioId).map(UsuarioJpaEntity::getEmail);
    }

    @Override
    public Optional<String> obtenerNombre(Long usuarioId) {
        return usuariosRepository.findById(usuarioId).map(UsuarioJpaEntity::getNombre);
    }

    @Override
    public List<Long> obtenerIdsUsuariosConRol(String rol) {
        return usuariosRepository.findByRoles_Name(rol)
                .stream()
                .map(UsuarioJpaEntity::getUsuario_id)
                .toList();
    }
}
