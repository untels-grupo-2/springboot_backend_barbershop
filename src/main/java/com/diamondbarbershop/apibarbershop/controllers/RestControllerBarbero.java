package com.diamondbarbershop.apibarbershop.controllers;


import com.diamondbarbershop.apibarbershop.dtos.barbero.request.DtoBarbero;
import com.diamondbarbershop.apibarbershop.dtos.barbero.response.DtoBarberoResponse;
import com.diamondbarbershop.apibarbershop.dtos.common.ApiResponse;
import com.diamondbarbershop.apibarbershop.exceptions.ImagenNoSubidaException;
import com.diamondbarbershop.apibarbershop.services.BarberoService;
import com.diamondbarbershop.apibarbershop.util.MensajeError;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/barberos")
@RequiredArgsConstructor
public class RestControllerBarbero {
    private final BarberoService barberoService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Object>> crearBarbero(@RequestPart("dtoBarbero") @Valid DtoBarbero dtoBarbero,
                                                            @RequestPart(value="imagen",required = false) MultipartFile imagen) {
        if (imagen != null &&
                (imagen.getContentType() == null
                        || !imagen.getContentType().startsWith("image/"))) {
            throw new ImagenNoSubidaException(MensajeError.TIPO_ARCHIVO_NO_PERMITIDO);
        }
        barberoService.crear(dtoBarbero,imagen);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.succes("Barbero creado correctamente", null)
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<DtoBarberoResponse>>> listarBarbero() {
                List<DtoBarberoResponse> barberos = barberoService.readAll();
        return ResponseEntity.ok(ApiResponse.succes("Lista de barberos obtenida correctamente",barberos));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DtoBarberoResponse>> obtenerBarberoPorId(@PathVariable Long id) {
        DtoBarberoResponse dtoBarberoResponse = barberoService.readOne(id);
        return ResponseEntity.ok(ApiResponse.succes("Barbero encontrado",dtoBarberoResponse));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Object>> actualizarBarbero(@PathVariable Long id,@RequestPart @Valid DtoBarbero dtoBarbero,
                                                                 @RequestPart (value = "imagen", required = false) MultipartFile imagen) {
        if (imagen != null &&
                (imagen.getContentType() == null
                        || !imagen.getContentType().startsWith("image/"))) {
            throw new ImagenNoSubidaException(MensajeError.TIPO_ARCHIVO_NO_PERMITIDO);
        }
        barberoService.update(id,dtoBarbero,imagen);
        DtoBarberoResponse dtoResponse = barberoService.readOne(id);
        return ResponseEntity.ok(ApiResponse.succes("Barbero Actualizado exitosamente",dtoResponse));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> eliminarBarbero(@PathVariable Long id) {
        barberoService.deshabilitar(id);
        return ResponseEntity.ok(ApiResponse.succes("Barbero Deshabilitado exitosamente",null));
    }

}
