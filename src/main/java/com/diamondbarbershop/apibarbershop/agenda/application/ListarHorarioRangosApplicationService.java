package com.diamondbarbershop.apibarbershop.agenda.application;

import com.diamondbarbershop.apibarbershop.agenda.domain.model.HorarioRango;
import com.diamondbarbershop.apibarbershop.agenda.domain.port.in.ListarHorarioRangosUseCase;
import com.diamondbarbershop.apibarbershop.agenda.domain.port.out.HorarioRangoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ListarHorarioRangosApplicationService implements ListarHorarioRangosUseCase {

    private final HorarioRangoRepository horarioRangoRepository;

    @Override
    @Transactional(readOnly = true)
    @Cacheable("rangos-horario")
    public List<HorarioRango> listar() {
        return horarioRangoRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public HorarioRango obtener(Long id) {
        return horarioRangoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rango horario no encontrado: " + id));
    }
}
