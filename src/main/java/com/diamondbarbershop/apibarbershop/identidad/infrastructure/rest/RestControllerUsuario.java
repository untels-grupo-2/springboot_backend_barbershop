package com.diamondbarbershop.apibarbershop.identidad.infrastructure.rest;

import com.diamondbarbershop.apibarbershop.shared.infrastructure.rest.ApiResponse;
import com.diamondbarbershop.apibarbershop.identidad.infrastructure.rest.dto.DtoUsuario;
import com.diamondbarbershop.apibarbershop.identidad.infrastructure.rest.dto.DtoUsuarioResponse;
import com.diamondbarbershop.apibarbershop.shared.domain.exception.ImagenNoSubidaException;
import com.diamondbarbershop.apibarbershop.identidad.domain.port.in.ActualizarUsuarioUseCase;
import com.diamondbarbershop.apibarbershop.identidad.domain.port.in.ConsultarUsuariosUseCase;
import com.diamondbarbershop.apibarbershop.util.MensajeError;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Controller migrado del BC Identidad — endpoints de gestión de usuarios.
 */
@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class RestControllerUsuario {

    private final ConsultarUsuariosUseCase consultarUsuariosUseCase;
    private final ActualizarUsuarioUseCase actualizarUsuarioUseCase;

    @GetMapping
    public ResponseEntity<ApiResponse<List<DtoUsuarioResponse>>> listarUsuarios() {
        List<DtoUsuarioResponse> usuarios = consultarUsuariosUseCase.listarClientes();
        return ResponseEntity.ok(ApiResponse.succes("Lista de usuarios obtenida correctamente", usuarios));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DtoUsuarioResponse>> obtenerUsuarioPorId(@PathVariable Long id) {
        DtoUsuarioResponse usuario = consultarUsuariosUseCase.obtener(id);
        return ResponseEntity.ok(ApiResponse.succes("Usuario encontrado", usuario));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<DtoUsuarioResponse>> obtenerMiUsuario(Authentication authentication) {
        DtoUsuarioResponse usuario = consultarUsuariosUseCase.obtenerPorUsername(authentication.getName());
        return ResponseEntity.ok(ApiResponse.succes("Usuario encontrado", usuario));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Object>> actualizarUsuario(
            @PathVariable Long id,
            @RequestPart @Valid DtoUsuario dtoUsuario,
            @RequestPart(value = "imagen", required = false) MultipartFile imagen) {

        validarImagenSiExiste(imagen);
        actualizarUsuarioUseCase.actualizar(id, dtoUsuario, imagen);
        DtoUsuarioResponse dtoResponse = consultarUsuariosUseCase.obtener(id);
        return ResponseEntity.ok(ApiResponse.succes("Usuario actualizado exitosamente", dtoResponse));
    }

    @PutMapping(value = "/me", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Object>> actualizarMiPerfil(
            @RequestPart @Valid DtoUsuario dtoUsuario,
            @RequestPart(value = "imagen", required = false) MultipartFile imagen,
            Authentication authentication) {

        validarImagenSiExiste(imagen);
        actualizarUsuarioUseCase.actualizarPorUsername(authentication.getName(), dtoUsuario, imagen);
        DtoUsuarioResponse dtoResponse = consultarUsuariosUseCase.obtenerPorUsername(authentication.getName());
        return ResponseEntity.ok(ApiResponse.succes("Usuario actualizado exitosamente", dtoResponse));
    }

    private void validarImagenSiExiste(MultipartFile imagen) {
        if (imagen != null &&
                (imagen.getContentType() == null
                        || !imagen.getContentType().startsWith("image/"))) {
            throw new ImagenNoSubidaException(MensajeError.TIPO_ARCHIVO_NO_PERMITIDO);
        }
    }
}
