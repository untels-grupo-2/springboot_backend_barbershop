package com.diamondbarbershop.apibarbershop.catalogo.infrastructure.persistance;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IServicioJpaRepository extends JpaRepository<ServicioJpaEntity, Long> {
}
