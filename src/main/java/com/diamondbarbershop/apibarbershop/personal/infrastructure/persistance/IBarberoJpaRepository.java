package com.diamondbarbershop.apibarbershop.personal.infrastructure.persistance;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IBarberoJpaRepository extends JpaRepository<BarberoJpaEntity, Long> {

    Optional<BarberoJpaEntity> findByEstado(Integer estado);

    Optional<BarberoJpaEntity> findByNombre(String nombre);
}
