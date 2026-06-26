package com.diamondbarbershop.apibarbershop.shared.infrastructure.cloudinary;

import com.diamondbarbershop.apibarbershop.shared.infrastructure.cloudinary.CloudinaryService;
import com.diamondbarbershop.apibarbershop.shared.domain.port.out.SubidorImagen;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

/**
 * Adapter compartido — implementación del puerto SubidorImagen usando Cloudinary.
 *
 * Reusable por todos los BCs (Catálogo, Personal, etc.) sin acoplarlos a
 * Cloudinary directamente.
 */
@Component
@RequiredArgsConstructor
public class CloudinarySubidorImagenAdapter implements SubidorImagen {

    private final CloudinaryService cloudinaryService;

    @Override
    public String subir(MultipartFile imagen, String carpeta) {
        return cloudinaryService.subirImagen(imagen, carpeta);
    }
}
