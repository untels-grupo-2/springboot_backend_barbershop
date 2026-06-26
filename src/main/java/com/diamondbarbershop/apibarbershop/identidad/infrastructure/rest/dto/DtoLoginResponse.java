package com.diamondbarbershop.apibarbershop.identidad.infrastructure.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DtoLoginResponse {
    private String token;
    private String refreshToken;
}