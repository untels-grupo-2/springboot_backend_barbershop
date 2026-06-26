package com.diamondbarbershop.apibarbershop.valoraciones.infrastructure.persistance;

import com.diamondbarbershop.apibarbershop.identidad.infrastructure.persistance.UsuarioJpaEntity;
import com.diamondbarbershop.apibarbershop.valoraciones.infrastructure.persistance.ValoracionJpaEntity;
import com.diamondbarbershop.apibarbershop.identidad.infrastructure.persistance.IUsuarioJpaRepository;
import com.diamondbarbershop.apibarbershop.valoraciones.infrastructure.persistance.IValoracionJpaRepository;
import com.diamondbarbershop.apibarbershop.valoraciones.domain.model.Valoracion;
import com.diamondbarbershop.apibarbershop.valoraciones.domain.port.out.ValoracionListadoView;
import com.diamondbarbershop.apibarbershop.valoraciones.domain.port.out.ValoracionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Adaptador de salida — persiste Valoracion delegando en JPA legacy.
 *
 * Para listados, retorna directamente ValoracionListadoView con los datos del
 * cliente resueltos en una sola query (JPA carga la relación @ManyToOne con
 * UsuarioJpaEntity por defecto).
 */
@Component
@RequiredArgsConstructor
public class ValoracionJpaAdapter implements ValoracionRepository {

    private final IValoracionJpaRepository valoracionJpaRepository;
    private final IUsuarioJpaRepository usuariosJpaRepository;

    @Override
    public Valoracion save(Valoracion valoracion) {
        ValoracionJpaEntity entity = toJpa(valoracion);
        ValoracionJpaEntity saved = valoracionJpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<Valoracion> findById(Long id) {
        return valoracionJpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<ValoracionListadoView> buscarParaListado() {
        return valoracionJpaRepository.findAll().stream()
                .map(this::toListadoView)
                .toList();
    }

    // ── Mappers ──────────────────────────────────────────────────────────────────

    private ValoracionJpaEntity toJpa(Valoracion domain) {
        ValoracionJpaEntity entity = domain.getId() != null
                ? valoracionJpaRepository.findById(domain.getId()).orElse(new ValoracionJpaEntity())
                : new ValoracionJpaEntity();

        entity.setValoracion(domain.getPuntuacion());
        entity.setUtil(domain.getUtil());
        entity.setMensaje(domain.getMensaje());
        entity.setEstado(domain.getEstado());

        // Resolver UsuarioJpaEntity JPA por ID — necesario para que JPA mantenga la relación.
        if (domain.getClienteId() != null) {
            UsuarioJpaEntity usuario = usuariosJpaRepository.findById(domain.getClienteId())
                    .orElseThrow(() -> new RuntimeException("UsuarioJpaEntity no encontrado: " + domain.getClienteId()));
            entity.setUsuario(usuario);
        }

        return entity;
    }

    private Valoracion toDomain(ValoracionJpaEntity entity) {
        Valoracion v = new Valoracion();
        v.setId(entity.getValoracion_id());
        v.setPuntuacion(entity.getValoracion());
        v.setUtil(entity.getUtil());
        v.setMensaje(entity.getMensaje());
        v.setClienteId(entity.getUsuario() != null ? entity.getUsuario().getUsuario_id() : null);
        v.setEstado(entity.getEstado());
        return v;
    }

    private ValoracionListadoView toListadoView(ValoracionJpaEntity entity) {
        UsuarioJpaEntity u = entity.getUsuario();
        return new ValoracionListadoView(
                entity.getValoracion_id(),
                u != null ? u.getUsuario_id() : null,
                u != null ? u.getNombre() : null,
                u != null ? u.getCelular() : null,
                entity.getValoracion(),
                entity.getUtil(),
                entity.getMensaje(),
                entity.getEstado()
        );
    }
}
