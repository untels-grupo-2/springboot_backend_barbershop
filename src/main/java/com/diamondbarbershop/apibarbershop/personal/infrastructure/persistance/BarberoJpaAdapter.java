package com.diamondbarbershop.apibarbershop.personal.infrastructure.persistance;

import com.diamondbarbershop.apibarbershop.personal.domain.model.Barbero;
import com.diamondbarbershop.apibarbershop.personal.domain.port.out.BarberoRepository;
import com.diamondbarbershop.apibarbershop.personal.infrastructure.persistance.IBarberoJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Adaptador de salida — implementa BarberoRepository delegando en el
 * Spring Data legacy IBarberoJpaRepository (que opera sobre la JPA entity legacy
 * `com.diamondbarbershop.apibarbershop.personal.infrastructure.persistance.BarberoJpaEntity`).
 *
 * NOTA TRANSITORIA: la JPA entity y el repo Spring Data viven aún en `models/`
 * y `repositories/` mientras ReservaJpaAdapter las referencie. Se moverán
 * físicamente en el bloque final de limpieza.
 */
@Component
@RequiredArgsConstructor
public class BarberoJpaAdapter implements BarberoRepository {

    private final IBarberoJpaRepository barberoJpaRepository;

    @Override
    public Barbero save(Barbero barbero) {
        com.diamondbarbershop.apibarbershop.personal.infrastructure.persistance.BarberoJpaEntity entity = toJpa(barbero);
        var saved = barberoJpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<Barbero> findById(Long id) {
        return barberoJpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Barbero> findActivos() {
        return barberoJpaRepository.findAll().stream()
                .filter(b -> Integer.valueOf(1).equals(b.getEstado()))
                .map(this::toDomain)
                .toList();
    }

    @Override
    public boolean existsById(Long id) {
        return barberoJpaRepository.existsById(id);
    }

    // ── Mappers ──────────────────────────────────────────────────────────────────

    private com.diamondbarbershop.apibarbershop.personal.infrastructure.persistance.BarberoJpaEntity toJpa(Barbero domain) {
        var entity = domain.getId() != null
                ? barberoJpaRepository.findById(domain.getId())
                    .orElse(new com.diamondbarbershop.apibarbershop.personal.infrastructure.persistance.BarberoJpaEntity())
                : new com.diamondbarbershop.apibarbershop.personal.infrastructure.persistance.BarberoJpaEntity();

        entity.setNombre(domain.getNombre());
        entity.setEstado(domain.getEstado());
        entity.setUrlBarbero(domain.getUrlBarbero());
        return entity;
    }

    private Barbero toDomain(com.diamondbarbershop.apibarbershop.personal.infrastructure.persistance.BarberoJpaEntity entity) {
        Barbero b = new Barbero();
        b.setId(entity.getBarbero_id());
        b.setNombre(entity.getNombre());
        b.setEstado(entity.getEstado());
        b.setUrlBarbero(entity.getUrlBarbero());
        return b;
    }
}
