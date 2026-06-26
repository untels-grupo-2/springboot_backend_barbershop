package com.diamondbarbershop.apibarbershop.catalogo.application;

import com.diamondbarbershop.apibarbershop.catalogo.domain.model.TipoServicio;
import com.diamondbarbershop.apibarbershop.catalogo.domain.port.in.ListarTiposServicioUseCase;
import com.diamondbarbershop.apibarbershop.catalogo.domain.port.out.TipoServicioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Application service para listar tipos de servicio disponibles.
 */
@Service
@RequiredArgsConstructor
public class ListarTiposServicioApplicationService implements ListarTiposServicioUseCase {

    private final TipoServicioRepository tipoServicioRepository;

    @Override
    @Transactional(readOnly = true)
    public List<TipoServicio> listar() {
        return tipoServicioRepository.findAll();
    }
}
