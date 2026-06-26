package com.diamondbarbershop.apibarbershop.identidad.application;

import com.diamondbarbershop.apibarbershop.identidad.infrastructure.rest.dto.DtoUsuario;
import com.diamondbarbershop.apibarbershop.identidad.domain.exception.UsuarioExistenteException;
import com.diamondbarbershop.apibarbershop.identidad.domain.port.in.ActualizarUsuarioUseCase;
import com.diamondbarbershop.apibarbershop.identidad.infrastructure.persistance.UsuarioJpaEntity;
import com.diamondbarbershop.apibarbershop.identidad.infrastructure.persistance.IUsuarioJpaRepository;
import com.diamondbarbershop.apibarbershop.shared.domain.port.out.SubidorImagen;
import com.diamondbarbershop.apibarbershop.util.MensajeError;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * Application service para actualización de datos del usuario.
 *
 * Cubre dos casos:
 *   - actualizar(usuarioId, ...): el admin actualiza un usuario por ID.
 *   - actualizarPorUsername(username, ...): el usuario actualiza su propio perfil.
 *
 * Usa el SubidorImagen compartido (shared/) para Cloudinary.
 */
@Service
@RequiredArgsConstructor
public class ActualizarUsuarioApplicationService implements ActualizarUsuarioUseCase {

    private static final String CARPETA_IMAGENES = "usuarios";

    private final IUsuarioJpaRepository usuariosRepository;
    private final SubidorImagen subidorImagen;

    @Override
    @Transactional
    public void actualizar(Long usuarioId, DtoUsuario dtoUsuario, MultipartFile imagen) {
        UsuarioJpaEntity usuario = usuariosRepository.findById(usuarioId)
                .orElseThrow(() -> new UsuarioExistenteException(MensajeError.USUARIO_NO_EXISTENTE));
        aplicarCambios(usuario, dtoUsuario, imagen);
    }

    @Override
    @Transactional
    public void actualizarPorUsername(String username, DtoUsuario dtoUsuario, MultipartFile imagen) {
        UsuarioJpaEntity usuario = usuariosRepository.findByUsername(username)
                .orElseThrow(() -> new UsuarioExistenteException(MensajeError.USUARIO_NO_EXISTENTE));
        aplicarCambios(usuario, dtoUsuario, imagen);
    }

    private void aplicarCambios(UsuarioJpaEntity usuario, DtoUsuario dto, MultipartFile imagen) {
        if (imagen != null) {
            String urlImagen = subidorImagen.subir(imagen, CARPETA_IMAGENES);
            usuario.setUrlUsuario(urlImagen);
        }
        usuario.setNombre(dto.getNombre());
        usuario.setApellido(dto.getApellido());
        usuario.setEmail(dto.getEmail());
        usuario.setCelular(dto.getCelular());
        usuariosRepository.save(usuario);
    }
}
