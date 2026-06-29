package com.diamondbarbershop.apibarbershop.catalogo.application;

import com.diamondbarbershop.apibarbershop.catalogo.domain.model.Servicio;
import com.diamondbarbershop.apibarbershop.catalogo.domain.port.in.ActualizarServicioUseCase;
import com.diamondbarbershop.apibarbershop.catalogo.domain.port.out.ServicioRepository;
import com.diamondbarbershop.apibarbershop.catalogo.domain.port.out.TipoServicioRepository;
import com.diamondbarbershop.apibarbershop.shared.domain.port.out.SubidorImagen;
import com.diamondbarbershop.apibarbershop.catalogo.domain.exception.ServicioNoEncontradoException;
import com.diamondbarbershop.apibarbershop.util.MensajeError;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Application service para actualizar un servicio existente.
 *
 * Si no viene una imagen nueva, el método actualizar() del dominio conserva
 * la URL anterior (regla del agregado).
 */
@Service
@RequiredArgsConstructor
public class ActualizarServicioApplicationService implements ActualizarServicioUseCase {

    private static final String CARPETA_IMAGENES = "servicios";

    private final ServicioRepository servicioRepository;
    private final TipoServicioRepository tipoServicioRepository;
    private final SubidorImagen subidorImagen;

    @Override
    @Transactional
    @CacheEvict(cacheNames = "servicios", allEntries = true)
    public void actualizar(ActualizarServicioCommand command) {
        Servicio servicio = servicioRepository.findById(command.servicioId())
                .orElseThrow(() -> new ServicioNoEncontradoException(MensajeError.SERVICIO_NO_ENCONTRADO));

        tipoServicioRepository.findById(command.tipoServicioId())
                .orElseThrow(() -> new ServicioNoEncontradoException(MensajeError.TIPO_SERVICIO_NO_ENCONTRADO));

        String urlImagenNueva = null;
        if (command.imagen() != null) {
            urlImagenNueva = subidorImagen.subir(command.imagen(), CARPETA_IMAGENES);
        }

        servicio.actualizar(
                command.nombre(),
                command.precio(),
                command.descripcion(),
                command.tipoServicioId(),
                urlImagenNueva   // null preserva imagen anterior
        );

        servicioRepository.save(servicio);
    }
}
