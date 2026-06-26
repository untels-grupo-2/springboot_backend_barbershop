package com.diamondbarbershop.apibarbershop.identidad.infrastructure.persistance;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IUsuarioJpaRepository extends JpaRepository<UsuarioJpaEntity, Long> {

    Optional<UsuarioJpaEntity> findByUsername(String username);

    Optional<UsuarioJpaEntity> findByTokenPassword(String password);

    Optional<UsuarioJpaEntity> findByRefreshToken(String refreshToken);

    Boolean existsByEmail(String email);

    Boolean existsByUsername(String username);

    /**
     * Devuelve los usuarios que tienen un rol con el nombre especificado.
     * Spring Data resuelve la consulta automáticamente usando la relación
     * ManyToMany UsuarioJpaEntity.roles ↔ RolJpaEntity.name.
     */
    List<UsuarioJpaEntity> findByRoles_Name(String roleName);
}
