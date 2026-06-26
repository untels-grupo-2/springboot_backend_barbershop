package com.diamondbarbershop.apibarbershop.identidad.domain.port.in;

import com.diamondbarbershop.apibarbershop.identidad.infrastructure.rest.dto.DtoLogin;
import com.diamondbarbershop.apibarbershop.identidad.infrastructure.rest.dto.DtoRefreshToken;
import com.diamondbarbershop.apibarbershop.identidad.infrastructure.rest.dto.DtoLoginResponse;

/**
 * Puerto de entrada — agrupa las operaciones de autenticación.
 *
 * Agrupados juntos porque comparten estado (token + refresh token) y
 * dependencias (JwtGenerador, AuthenticationManager).
 */
public interface AutenticarUseCase {

    DtoLoginResponse login(DtoLogin dtoLogin);

    String renovarToken(DtoRefreshToken dtoRefreshToken);

    String renovarRefreshToken(String refreshToken);

    void logout(String username);
}
