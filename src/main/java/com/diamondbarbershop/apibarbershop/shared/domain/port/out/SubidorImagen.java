package com.diamondbarbershop.apibarbershop.shared.domain.port.out;

import org.springframework.web.multipart.MultipartFile;

/**
 * Puerto de salida compartido — subida de imágenes a un servicio externo.
 *
 * Vive en `shared/` porque varios BCs lo necesitan (Catálogo para imágenes
 * de servicios, Personal para fotos de barberos, etc.) — es una capability
 * cross-cutting, no propia de un BC específico.
 *
 * Cada BC consume este contrato sin conocer Cloudinary; el adapter de
 * infraestructura (CloudinarySubidorImagenAdapter) traduce a llamadas reales.
 * Si en el futuro se cambia Cloudinary por AWS S3, solo cambia el adapter.
 */
public interface SubidorImagen {

    /**
     * @param imagen   archivo a subir
     * @param carpeta  carpeta lógica del proveedor (ej. "servicios", "barberos")
     * @return URL pública de la imagen subida
     */
    String subir(MultipartFile imagen, String carpeta);
}
