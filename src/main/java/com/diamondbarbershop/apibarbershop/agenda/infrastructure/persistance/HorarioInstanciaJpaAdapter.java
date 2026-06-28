package com.diamondbarbershop.apibarbershop.agenda.infrastructure.persistance;

import com.diamondbarbershop.apibarbershop.agenda.domain.model.HorarioBarberoInstancia;
import com.diamondbarbershop.apibarbershop.agenda.domain.port.out.HorarioInstanciaRepository;
import com.diamondbarbershop.apibarbershop.personal.infrastructure.persistance.BarberoJpaEntity;
import com.diamondbarbershop.apibarbershop.agenda.infrastructure.persistance.TipoHorarioJpaEntity;
import com.diamondbarbershop.apibarbershop.personal.infrastructure.persistance.IBarberoJpaRepository;
import com.diamondbarbershop.apibarbershop.agenda.infrastructure.persistance.IHorarioBarberoInstanciaJpaRepository;
import com.diamondbarbershop.apibarbershop.agenda.infrastructure.persistance.ITipoHorarioJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

/**
 * Adapter — HorarioInstanciaRepository delegando en el JPA legacy.
 */
@Component
@RequiredArgsConstructor
public class HorarioInstanciaJpaAdapter implements HorarioInstanciaRepository {

    private final IHorarioBarberoInstanciaJpaRepository instanciaJpaRepository;
    private final IBarberoJpaRepository barberoJpaRepository;
    private final ITipoHorarioJpaRepository tipoHorarioJpaRepository;

    @Override
    public List<HorarioBarberoInstancia> findByBarberoIdAndFecha(Long barberoId, LocalDate fecha) {
        BarberoJpaEntity barbero = barberoJpaRepository.findById(barberoId)
                .orElseThrow(() -> new RuntimeException("BarberoJpaEntity no encontrado: " + barberoId));
        // No existe método específico en el repo legacy — usamos findByFechaAndTipoHorario_Id como variante.
        // Para la query "barbero + fecha" filtramos en memoria desde el listado del día.
        return instanciaJpaRepository.findByFechaBetween(fecha, fecha).stream()
                .filter(i -> i.getBarbero().getBarbero_id().equals(barberoId))
                .map(this::toDomain)
                .toList();
    }

    @Override
    public void deleteByFechaBetween(LocalDate desde, LocalDate hasta) {
        instanciaJpaRepository.deleteByFechaBetween(desde, hasta);
    }

    @Override
    public HorarioBarberoInstancia save(HorarioBarberoInstancia instancia) {
        com.diamondbarbershop.apibarbershop.agenda.infrastructure.persistance.HorarioBarberoInstanciaJpaEntity entity = toJpa(instancia);
        var saved = instanciaJpaRepository.save(entity);
        return toDomain(saved);
    }

    // ── Mappers ──────────────────────────────────────────────────────────────────

    private com.diamondbarbershop.apibarbershop.agenda.infrastructure.persistance.HorarioBarberoInstanciaJpaEntity toJpa(HorarioBarberoInstancia domain) {
        var entity = domain.getId() != null
                ? instanciaJpaRepository.findById(domain.getId())
                    .orElse(new com.diamondbarbershop.apibarbershop.agenda.infrastructure.persistance.HorarioBarberoInstanciaJpaEntity())
                : new com.diamondbarbershop.apibarbershop.agenda.infrastructure.persistance.HorarioBarberoInstanciaJpaEntity();

        BarberoJpaEntity barbero = barberoJpaRepository.findById(domain.getBarberoId())
                .orElseThrow(() -> new RuntimeException("BarberoJpaEntity no encontrado: " + domain.getBarberoId()));
        TipoHorarioJpaEntity tipo = tipoHorarioJpaRepository.findById(domain.getTipoHorarioId())
                .orElseThrow(() -> new RuntimeException("TipoHorarioJpaEntity no encontrado: " + domain.getTipoHorarioId()));

        entity.setBarbero(barbero);
        entity.setTipoHorario(tipo);
        entity.setDia(domain.getDia());
        entity.setFecha(domain.getFecha());
        entity.setEst_id(domain.getEstId());
        return entity;
    }

    private HorarioBarberoInstancia toDomain(com.diamondbarbershop.apibarbershop.agenda.infrastructure.persistance.HorarioBarberoInstanciaJpaEntity entity) {
        HorarioBarberoInstancia i = new HorarioBarberoInstancia();
        i.setId(entity.getHorarioBarberoInstancia_id());
        i.setBarberoId(entity.getBarbero().getBarbero_id());
        i.setTipoHorarioId(entity.getTipoHorario().getId());
        i.setDia(entity.getDia());
        i.setFecha(entity.getFecha());
        i.setEstId(entity.getEst_id());
        return i;
    }
}
