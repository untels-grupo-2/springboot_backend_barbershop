package com.diamondbarbershop.apibarbershop.catalogo.application;

import com.diamondbarbershop.apibarbershop.catalogo.domain.model.Servicio;
import com.diamondbarbershop.apibarbershop.catalogo.domain.port.in.ConsultarServiciosUseCase;
import com.diamondbarbershop.apibarbershop.catalogo.domain.port.out.ServicioRepository;
import com.diamondbarbershop.apibarbershop.catalogo.domain.exception.ServicioNoEncontradoException;
import com.diamondbarbershop.apibarbershop.util.MensajeError;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ConsultarServiciosApplicationService implements ConsultarServiciosUseCase {

    private final ServicioRepository servicioRepository;

    @Override
    @Transactional(readOnly = true)
    @Cacheable("servicios")
    public List<Servicio> listarActivos() {
        return servicioRepository.findActivos();
    }

    @Override
    @Transactional(readOnly = true)
    public Servicio obtener(Long id) {
        return servicioRepository.findById(id)
                .orElseThrow(() -> new ServicioNoEncontradoException(MensajeError.SERVICIO_NO_ENCONTRADO));
    }
}
