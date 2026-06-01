package com.diamondbarbershop.apibarbershop.services;

import com.diamondbarbershop.apibarbershop.dtos.valoracion.request.DtoValoracion;
import com.diamondbarbershop.apibarbershop.dtos.valoracion.response.DtoValoracionResponse;
import com.diamondbarbershop.apibarbershop.exceptions.UsuarioExistenteException;
import com.diamondbarbershop.apibarbershop.mappers.ValoracionEntityMapper;
import com.diamondbarbershop.apibarbershop.models.Usuario;
import com.diamondbarbershop.apibarbershop.models.ValoracionEntity;
import com.diamondbarbershop.apibarbershop.repositories.IUsuariosRepository;
import com.diamondbarbershop.apibarbershop.repositories.IValoracionRepository;
import com.diamondbarbershop.apibarbershop.util.MensajeError;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ValoracionService {

    private final IUsuariosRepository usuariosRepository;
    private final IValoracionRepository valoracionRepository;
    private final ValoracionEntityMapper valoracionEntityMapper;


    public void crear(DtoValoracion dtoValoracion, Authentication authentication) {
        String username = authentication.getName();
        Usuario usuario = usuariosRepository.findByUsername(username)
                        .orElseThrow(() -> new UsuarioExistenteException(MensajeError.USUARIO_NO_EXISTENTE));
        ValoracionEntity valoracionEntity = new ValoracionEntity();
        valoracionEntity.setValoracion(dtoValoracion.getValoracion());
        valoracionEntity.setUtil(dtoValoracion.getUtil());
        valoracionEntity.setMensaje(dtoValoracion.getMensaje());
        valoracionEntity.setUsuario(usuario);
        valoracionEntity.setEstado(1);
        valoracionRepository.save(valoracionEntity);
    }

    public List<DtoValoracionResponse> listarValoraciones() {
        List<ValoracionEntity> valoracionEntities = valoracionRepository.findAll().stream()
                .toList();
        return valoracionEntityMapper.toDtoList(valoracionEntities);
    }

    public void cambiarEstado(Long valoracionId){
        ValoracionEntity valoracionEntity = valoracionRepository.findById(valoracionId)
                .orElseThrow(() -> new RuntimeException("Valoración no encontrada"));
        valoracionEntity.setEstado(0);
        valoracionRepository.save(valoracionEntity);
    }
}
