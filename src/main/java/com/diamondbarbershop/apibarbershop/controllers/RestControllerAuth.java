package com.diamondbarbershop.apibarbershop.controllers;

import com.diamondbarbershop.apibarbershop.dtos.auth.request.DtoLogin;
import com.diamondbarbershop.apibarbershop.dtos.auth.request.DtoRefreshToken;
import com.diamondbarbershop.apibarbershop.dtos.auth.request.DtoRegistro;
import com.diamondbarbershop.apibarbershop.dtos.auth.response.DtoLoginResponse;
import com.diamondbarbershop.apibarbershop.dtos.common.ApiResponse;
import com.diamondbarbershop.apibarbershop.dtos.auth.request.DtoResetPassword;
import com.diamondbarbershop.apibarbershop.services.AuthService;
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

@RestController
@RequestMapping("/autenticacion")
@RequiredArgsConstructor
public class RestControllerAuth {

    private final AuthService authService;

    @PostMapping("/registro/cliente")
    public ResponseEntity<ApiResponse<Object>> registrar(@RequestBody DtoRegistro dtoRegistro, Authentication authentication){

        if (authentication.getAuthorities().stream().noneMatch(auth ->auth.getAuthority().equals("ADMIN"))){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                    new ApiResponse<>(HttpStatus.FORBIDDEN.value(), "Acceso denegado: Solo los administradores pueden registrar usuarios", null)
            );
        }
        try {
            authService.registrarUsuario(dtoRegistro);
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ApiResponse<>(HttpStatus.OK.value(), "Registro de usuario cliente exitoso", null)
            );
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), e.getMessage(),null)
            );
        }
    }


    @PostMapping("/registro/admin")
    public ResponseEntity<ApiResponse<Object>> registrarAdmin(@RequestBody DtoRegistro dtoRegistro, Authentication authentication ) {

        if (authentication.getAuthorities().stream().noneMatch(auth -> auth.getAuthority().equals("ADMIN"))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                    new ApiResponse<>(HttpStatus.FORBIDDEN.value(), "Acceso denegado: solo los administradores pueden registrar usuario", null)
            );
        }
        try {
            authService.registrarAdministrador(dtoRegistro);
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ApiResponse<>(HttpStatus.OK.value(), "Registro de usuario administrador exitoso", null)
            );
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), e.getMessage(),null)
            );
        }

    }

    @PostMapping("/bootstrap/admin")
    public ResponseEntity<ApiResponse<Object>> registrarAdmin2(@RequestBody DtoRegistro dtoRegistro, Authentication authentication ) {
        try {
            authService.registrarAdministrador(dtoRegistro);
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ApiResponse<>(HttpStatus.OK.value(), "Registro de usuario administrador exitoso", null)
            );
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), e.getMessage(),null)
            );
        }

    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Object>> login(@RequestBody @Valid DtoLogin dtoLogin) {
        try {
            DtoLoginResponse dtoLoginResponse = authService.login(dtoLogin);
            return ResponseEntity.ok(ApiResponse.succes("Inicio de sesión existoso", dtoLoginResponse));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse
                    .error("Credenciales Inválidas",null) );
        }
    }


    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<Object>> resetPassword(@RequestBody @Valid DtoResetPassword dtoResetPassword){
        authService.resetPassword(dtoResetPassword);
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.succes("Contraseña cambiada correctamente",null)
        );
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse<Object>> refreshToken(@RequestBody @Valid DtoRefreshToken dtoRefreshToken){
        String newToken = authService.renovarToken(dtoRefreshToken);
        String newRefreshToken = authService.renovarRefreshToken(dtoRefreshToken.getRefreshToken());
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.succes("Token renovado exitosamente.", Map.of(
                        "token", newToken,
                        "refreshToken",newRefreshToken
                ))
        );
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Object>> logout(Authentication authentication) {
        String username = authentication.getName();
        authService.logout(username);

        return ResponseEntity.ok(ApiResponse.succes("Logout exitoso.", null));
    }
}
