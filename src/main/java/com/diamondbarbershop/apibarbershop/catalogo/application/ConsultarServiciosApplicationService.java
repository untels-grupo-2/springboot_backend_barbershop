package com.diamondbarbershop.apibarbershop.catalogo.application;

import com.diamondbarbershop.apibarbershop.catalogo.domain.model.Servicio;
import com.diamondbarbershop.apibarbershop.catalogo.domain.port.in.ConsultarServiciosUseCase;
import com.diamondbarbershop.apibarbershop.catalogo.domain.port.out.ServicioRepository;
import com.diamondbarbershop.apibarbershop.catalogo.domain.exception.ServicioNoEncontradoException;
import com.diamondbarbershop.apibarbershop.util.MensajeError;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Application service de consulta de servicios.
 * Read-only — Hibernate puede optimizar el manejo de sesión con la pista.
 */
@Service
@RequiredArgsConstructor
public class ConsultarServiciosApplicationService implements ConsultarServiciosUseCase {

    private final ServicioRepository servicioRepository;

    @Override
    @Transactional(readOnly = true)
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
