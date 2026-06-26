package com.diamondbarbershop.apibarbershop.shared.infrastructure.cloudinary;

import com.diamondbarbershop.apibarbershop.shared.domain.exception.ImagenNoSubidaException;
import com.diamondbarbershop.apibarbershop.util.MensajeError;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CloudinaryService {
    private final Cloudinary cloudinary;

    public String subirImagen(MultipartFile file, String carpeta){
        try {
            Map<String, Object> params = ObjectUtils.asMap("folder",carpeta);
            Map resultado = cloudinary.uploader().upload(file.getBytes(), params);
            return resultado.get("secure_url").toString();
        } catch (IOException e){
            throw new ImagenNoSubidaException(MensajeError.CLOUDINARY_ERROR);
        }
    }
}
