package com.diamondbarbershop.apibarbershop.identidad.infrastructure.mapper;

import com.diamondbarbershop.apibarbershop.identidad.infrastructure.rest.dto.DtoRegistro;
import com.diamondbarbershop.apibarbershop.identidad.infrastructure.rest.dto.DtoUsuarioResponse;
import com.diamondbarbershop.apibarbershop.identidad.infrastructure.persistance.UsuarioJpaEntity;

public class UsuarioMapper {

    public static UsuarioJpaEntity toEntity(DtoRegistro dtoRegistro){
        UsuarioJpaEntity usuario = new UsuarioJpaEntity();
        usuario.setUsername(dtoRegistro.getUsername());
        usuario.setPassword(dtoRegistro.getPassword());
        usuario.setNombre(dtoRegistro.getNombre());
        usuario.setApellido(dtoRegistro.getApellido());
        usuario.setEmail(dtoRegistro.getEmail());
        usuario.setCelular(dtoRegistro.getCelular());
        return usuario;
    }

    public static DtoUsuarioResponse toDto(UsuarioJpaEntity usuario){
        DtoUsuarioResponse dto = new DtoUsuarioResponse();
        dto.setUsuario_id(usuario.getUsuario_id());
        dto.setUsername(usuario.getUsername());
        dto.setNombre(usuario.getNombre());
        dto.setApellido(usuario.getApellido());
        dto.setEmail(usuario.getEmail());
        dto.setCelular(usuario.getCelular());
        dto.setUrlUsuario(usuario.getUrlUsuario());
        return dto;
    }
}
