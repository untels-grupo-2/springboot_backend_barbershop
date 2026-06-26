package com.diamondbarbershop.apibarbershop.valoraciones.infrastructure.persistance;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IValoracionJpaRepository extends JpaRepository<ValoracionJpaEntity, Long> {
}
