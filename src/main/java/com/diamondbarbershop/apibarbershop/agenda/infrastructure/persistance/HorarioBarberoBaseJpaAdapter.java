package com.diamondbarbershop.apibarbershop.agenda.infrastructure.persistance;

import com.diamondbarbershop.apibarbershop.agenda.domain.model.HorarioBarberoBase;
import com.diamondbarbershop.apibarbershop.agenda.domain.port.out.HorarioBarberoBaseRepository;
import com.diamondbarbershop.apibarbershop.personal.infrastructure.persistance.BarberoJpaEntity;
import com.diamondbarbershop.apibarbershop.agenda.infrastructure.persistance.TipoHorarioJpaEntity;
import com.diamondbarbershop.apibarbershop.personal.infrastructure.persistance.IBarberoJpaRepository;
import com.diamondbarbershop.apibarbershop.agenda.infrastructure.persistance.IHorarioBarberoBaseJpaRepository;
import com.diamondbarbershop.apibarbershop.agenda.infrastructure.persistance.ITipoHorarioJpaRepository;
import com.diamondbarbershop.apibarbershop.util.DiaSemana;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Adapter — HorarioBarberoBaseRepository delegando en el JPA legacy.
 *
 * NOTA TRANSITORIA: la JPA entity vive aún en `models/HorarioBarberoBase.java`.
 * El nombre del paquete domain (HorarioBarberoBase también) coincide pero
 * son clases distintas — una con anotaciones JPA, la otra puro dominio.
 */
@Component
@RequiredArgsConstructor
public class HorarioBarberoBaseJpaAdapter implements HorarioBarberoBaseRepository {

    private final IHorarioBarberoBaseJpaRepository horarioBaseJpaRepository;
    private final IBarberoJpaRepository barberoJpaRepository;
    private final ITipoHorarioJpaRepository tipoHorarioJpaRepository;

    @Override
    public List<HorarioBarberoBase> findByDia(DiaSemana dia) {
        return horarioBaseJpaRepository.findByDia(dia).stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public List<HorarioBarberoBase> findActivos() {
        return horarioBaseJpaRepository.findAll().stream()
                .filter(r -> r.getEst_id() != null && r.getEst_id() == 1)
                .map(this::toDomain)
                .toList();
    }

    @Override
    public void saveAll(List<HorarioBarberoBase> registros) {
        List<com.diamondbarbershop.apibarbershop.agenda.infrastructure.persistance.HorarioBarberoBaseJpaEntity> jpaEntities =
                registros.stream().map(this::toJpa).toList();
        horarioBaseJpaRepository.saveAll(jpaEntities);
    }

    // ── Mappers ──────────────────────────────────────────────────────────────────

    private com.diamondbarbershop.apibarbershop.agenda.infrastructure.persistance.HorarioBarberoBaseJpaEntity toJpa(HorarioBarberoBase domain) {
        var entity = domain.getId() != null
                ? horarioBaseJpaRepository.findById(domain.getId())
                    .orElse(new com.diamondbarbershop.apibarbershop.agenda.infrastructure.persistance.HorarioBarberoBaseJpaEntity())
                : new com.diamondbarbershop.apibarbershop.agenda.infrastructure.persistance.HorarioBarberoBaseJpaEntity();

        BarberoJpaEntity barbero = barberoJpaRepository.findById(domain.getBarberoId())
                .orElseThrow(() -> new RuntimeException("BarberoJpaEntity no encontrado: " + domain.getBarberoId()));
        TipoHorarioJpaEntity tipo = tipoHorarioJpaRepository.findById(domain.getTipoHorarioId())
                .orElseThrow(() -> new RuntimeException("TipoHorarioJpaEntity no encontrado: " + domain.getTipoHorarioId()));

        entity.setBarbero(barbero);
        entity.setTipoHorario(tipo);
        entity.setDia(domain.getDia());
        entity.setEst_id(domain.getEstId());
        entity.setEstado(domain.getEstado());
        return entity;
    }

    private HorarioBarberoBase toDomain(com.diamondbarbershop.apibarbershop.agenda.infrastructure.persistance.HorarioBarberoBaseJpaEntity entity) {
        HorarioBarberoBase b = new HorarioBarberoBase();
        b.setId(entity.getHorarioBarberoBase_id());
        b.setBarberoId(entity.getBarbero().getBarbero_id());
        b.setTipoHorarioId(entity.getTipoHorario().getId());
        b.setDia(entity.getDia());
        b.setEstId(entity.getEst_id());
        b.setEstado(entity.getEstado());
        return b;
    }
}
