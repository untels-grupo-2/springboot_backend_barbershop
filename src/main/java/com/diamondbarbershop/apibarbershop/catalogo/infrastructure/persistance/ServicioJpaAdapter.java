package com.diamondbarbershop.apibarbershop.catalogo.infrastructure.persistance;

import com.diamondbarbershop.apibarbershop.catalogo.domain.model.Servicio;
import com.diamondbarbershop.apibarbershop.catalogo.domain.port.out.ServicioRepository;
import com.diamondbarbershop.apibarbershop.catalogo.infrastructure.persistance.ServicioJpaEntity;
import com.diamondbarbershop.apibarbershop.catalogo.infrastructure.persistance.TipoServicioJpaEntity;
import com.diamondbarbershop.apibarbershop.catalogo.infrastructure.persistance.IServicioJpaRepository;
import com.diamondbarbershop.apibarbershop.catalogo.infrastructure.persistance.ITipoServicioJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Adaptador de salida — implementa el puerto ServicioRepository delegando
 * en la entidad JPA legacy ServicioJpaEntity.
 *
 * NOTA TRANSITORIA: la entidad JPA y el Spring Data repo viven aún en las
 * carpetas `models/` y `repositories/` mientras el ReservaJpaAdapter las
 * referencie. Cuando esa dependencia se elimine en el bloque de limpieza,
 * se moverán físicamente a esta misma carpeta `catalogo/infrastructure/persistance/`.
 *
 * Lo importante: el dominio (BC Catálogo) ya no las conoce — solo este adapter.
 */
@Component
@RequiredArgsConstructor
public class ServicioJpaAdapter implements ServicioRepository {

    private final IServicioJpaRepository servicioJpaRepository;
    private final ITipoServicioJpaRepository tipoServicioJpaRepository;

    @Override
    public Servicio save(Servicio servicio) {
        ServicioJpaEntity entity = toJpa(servicio);
        ServicioJpaEntity saved = servicioJpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<Servicio> findById(Long id) {
        return servicioJpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Servicio> findActivos() {
        return servicioJpaRepository.findAll()
                .stream()
                .filter(s -> Integer.valueOf(1).equals(s.getEstado()))
                .map(this::toDomain)
                .toList();
    }

    @Override
    public boolean existsById(Long id) {
        return servicioJpaRepository.existsById(id);
    }

    // ── Mappers ──────────────────────────────────────────────────────────────────

    private ServicioJpaEntity toJpa(Servicio domain) {
        ServicioJpaEntity entity = domain.getId() != null
                ? servicioJpaRepository.findById(domain.getId()).orElse(new ServicioJpaEntity())
                : new ServicioJpaEntity();

        entity.setNombre(domain.getNombre());
        entity.setPrecio(domain.getPrecio());
        entity.setDescripcion(domain.getDescripcion());
        entity.setUrlServicio(domain.getUrlServicio());
        entity.setEstado(domain.getEstado());

        // tipoServicio se asigna por referencia JPA — cargamos el proxy con findById
        if (domain.getTipoServicioId() != null) {
            TipoServicioJpaEntity tipoJpa = tipoServicioJpaRepository.findById(domain.getTipoServicioId())
                    .orElseThrow(() -> new RuntimeException(
                            "TipoServicioJpaEntity no encontrado: " + domain.getTipoServicioId()));
            entity.setTipoServicio(tipoJpa);
        }

        return entity;
    }

    private Servicio toDomain(ServicioJpaEntity entity) {
        Servicio s = new Servicio();
        s.setId(entity.getServicio_id());
        s.setNombre(entity.getNombre());
        s.setPrecio(entity.getPrecio());
        s.setDescripcion(entity.getDescripcion());
        s.setTipoServicioId(entity.getTipoServicio() != null
                ? entity.getTipoServicio().getTipoServicio_id()
                : null);
        s.setUrlServicio(entity.getUrlServicio());
        s.setEstado(entity.getEstado());
        return s;
    }
}
