package com.diamondbarbershop.apibarbershop.catalogo.infrastructure.adapter;

import com.diamondbarbershop.apibarbershop.catalogo.infrastructure.persistance.ServicioJpaEntity;
import com.diamondbarbershop.apibarbershop.catalogo.infrastructure.persistance.IServicioJpaRepository;
import com.diamondbarbershop.apibarbershop.reservas.domain.port.out.CatalogoFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Implementación de CatalogoFacade — vive en el BC Catálogo.
 *
 * El BC Reservas pide servicios vía la interfaz CatalogoFacade (puerto).
 * Esta clase es la única en todo el sistema que conoce los detalles internos
 * del BC Catálogo (entidad JPA ServicioJpaEntity, repositorio IServicioJpaRepository).
 *
 * NOTA TRANSITORIA:
 *   Hoy esta clase consume el repositorio JPA legacy del BC Catálogo
 *   (IServicioJpaRepository). Cuando se complete la migración hexagonal de
 *   Catálogo en un Sprint futuro, esta Facade leerá del nuevo
 *   ServicioRepository (puerto de salida del BC Catálogo) en lugar de la
 *   capa JPA directamente. La interfaz CatalogoFacade no cambia.
 */
@Component
@RequiredArgsConstructor
public class CatalogoFacadeImpl implements CatalogoFacade {

    private final IServicioJpaRepository servicioRepository;

    @Override
    public boolean existeServicio(Long servicioId) {
        return servicioRepository.findById(servicioId).isPresent();
    }

    @Override
    public boolean estaActivoServicio(Long servicioId) {
        Optional<ServicioJpaEntity> servicio = servicioRepository.findById(servicioId);
        return servicio.isPresent()
                && Integer.valueOf(1).equals(servicio.get().getEstado());
    }

    @Override
    public Optional<Long> obtenerPrecio(Long servicioId) {
        return servicioRepository.findById(servicioId).map(ServicioJpaEntity::getPrecio);
    }
}
