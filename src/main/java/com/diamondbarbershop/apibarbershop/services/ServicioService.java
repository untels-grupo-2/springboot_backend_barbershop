package com.diamondbarbershop.apibarbershop.services;

import com.diamondbarbershop.apibarbershop.cloudinaryImages.service.CloudinaryService;
import com.diamondbarbershop.apibarbershop.dtos.servicio.request.DtoServicio;
import com.diamondbarbershop.apibarbershop.dtos.servicio.request.DtoTipoServicio;
import com.diamondbarbershop.apibarbershop.dtos.servicio.response.DtoServicioResponse;
import com.diamondbarbershop.apibarbershop.exceptions.ServicioNoEncontradoException;
import com.diamondbarbershop.apibarbershop.exceptions.UsuarioExistenteException;
import com.diamondbarbershop.apibarbershop.mappers.ServicioEntityMapper;
import com.diamondbarbershop.apibarbershop.models.ServicioEntity;
import com.diamondbarbershop.apibarbershop.models.TipoServicio;
import com.diamondbarbershop.apibarbershop.repositories.IServicioRepository;
import com.diamondbarbershop.apibarbershop.repositories.ITipoServicioRepository;
import com.diamondbarbershop.apibarbershop.util.MensajeError;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ServicioService {
    private final IServicioRepository servicioRepo;
    private final ITipoServicioRepository tipoServicioRepository;
    private final CloudinaryService cloudinaryService;
    private final ServicioEntityMapper servicioEntityMapper;


    public void crear(DtoServicio dtoServicio, MultipartFile imagen) {
        String urlImagen = cloudinaryService.subirImagen(imagen,"servicios");

        ServicioEntity servicioEntity = new ServicioEntity();
        servicioEntity.setNombre(dtoServicio.getNombre());
        servicioEntity.setPrecio(dtoServicio.getPrecio());
        servicioEntity.setDescripcion(dtoServicio.getDescripcion());
        servicioEntity.setEstado(1);
        TipoServicio tipoServicio = tipoServicioRepository.findById(dtoServicio.getTipoServicio_id())
                .orElseThrow(() -> new ServicioNoEncontradoException(MensajeError.TIPO_SERVICIO_NO_ENCONTRADO));
        servicioEntity.setTipoServicio(tipoServicio);
        servicioEntity.setUrlServicio(urlImagen);
        servicioRepo.save(servicioEntity);
    }

    public List<DtoServicioResponse> readAll() {
        List<ServicioEntity> servicioEntities = servicioRepo.findAll().stream()
                .filter(servicioEntity -> servicioEntity.getEstado() == 1)
                .toList();
        return servicioEntityMapper.toDtoList(servicioEntities);

    }

    public DtoServicioResponse readOne(Long id) {
        ServicioEntity servicioEntity = servicioRepo.findById(id)
                .orElseThrow(()-> new UsuarioExistenteException(MensajeError.USUARIO_NO_EXISTENTE));
        return  servicioEntityMapper.toDto(servicioEntity);
    }

    public void update(Long id, DtoServicio dtoServicio, MultipartFile imagen) {
        String urlImagen = null;

        ServicioEntity servicioEntity = servicioRepo.findById(id)
                        .orElseThrow(()-> new ServicioNoEncontradoException(MensajeError.SERVICIO_NO_ENCONTRADO));

        if (imagen != null){
            urlImagen = cloudinaryService.subirImagen(imagen,"servicios");
            servicioEntity.setUrlServicio(urlImagen);
        }
        servicioEntity.setNombre(dtoServicio.getNombre());
        servicioEntity.setPrecio(dtoServicio.getPrecio());
        servicioEntity.setDescripcion(dtoServicio.getDescripcion());
        TipoServicio tipoServicio = tipoServicioRepository.findById(dtoServicio.getTipoServicio_id())
                .orElseThrow(() -> new ServicioNoEncontradoException(MensajeError.TIPO_SERVICIO_NO_ENCONTRADO));
        servicioEntity.setTipoServicio(tipoServicio);
        servicioRepo.save(servicioEntity);
    }

    public void deshabilitar(Long id) {
        ServicioEntity servicioEntity = servicioRepo.findById(id)
                .orElseThrow(() -> new ServicioNoEncontradoException("No se puede eliminar. ServicioEntity no encontrado con Id: " + id));
        servicioEntity.setEstado(0);
        servicioRepo.save(servicioEntity);
    }

    public List<DtoTipoServicio> listarTipoServicio() {
        List<TipoServicio> tipoServicio = tipoServicioRepository.findAll().stream().toList();
        return tipoServicio.stream().map(tipo -> new DtoTipoServicio(
                tipo.getTipoServicio_id(),
                tipo.getNombre()
        )).toList();
    }
}
