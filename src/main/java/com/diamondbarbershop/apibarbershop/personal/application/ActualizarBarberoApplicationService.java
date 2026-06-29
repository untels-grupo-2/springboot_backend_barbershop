package com.diamondbarbershop.apibarbershop.personal.application;

import com.diamondbarbershop.apibarbershop.personal.domain.exception.BarberoNoEncontradoException;
import com.diamondbarbershop.apibarbershop.personal.domain.model.Barbero;
import com.diamondbarbershop.apibarbershop.personal.domain.port.in.ActualizarBarberoUseCase;
import com.diamondbarbershop.apibarbershop.personal.domain.port.out.BarberoRepository;
import com.diamondbarbershop.apibarbershop.shared.domain.port.out.SubidorImagen;
import com.diamondbarbershop.apibarbershop.util.MensajeError;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Application service para actualizar datos básicos de un barbero.
 */
@Service
@RequiredArgsConstructor
public class ActualizarBarberoApplicationService implements ActualizarBarberoUseCase {

    private static final String CARPETA_IMAGENES = "barberos";

    private final BarberoRepository barberoRepository;
    private final SubidorImagen subidorImagen;

    @Override
    @Transactional
    @CacheEvict(cacheNames = "barberos", allEntries = true)
    public void actualizar(ActualizarBarberoCommand command) {
        Barbero barbero = barberoRepository.findById(command.barberoId())
                .orElseThrow(() -> new BarberoNoEncontradoException(MensajeError.BARBERO_NO_ENCONTRADO));

        String urlImagenNueva = null;
        if (command.imagen() != null) {
            urlImagenNueva = subidorImagen.subir(command.imagen(), CARPETA_IMAGENES);
        }

        barbero.actualizar(command.nombre(), urlImagenNueva);  // null preserva imagen anterior
        barberoRepository.save(barbero);
    }
}
