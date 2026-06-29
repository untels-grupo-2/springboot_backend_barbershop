package com.diamondbarbershop.apibarbershop.personal.application;

import com.diamondbarbershop.apibarbershop.personal.domain.exception.BarberoNoEncontradoException;
import com.diamondbarbershop.apibarbershop.personal.domain.model.Barbero;
import com.diamondbarbershop.apibarbershop.personal.domain.port.in.ConsultarBarberosUseCase;
import com.diamondbarbershop.apibarbershop.personal.domain.port.out.BarberoRepository;
import com.diamondbarbershop.apibarbershop.util.MensajeError;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ConsultarBarberosApplicationService implements ConsultarBarberosUseCase {

    private final BarberoRepository barberoRepository;

    @Override
    @Transactional(readOnly = true)
    @Cacheable("barberos")
    public List<Barbero> listarActivos() {
        return barberoRepository.findActivos();
    }

    @Override
    @Transactional(readOnly = true)
    public Barbero obtener(Long id) {
        return barberoRepository.findById(id)
                .orElseThrow(() -> new BarberoNoEncontradoException(MensajeError.BARBERO_NO_ENCONTRADO));
    }
}
