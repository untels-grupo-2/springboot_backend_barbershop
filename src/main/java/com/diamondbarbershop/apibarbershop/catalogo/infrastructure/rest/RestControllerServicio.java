package com.diamondbarbershop.apibarbershop.catalogo.infrastructure.rest;

import com.diamondbarbershop.apibarbershop.catalogo.domain.model.Servicio;
import com.diamondbarbershop.apibarbershop.catalogo.domain.model.TipoServicio;
import com.diamondbarbershop.apibarbershop.catalogo.domain.port.in.*;
import com.diamondbarbershop.apibarbershop.shared.infrastructure.rest.ApiResponse;
import com.diamondbarbershop.apibarbershop.catalogo.infrastructure.rest.dto.DtoServicio;
import com.diamondbarbershop.apibarbershop.catalogo.infrastructure.rest.dto.DtoTipoServicio;
import com.diamondbarbershop.apibarbershop.catalogo.infrastructure.rest.dto.DtoServicioResponse;
import com.diamondbarbershop.apibarbershop.shared.domain.exception.ImagenNoSubidaException;
import com.diamondbarbershop.apibarbershop.util.MensajeError;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Controller del BC Catálogo migrado a hexagonal.
 *
 * Reemplaza al legacy controllers/RestControllerServicio (que se elimina).
 * Solo orquesta: traduce HTTP a comandos del dominio, no contiene lógica.
 *
 * Los DTOs de request/response viven en `dtos/servicio/`. El controller
 * los mapea a Commands de los Use Cases y mapea los modelos de dominio
 * de vuelta a DTOs de salida.
 */
@RestController
@RequestMapping("/servicios")
@RequiredArgsConstructor
public class RestControllerServicio {

    private final CrearServicioUseCase crearServicioUseCase;
    private final ActualizarServicioUseCase actualizarServicioUseCase;
    private final ConsultarServiciosUseCase consultarServiciosUseCase;
    private final DeshabilitarServicioUseCase deshabilitarServicioUseCase;
    private final ListarTiposServicioUseCase listarTiposServicioUseCase;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Object>> crearServicio(
            @RequestPart("dtoServicio") @Valid DtoServicio dtoServicio,
            @RequestPart("imagen") MultipartFile imagen) {

        validarImagen(imagen);

        crearServicioUseCase.crear(new CrearServicioUseCase.CrearServicioCommand(
                dtoServicio.getNombre(),
                dtoServicio.getPrecio(),
                dtoServicio.getDescripcion(),
                dtoServicio.getTipoServicio_id(),
                imagen
        ));

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.succes("Servicio creado correctamente", null));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<DtoServicioResponse>>> listarServicio() {
        List<DtoServicioResponse> dtos = consultarServiciosUseCase.listarActivos()
                .stream()
                .map(this::toDto)
                .toList();
        return ResponseEntity.ok(ApiResponse.succes("Lista de servicios obtenida correctamente", dtos));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DtoServicioResponse>> obtenerServicioPorId(@PathVariable Long id) {
        DtoServicioResponse dto = toDto(consultarServiciosUseCase.obtener(id));
        return ResponseEntity.ok(ApiResponse.succes("Servicio encontrado", dto));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Object>> actualizarServicio(
            @PathVariable Long id,
            @RequestPart DtoServicio dtoServicio,
            @RequestPart(value = "imagen", required = false) MultipartFile imagen) {

        if (imagen != null) {
            validarImagen(imagen);
        }

        actualizarServicioUseCase.actualizar(new ActualizarServicioUseCase.ActualizarServicioCommand(
                id,
                dtoServicio.getNombre(),
                dtoServicio.getPrecio(),
                dtoServicio.getDescripcion(),
                dtoServicio.getTipoServicio_id(),
                imagen
        ));

        DtoServicioResponse dtoResponse = toDto(consultarServiciosUseCase.obtener(id));
        return ResponseEntity.ok(ApiResponse.succes("Servicio actualizado exitosamente", dtoResponse));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> eliminarServicio(@PathVariable Long id) {
        deshabilitarServicioUseCase.deshabilitar(id);
        return ResponseEntity.ok(ApiResponse.succes("Servicio Eliminado exitosamente", null));
    }

    @GetMapping("/tipos")
    public ResponseEntity<ApiResponse<List<DtoTipoServicio>>> listarTipoServicio() {
        List<DtoTipoServicio> dtos = listarTiposServicioUseCase.listar()
                .stream()
                .map(t -> new DtoTipoServicio(t.getId(), t.getNombre()))
                .toList();
        return ResponseEntity.ok(ApiResponse.succes("Lista de tipo de servicios enviada correctamente", dtos));
    }

    // ── Helpers privados ─────────────────────────────────────────────────────────

    private void validarImagen(MultipartFile imagen) {
        if (imagen.getContentType() == null || !imagen.getContentType().startsWith("image/")) {
            throw new ImagenNoSubidaException(MensajeError.TIPO_ARCHIVO_NO_PERMITIDO);
        }
    }

    /**
     * Mapea el modelo de dominio Servicio al DTO HTTP. Resuelve el nombre del
     * TipoServicio consultando el use case correspondiente — eso evita exponer
     * el ID puro al cliente.
     */
    private DtoServicioResponse toDto(Servicio s) {
        DtoServicioResponse dto = new DtoServicioResponse();
        dto.setServicio_id(s.getId());
        dto.setNombre(s.getNombre());
        dto.setPrecio(s.getPrecio());
        dto.setDescripcion(s.getDescripcion());
        dto.setUrlServicio(s.getUrlServicio());
        // Resolver nombre del tipo (consulta extra; cacheable a futuro si hace falta)
        if (s.getTipoServicioId() != null) {
            listarTiposServicioUseCase.listar().stream()
                    .filter(t -> t.getId().equals(s.getTipoServicioId()))
                    .findFirst()
                    .ifPresent(t -> dto.setNombre_tipoServicio(t.getNombre()));
        }
        return dto;
    }
}
