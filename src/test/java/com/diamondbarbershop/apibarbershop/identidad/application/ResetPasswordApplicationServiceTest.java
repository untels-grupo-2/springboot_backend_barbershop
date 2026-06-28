package com.diamondbarbershop.apibarbershop.identidad.application;

import com.diamondbarbershop.apibarbershop.identidad.domain.exception.CredencialesInvalidasException;
import com.diamondbarbershop.apibarbershop.identidad.domain.exception.TokenInvalidoOExpiradoException;
import com.diamondbarbershop.apibarbershop.identidad.infrastructure.persistance.IUsuarioJpaRepository;
import com.diamondbarbershop.apibarbershop.identidad.infrastructure.persistance.UsuarioJpaEntity;
import com.diamondbarbershop.apibarbershop.identidad.infrastructure.rest.dto.DtoResetPassword;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResetPasswordApplicationServiceTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private IUsuarioJpaRepository usuariosRepository;

    @InjectMocks
    private ResetPasswordApplicationService service;

    private DtoResetPassword crearDto(String token, String newPassword, String confirmPassword) {
        DtoResetPassword dto = new DtoResetPassword();
        dto.setTokenPassword(token);
        dto.setNewPassword(newPassword);
        dto.setConfirmPassword(confirmPassword);
        return dto;
    }

    private UsuarioJpaEntity crearUsuarioConToken(String token, LocalDateTime lastTokenRequest) {
        UsuarioJpaEntity usuario = new UsuarioJpaEntity();
        usuario.setUsuario_id(1L);
        usuario.setUsername("juanperez");
        usuario.setPassword("oldEncoded");
        usuario.setNombre("Juan");
        usuario.setApellido("Perez");
        usuario.setEmail("juan@email.com");
        usuario.setCelular("999888777");
        usuario.setTokenPassword(token);
        usuario.setLastTokenRequest(lastTokenRequest);
        return usuario;
    }

    @Test
    @DisplayName("Debe resetear password exitosamente con token valido")
    void should_resetPassword_when_validToken() {
        DtoResetPassword dto = crearDto("valid-token", "newPass123", "newPass123");
        UsuarioJpaEntity usuario = crearUsuarioConToken("valid-token", LocalDateTime.now().minusMinutes(30));
        when(usuariosRepository.findByTokenPassword("valid-token")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.encode("newPass123")).thenReturn("encodedNewPass");

        service.resetear(dto);

        assertThat(usuario.getPassword()).isEqualTo("encodedNewPass");
        assertThat(usuario.getTokenPassword()).isNull();
        assertThat(usuario.getLastTokenRequest()).isNull();
        verify(usuariosRepository).save(usuario);
    }

    @Test
    @DisplayName("Debe lanzar TokenInvalidoOExpiradoException cuando el token es vacio")
    void should_throwException_when_tokenIsEmpty() {
        DtoResetPassword dto = crearDto("", "newPass123", "newPass123");

        assertThatThrownBy(() -> service.resetear(dto))
                .isInstanceOf(TokenInvalidoOExpiradoException.class);

        verify(usuariosRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe lanzar TokenInvalidoOExpiradoException cuando el token es null")
    void should_throwException_when_tokenIsNull() {
        DtoResetPassword dto = crearDto(null, "newPass123", "newPass123");

        assertThatThrownBy(() -> service.resetear(dto))
                .isInstanceOf(TokenInvalidoOExpiradoException.class);

        verify(usuariosRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe lanzar CredencialesInvalidasException cuando las passwords no coinciden")
    void should_throwException_when_passwordsDoNotMatch() {
        DtoResetPassword dto = crearDto("valid-token", "newPass123", "differentPass");

        assertThatThrownBy(() -> service.resetear(dto))
                .isInstanceOf(CredencialesInvalidasException.class);

        verify(usuariosRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe lanzar TokenInvalidoOExpiradoException cuando el token no existe en BD")
    void should_throwException_when_tokenNotFoundInDb() {
        DtoResetPassword dto = crearDto("invalid-token", "newPass123", "newPass123");
        when(usuariosRepository.findByTokenPassword("invalid-token")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.resetear(dto))
                .isInstanceOf(TokenInvalidoOExpiradoException.class);

        verify(usuariosRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe lanzar TokenInvalidoOExpiradoException cuando el token esta expirado")
    void should_throwException_when_tokenIsExpired() {
        DtoResetPassword dto = crearDto("expired-token", "newPass123", "newPass123");
        UsuarioJpaEntity usuario = crearUsuarioConToken("expired-token", LocalDateTime.now().minusHours(2));
        when(usuariosRepository.findByTokenPassword("expired-token")).thenReturn(Optional.of(usuario));

        assertThatThrownBy(() -> service.resetear(dto))
                .isInstanceOf(TokenInvalidoOExpiradoException.class);

        verify(usuariosRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe resetear exitosamente cuando lastTokenRequest es null")
    void should_resetPassword_when_lastTokenRequestIsNull() {
        DtoResetPassword dto = crearDto("valid-token", "newPass123", "newPass123");
        UsuarioJpaEntity usuario = crearUsuarioConToken("valid-token", null);
        when(usuariosRepository.findByTokenPassword("valid-token")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.encode("newPass123")).thenReturn("encodedNewPass");

        service.resetear(dto);

        assertThat(usuario.getPassword()).isEqualTo("encodedNewPass");
        verify(usuariosRepository).save(usuario);
    }
}
