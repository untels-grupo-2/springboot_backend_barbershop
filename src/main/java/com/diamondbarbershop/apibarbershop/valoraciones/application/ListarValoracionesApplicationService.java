package com.diamondbarbershop.apibarbershop.valoraciones.application;

import com.diamondbarbershop.apibarbershop.valoraciones.domain.port.in.ListarValoracionesUseCase;
import com.diamondbarbershop.apibarbershop.valoraciones.domain.port.out.ValoracionListadoView;
import com.diamondbarbershop.apibarbershop.valoraciones.domain.port.out.ValoracionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Application service para listar valoraciones (con datos del cliente).
 */
@Service
@RequiredArgsConstructor
public class ListarValoracionesApplicationService implements ListarValoracionesUseCase {

    private final ValoracionRepository valoracionRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ValoracionListadoView> listar() {
        return valoracionRepository.buscarParaListado();
    }
}
