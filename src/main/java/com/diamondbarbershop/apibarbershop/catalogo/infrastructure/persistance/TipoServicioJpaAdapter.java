package com.diamondbarbershop.apibarbershop.catalogo.infrastructure.persistance;

import com.diamondbarbershop.apibarbershop.catalogo.domain.model.TipoServicio;
import com.diamondbarbershop.apibarbershop.catalogo.domain.port.out.TipoServicioRepository;
import com.diamondbarbershop.apibarbershop.catalogo.infrastructure.persistance.ITipoServicioJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Adapter — TipoServicioRepository delegando en el repo JPA legacy.
 */
@Component
@RequiredArgsConstructor
public class TipoServicioJpaAdapter implements TipoServicioRepository {

    private final ITipoServicioJpaRepository tipoServicioJpaRepository;

    @Override
    public Optional<TipoServicio> findById(Long id) {
        return tipoServicioJpaRepository.findById(id)
                .map(jpa -> TipoServicio.reconstitute(jpa.getTipoServicio_id(), jpa.getNombre()));
    }

    @Override
    public List<TipoServicio> findAll() {
        return tipoServicioJpaRepository.findAll().stream()
                .map(jpa -> TipoServicio.reconstitute(jpa.getTipoServicio_id(), jpa.getNombre()))
                .toList();
    }
}
