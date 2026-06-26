package com.diamondbarbershop.apibarbershop.shared.infrastructure.rest;

import com.diamondbarbershop.apibarbershop.agenda.domain.exception.TipoHorarioNoEncotradoException;
import com.diamondbarbershop.apibarbershop.catalogo.domain.exception.ServicioNoEncontradoException;
import com.diamondbarbershop.apibarbershop.identidad.domain.exception.CredencialesInvalidasException;
import com.diamondbarbershop.apibarbershop.identidad.domain.exception.RolNoEncontradoException;
import com.diamondbarbershop.apibarbershop.identidad.domain.exception.TokenInvalidoOExpiradoException;
import com.diamondbarbershop.apibarbershop.identidad.domain.exception.UsuarioExistenteException;
import com.diamondbarbershop.apibarbershop.personal.domain.exception.BarberoNoEncontradoException;
import com.diamondbarbershop.apibarbershop.shared.domain.exception.EmailNoEnviadoException;
import com.diamondbarbershop.apibarbershop.shared.domain.exception.ImagenNoSubidaException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleException(Exception ex) {
        ApiResponse<Object> response = new ApiResponse<>(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Ocurrió un error inesperado: " + ex.getMessage(),
                null
        );
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(UsuarioExistenteException.class)
    public ResponseEntity<ApiResponse<Object>> handleUsuarioExistenteException(UsuarioExistenteException ex) {
        ApiResponse<Object> response = new ApiResponse<>(
                HttpStatus.BAD_REQUEST.value(),
                "Error de ejecución: " + ex.getMessage(),
                null
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AuthenticationCredentialsNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleAuthenticationException(AuthenticationCredentialsNotFoundException ex) {
        ApiResponse<Object> response = new ApiResponse<>(
                HttpStatus.UNAUTHORIZED.value(),
                "El token es inválido o ha expirado. Por favor, inicia sesión nuevamente.",
                null
        );
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }
    @ExceptionHandler(RolNoEncontradoException.class)
    public ResponseEntity<ApiResponse<Object>> handleRolNoEncontradoException(RolNoEncontradoException ex) {
        ApiResponse<Object> response = new ApiResponse<>(
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                null
        );
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(CredencialesInvalidasException.class)
    public ResponseEntity<ApiResponse<Object>> handleCredencialesInvalidasException(CredencialesInvalidasException ex) {
        ApiResponse<Object> response = new ApiResponse<>(
                HttpStatus.UNAUTHORIZED.value(),
                ex.getMessage(),
                null
        );
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(ServicioNoEncontradoException.class)
    public ResponseEntity<ApiResponse<Object>> handleServicioNoEncontradoException(ServicioNoEncontradoException ex){
        ApiResponse<Object> response = new ApiResponse<>(
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                null
        );
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(EmailNoEnviadoException.class)
    public ResponseEntity<ApiResponse<Object>> handleEmailNoEnviadoException(EmailNoEnviadoException ex){
        ApiResponse<Object> response = new ApiResponse<>(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                null
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(TokenInvalidoOExpiradoException.class)
    public ResponseEntity<ApiResponse<Object>> handleTokenInvalidoOExpiradoException(TokenInvalidoOExpiradoException ex){
        ApiResponse<Object> response = new ApiResponse<>(
                HttpStatus.UNAUTHORIZED.value(),
                ex.getMessage(),
                null
        );
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(BarberoNoEncontradoException.class)
    public ResponseEntity<ApiResponse<Object>> handleBarberoNoEncontradoException(BarberoNoEncontradoException ex){
        ApiResponse<Object> response = new ApiResponse<>(
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                null
        );
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(TipoHorarioNoEncotradoException.class)
    public ResponseEntity<ApiResponse<Object>> handleTipoHorarioNoEncotradoException(TipoHorarioNoEncotradoException ex){
        ApiResponse<Object> response = new ApiResponse<>(
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                null
        );
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ImagenNoSubidaException.class)
    public ResponseEntity<ApiResponse<Object>> handleImagenNoSubidaException(ImagenNoSubidaException ex){
        ApiResponse<Object> response = new ApiResponse<>(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                ex.getMessage(),
                null
        );
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
