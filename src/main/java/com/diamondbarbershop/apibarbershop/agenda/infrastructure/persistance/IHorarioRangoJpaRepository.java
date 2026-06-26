package com.diamondbarbershop.apibarbershop.agenda.infrastructure.persistance;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IHorarioRangoJpaRepository extends JpaRepository<HorarioRangoJpaEntity, Long> {
}
