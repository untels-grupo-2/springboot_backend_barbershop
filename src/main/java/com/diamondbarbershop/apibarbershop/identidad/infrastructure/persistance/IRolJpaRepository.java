package com.diamondbarbershop.apibarbershop.identidad.infrastructure.persistance;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IRolJpaRepository extends JpaRepository<RolJpaEntity, Long> {

    Optional<RolJpaEntity> findByName(String name);
}
