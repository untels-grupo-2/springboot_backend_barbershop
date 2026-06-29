package com.diamondbarbershop.apibarbershop.catalogo.application;

import com.diamondbarbershop.apibarbershop.catalogo.domain.model.Servicio;
import com.diamondbarbershop.apibarbershop.catalogo.domain.port.in.CrearServicioUseCase;
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
 * Application service para crear un servicio nuevo en el catálogo.
 *
 * Flujo:
 *   1. Validar que el TipoServicio referenciado existe.
 *   2. Subir la imagen al proveedor externo (Cloudinary u otro).
 *   3. Crear el Servicio (dominio puro, sus invariantes se autoprotegen).
 *   4. Persistir vía puerto de salida.
 */
@Service
@RequiredArgsConstructor
public class CrearServicioApplicationService implements CrearServicioUseCase {

    private static final String CARPETA_IMAGENES = "servicios";

    private final ServicioRepository servicioRepository;
    private final TipoServicioRepository tipoServicioRepository;
    private final SubidorImagen subidorImagen;

    @Override
    @Transactional
    @CacheEvict(cacheNames = "servicios", allEntries = true)
    public Long crear(CrearServicioCommand command) {
        tipoServicioRepository.findById(command.tipoServicioId())
                .orElseThrow(() -> new ServicioNoEncontradoException(MensajeError.TIPO_SERVICIO_NO_ENCONTRADO));

        String urlImagen = subidorImagen.subir(command.imagen(), CARPETA_IMAGENES);

        Servicio servicio = Servicio.crear(
                command.nombre(),
                command.precio(),
                command.descripcion(),
                urlImagen
        );
        servicio.setTipoServicioId(command.tipoServicioId());

        Servicio guardado = servicioRepository.save(servicio);
        return guardado.getId();
    }
}
