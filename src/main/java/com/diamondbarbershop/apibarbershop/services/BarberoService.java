package com.diamondbarbershop.apibarbershop.services;

import com.diamondbarbershop.apibarbershop.agenda.application.factory.HorarioBaseTemplateCreator;
import com.diamondbarbershop.apibarbershop.agenda.application.factory.HorarioBaseTemplateCreatorSelector;
import com.diamondbarbershop.apibarbershop.cloudinaryImages.service.CloudinaryService;
import com.diamondbarbershop.apibarbershop.dtos.barbero.request.DtoBarbero;
import com.diamondbarbershop.apibarbershop.dtos.barbero.response.DtoBarberoResponse;
import com.diamondbarbershop.apibarbershop.exceptions.BarberoNoEncontradoException;
import com.diamondbarbershop.apibarbershop.mappers.BarberoMapper;
import com.diamondbarbershop.apibarbershop.models.Barbero;
import com.diamondbarbershop.apibarbershop.repositories.IBarberoRepository;
import com.diamondbarbershop.apibarbershop.util.MensajeError;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BarberoService {

    private final IBarberoRepository barberoRepository;
    private final AuthenticationManager authenticationManager;
    private final HorarioBaseTemplateCreatorSelector horarioBaseTemplateCreatorSelector;
    private final CloudinaryService cloudinaryService;

    @Transactional
    public void crear(DtoBarbero dtoBarbero, MultipartFile imagen) {
        String urlImagen = null;

        if (imagen != null) {
            urlImagen = cloudinaryService.subirImagen(imagen, "barberos");
        }
        Barbero barbero = new Barbero();
        barbero.setNombre(dtoBarbero.getNombre());
        barbero.setEstado(1);
        if (urlImagen != null) {
            barbero.setUrlBarbero(urlImagen);
        }
        barberoRepository.save(barbero);

        // Factory Method (PB-12): el Selector escoge el Creator según el tipo
        // de plantilla solicitado en el DTO. El Creator genera y persiste
        // la plantilla inicial de HorarioBarberoBase.
        HorarioBaseTemplateCreator creator =
                horarioBaseTemplateCreatorSelector.seleccionar(dtoBarbero.getTipoPlantilla());
        creator.crear(barbero.getBarbero_id());
    }

    public List<DtoBarberoResponse> readAll(){
        List<Barbero> barberos = barberoRepository.findAll();
        return barberos.stream().filter(barbero ->
                        barbero.getEstado() == 1)
                .map(barbero -> {
                    DtoBarberoResponse dto = BarberoMapper.toDto(barbero);
                    return dto;
                }).toList();
    }

    public void deshabilitar(Long id) {
        Barbero barbero = barberoRepository.findById(id)
                .orElseThrow(() -> new BarberoNoEncontradoException("Barbero no encontrado con el id: " + id));
        barbero.setEstado(0);
        barberoRepository.save(barbero);
    }

    public void update(Long id,DtoBarbero dtoBarbero,MultipartFile imagen){
        Barbero barbero = barberoRepository.findById(id)
                .orElseThrow(() -> new BarberoNoEncontradoException("Barbero no encontrada con id: " + id));

        String urlImagen = null;

        if (imagen != null) {
            urlImagen = cloudinaryService.subirImagen(imagen, "barberos");
            barbero.setUrlBarbero(urlImagen);
        }
        barbero.setNombre(dtoBarbero.getNombre());
        barberoRepository.save(barbero);
    }

    public DtoBarberoResponse readOne(Long id){
        Barbero barbero = barberoRepository.findById(id)
                .orElseThrow(() -> new BarberoNoEncontradoException(MensajeError.BARBERO_NO_ENCONTRADO));
        DtoBarberoResponse dto = BarberoMapper.toDto(barbero);
        return dto;
    }
}
