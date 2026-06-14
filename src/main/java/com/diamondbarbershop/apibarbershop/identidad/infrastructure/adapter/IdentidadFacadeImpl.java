package com.diamondbarbershop.apibarbershop.identidad.infrastructure.adapter;

import com.diamondbarbershop.apibarbershop.repositories.IUsuariosRepository;
import com.diamondbarbershop.apibarbershop.reservas.domain.port.out.IdentidadFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Implementación de IdentidadFacade — vive en el BC Identidad.
 *
 * Única clase del sistema que conoce los detalles internos del BC Identidad
 * (entidad JPA Usuario, repositorio, roles, JWT, refresh tokens).
 *
 * NOTA sobre estaActivoUsuario:
 *   La entidad Usuario actual NO tiene un campo "estado" — todo usuario
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

    private final IUsuariosRepository usuariosRepository;

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
}
