package com.diamondbarbershop.apibarbershop.identidad.application;

import com.diamondbarbershop.apibarbershop.identidad.infrastructure.rest.dto.DtoResetPassword;
import com.diamondbarbershop.apibarbershop.identidad.domain.exception.CredencialesInvalidasException;
import com.diamondbarbershop.apibarbershop.identidad.domain.exception.TokenInvalidoOExpiradoException;
import com.diamondbarbershop.apibarbershop.identidad.domain.port.in.ResetPasswordUseCase;
import com.diamondbarbershop.apibarbershop.identidad.infrastructure.persistance.UsuarioJpaEntity;
import com.diamondbarbershop.apibarbershop.identidad.infrastructure.persistance.IUsuarioJpaRepository;
import com.diamondbarbershop.apibarbershop.util.MensajeError;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Application service — restablecimiento de contraseña vía token de email.
 *
 * Validaciones:
 *   - Token no vacío.
 *   - newPassword == confirmPassword.
 *   - Token válido (existe en BD).
 *   - Token no expirado (menos de 1 hora desde lastTokenRequest).
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ResetPasswordApplicationService implements ResetPasswordUseCase {

    private final PasswordEncoder passwordEncoder;
    private final IUsuarioJpaRepository usuariosRepository;

    @Override
    @Transactional
    public void resetear(DtoResetPassword dto) {
        if (dto.getTokenPassword() == null || dto.getTokenPassword().isEmpty()) {
            throw new TokenInvalidoOExpiradoException(MensajeError.TOKEN_VACIO);
        }

        if (!dto.getNewPassword().equals(dto.getConfirmPassword())) {
            throw new CredencialesInvalidasException(MensajeError.PASSWORDS_NO_COINCIDEN);
        }

        UsuarioJpaEntity usuario = usuariosRepository.findByTokenPassword(dto.getTokenPassword())
                .orElseThrow(() -> new TokenInvalidoOExpiradoException(MensajeError.TOKEN_INVALIDO));

        if (usuario.getLastTokenRequest() != null
                && usuario.getLastTokenRequest().isBefore(LocalDateTime.now().minusHours(1))) {
            throw new TokenInvalidoOExpiradoException(MensajeError.TOKEN_EXPIRADO);
        }

        usuario.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        usuario.setTokenPassword(null);
        usuario.setLastTokenRequest(null);
        usuariosRepository.save(usuario);

        log.info("Contraseña restablecida para usuario: {}", usuario.getUsername());
    }
}
