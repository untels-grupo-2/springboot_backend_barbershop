package com.diamondbarbershop.apibarbershop.notificaciones.infrastructure.rest;

import com.diamondbarbershop.apibarbershop.dtos.common.ApiResponse;
import com.diamondbarbershop.apibarbershop.exceptions.UsuarioExistenteException;
import com.diamondbarbershop.apibarbershop.models.Usuario;
import com.diamondbarbershop.apibarbershop.notificaciones.domain.port.in.RegistrarDeviceTokenUseCase;
import com.diamondbarbershop.apibarbershop.repositories.IUsuariosRepository;
import com.diamondbarbershop.apibarbershop.util.MensajeError;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoint para que la app móvil registre el token FCM del dispositivo (PB-41).
 *
 * Flujo esperado en el cliente Android:
 *   1. Al iniciar sesión, llamar a FirebaseMessaging.getInstance().getToken().
 *   2. Cuando llegue el token, hacer POST a este endpoint con el JWT del usuario.
 *   3. La app debe re-llamar este endpoint si recibe onNewToken() en su
 *      FirebaseMessagingService — los tokens pueden cambiar.
 *
 * Solo accesible por usuarios autenticados — no abrimos esto al público porque
 * un atacante podría llenar la tabla de tokens spam.
 *
 * NOTA: el usuarioId se extrae del Authentication, no del body. Eso evita que
 * un usuario registre tokens en nombre de otro.
 */
@RestController
@RequestMapping("/api/notificaciones/device-token")
@RequiredArgsConstructor
public class DeviceTokenController {

    private final RegistrarDeviceTokenUseCase registrarDeviceTokenUseCase;
    private final IUsuariosRepository usuariosRepository;

    @PostMapping
    public ResponseEntity<ApiResponse<Object>> registrar(
            @Valid @RequestBody DtoRegistrarDeviceToken dto,
            Authentication authentication
    ) {
        Usuario usuario = usuariosRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new UsuarioExistenteException(MensajeError.USUARIO_NO_EXISTENTE));

        registrarDeviceTokenUseCase.registrar(
                new RegistrarDeviceTokenUseCase.RegistrarDeviceTokenCommand(
                        usuario.getUsuario_id(),
                        dto.getToken(),
                        dto.getPlataforma()
                )
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.succes("Device token registrado", null));
    }
}
