package com.diamondbarbershop.apibarbershop.identidad.application;

import com.diamondbarbershop.apibarbershop.identidad.infrastructure.rest.dto.DtoLogin;
import com.diamondbarbershop.apibarbershop.identidad.infrastructure.rest.dto.DtoRefreshToken;
import com.diamondbarbershop.apibarbershop.identidad.infrastructure.rest.dto.DtoLoginResponse;
import com.diamondbarbershop.apibarbershop.identidad.domain.exception.CredencialesInvalidasException;
import com.diamondbarbershop.apibarbershop.identidad.domain.exception.TokenInvalidoOExpiradoException;
import com.diamondbarbershop.apibarbershop.identidad.domain.port.in.AutenticarUseCase;
import com.diamondbarbershop.apibarbershop.identidad.infrastructure.persistance.UsuarioJpaEntity;
import com.diamondbarbershop.apibarbershop.identidad.infrastructure.persistance.IUsuarioJpaRepository;
import com.diamondbarbershop.apibarbershop.security.jwt.JwtGenerador;
import com.diamondbarbershop.apibarbershop.security.util.ConstantesSeguridad;
import com.diamondbarbershop.apibarbershop.util.MensajeError;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

/**
 * Application service — autenticación (login + refresh + logout).
 *
 * Encapsula la coordinación entre Spring Security (AuthenticationManager),
 * el generador de JWT y los refresh tokens. La lógica de hash de refresh
 * token (SHA-256) se mantiene como helper privado.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AutenticarApplicationService implements AutenticarUseCase {

    private final IUsuarioJpaRepository usuariosRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtGenerador jwtGenerador;

    @Override
    @Transactional
    public DtoLoginResponse login(DtoLogin dtoLogin) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(dtoLogin.getUsername(), dtoLogin.getPassword())
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

            String token = jwtGenerador.generarToken(authentication);
            String refreshToken = generarRefreshToken(dtoLogin.getUsername());

            log.info("Login exitoso para usuario: {}", dtoLogin.getUsername());
            return new DtoLoginResponse(token, refreshToken);
        } catch (Exception e) {
            throw new CredencialesInvalidasException(MensajeError.CREDENCIALES_INVALIDAS);
        }
    }

    @Override
    @Transactional
    public String renovarToken(DtoRefreshToken dtoRefreshToken) {
        String refreshToken = dtoRefreshToken.getRefreshToken();
        UsuarioJpaEntity usuario = usuariosRepository.findByRefreshToken(hashToken(refreshToken))
                .orElseThrow(() -> new TokenInvalidoOExpiradoException(MensajeError.TOKEN_INVALIDO));

        if (usuario.getRefreshTokenExpiryDate().isBefore(LocalDateTime.now())) {
            throw new TokenInvalidoOExpiradoException(MensajeError.TOKEN_EXPIRADO);
        }

        try {
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                    usuario.getUsername(),
                    null,
                    usuario.getRoles().stream()
                            .map(rol -> new SimpleGrantedAuthority(rol.getName()))
                            .toList()
            );
            return jwtGenerador.generarToken(auth);
        } catch (Exception e) {
            log.error("Error al renovar token", e);
            throw new CredencialesInvalidasException(MensajeError.CREDENCIALES_INVALIDAS);
        }
    }

    @Override
    @Transactional
    public String renovarRefreshToken(String refreshToken) {
        UsuarioJpaEntity usuario = usuariosRepository.findByRefreshToken(hashToken(refreshToken))
                .orElseThrow(() -> new TokenInvalidoOExpiradoException(MensajeError.TOKEN_INVALIDO));

        if (usuario.getRefreshTokenExpiryDate().isBefore(LocalDateTime.now())) {
            throw new TokenInvalidoOExpiradoException(MensajeError.TOKEN_EXPIRADO);
        }

        String newRefreshToken = UUID.randomUUID().toString();
        usuario.setRefreshToken(hashToken(newRefreshToken));
        usuario.setRefreshTokenExpiryDate(calcularExpiracion());
        usuariosRepository.save(usuario);
        return newRefreshToken;
    }

    @Override
    @Transactional
    public void logout(String username) {
        UsuarioJpaEntity usuario = usuariosRepository.findByUsername(username)
                .orElseThrow(() -> new CredencialesInvalidasException(MensajeError.CREDENCIALES_INVALIDAS));
        usuario.setRefreshToken(null);
        usuario.setRefreshTokenExpiryDate(null);
        usuariosRepository.save(usuario);
        log.info("Logout exitoso para usuario: {}", username);
    }

    // ── Helpers privados ─────────────────────────────────────────────────────────

    private String generarRefreshToken(String username) {
        String refreshToken = UUID.randomUUID().toString();
        UsuarioJpaEntity usuario = usuariosRepository.findByUsername(username)
                .orElseThrow(() -> new CredencialesInvalidasException(MensajeError.CREDENCIALES_INVALIDAS));
        usuario.setRefreshToken(hashToken(refreshToken));
        usuario.setRefreshTokenExpiryDate(calcularExpiracion());
        usuariosRepository.save(usuario);
        return refreshToken;
    }

    private LocalDateTime calcularExpiracion() {
        return Instant.now()
                .plusMillis(ConstantesSeguridad.JWT_REFRESH_EXPIRATION)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    private String hashToken(String refreshToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodeHash = digest.digest(refreshToken.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : encodeHash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error al hashear el token.", e);
        }
    }
}
