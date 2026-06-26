package com.diamondbarbershop.apibarbershop.identidad.infrastructure.rest;

import com.diamondbarbershop.apibarbershop.identidad.infrastructure.rest.dto.DtoLogin;
import com.diamondbarbershop.apibarbershop.identidad.infrastructure.rest.dto.DtoRefreshToken;
import com.diamondbarbershop.apibarbershop.identidad.infrastructure.rest.dto.DtoRegistro;
import com.diamondbarbershop.apibarbershop.identidad.infrastructure.rest.dto.DtoResetPassword;
import com.diamondbarbershop.apibarbershop.identidad.infrastructure.rest.dto.DtoLoginResponse;
import com.diamondbarbershop.apibarbershop.shared.infrastructure.rest.ApiResponse;
import com.diamondbarbershop.apibarbershop.identidad.domain.port.in.AutenticarUseCase;
import com.diamondbarbershop.apibarbershop.identidad.domain.port.in.RegistrarUsuarioUseCase;
import com.diamondbarbershop.apibarbershop.identidad.domain.port.in.ResetPasswordUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Controller migrado del BC Identidad — endpoints de autenticación.
 */
@RestController
@RequestMapping("/autenticacion")
@RequiredArgsConstructor
public class RestControllerAuth {

    private static final String ROL_USER = "USER";
    private static final String ROL_ADMIN = "ADMIN";

    private final RegistrarUsuarioUseCase registrarUsuarioUseCase;
    private final AutenticarUseCase autenticarUseCase;
    private final ResetPasswordUseCase resetPasswordUseCase;

    @PostMapping("/registro/cliente")
    public ResponseEntity<ApiResponse<Object>> registrar(
            @RequestBody DtoRegistro dtoRegistro,
            Authentication authentication) {

        if (authentication.getAuthorities().stream()
                .noneMatch(auth -> ROL_ADMIN.equals(auth.getAuthority()))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                    new ApiResponse<>(HttpStatus.FORBIDDEN.value(),
                            "Acceso denegado: Solo los administradores pueden registrar usuarios", null));
        }
        try {
            registrarUsuarioUseCase.registrar(dtoRegistro, ROL_USER);
            return ResponseEntity.ok(
                    new ApiResponse<>(HttpStatus.OK.value(), "Registro de usuario cliente exitoso", null));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(
                    new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), e.getMessage(), null));
        }
    }

    @PostMapping("/registro/admin")
    public ResponseEntity<ApiResponse<Object>> registrarAdmin(
            @RequestBody DtoRegistro dtoRegistro,
            Authentication authentication) {

        if (authentication.getAuthorities().stream()
                .noneMatch(auth -> ROL_ADMIN.equals(auth.getAuthority()))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                    new ApiResponse<>(HttpStatus.FORBIDDEN.value(),
                            "Acceso denegado: solo los administradores pueden registrar usuario", null));
        }
        try {
            registrarUsuarioUseCase.registrar(dtoRegistro, ROL_ADMIN);
            return ResponseEntity.ok(
                    new ApiResponse<>(HttpStatus.OK.value(), "Registro de usuario administrador exitoso", null));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(
                    new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), e.getMessage(), null));
        }
    }

    /**
     * Endpoint de bootstrap — permite crear el primer admin del sistema sin autenticación previa.
     * Una vez que existe al menos un admin, este endpoint debería deshabilitarse o protegerse.
     */
    @PostMapping("/bootstrap/admin")
    public ResponseEntity<ApiResponse<Object>> bootstrapAdmin(@RequestBody DtoRegistro dtoRegistro) {
        try {
            registrarUsuarioUseCase.registrar(dtoRegistro, ROL_ADMIN);
            return ResponseEntity.ok(
                    new ApiResponse<>(HttpStatus.OK.value(), "Registro de usuario administrador exitoso", null));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(
                    new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), e.getMessage(), null));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Object>> login(@RequestBody @Valid DtoLogin dtoLogin) {
        try {
            DtoLoginResponse response = autenticarUseCase.login(dtoLogin);
            return ResponseEntity.ok(ApiResponse.succes("Inicio de sesión exitoso", response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    ApiResponse.error("Credenciales Inválidas", null));
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<Object>> resetPassword(@RequestBody @Valid DtoResetPassword dto) {
        resetPasswordUseCase.resetear(dto);
        return ResponseEntity.ok(ApiResponse.succes("Contraseña cambiada correctamente", null));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse<Object>> refreshToken(@RequestBody @Valid DtoRefreshToken dto) {
        String newToken = autenticarUseCase.renovarToken(dto);
        String newRefreshToken = autenticarUseCase.renovarRefreshToken(dto.getRefreshToken());
        return ResponseEntity.ok(ApiResponse.succes("Token renovado exitosamente.", Map.of(
                "token", newToken,
                "refreshToken", newRefreshToken
        )));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Object>> logout(Authentication authentication) {
        autenticarUseCase.logout(authentication.getName());
        return ResponseEntity.ok(ApiResponse.succes("Logout exitoso.", null));
    }
}
