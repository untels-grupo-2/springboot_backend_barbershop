package com.diamondbarbershop.apibarbershop.agenda.infrastructure.persistance;

import com.diamondbarbershop.apibarbershop.util.DiaSemana;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IHorarioBarberoBaseJpaRepository extends JpaRepository<HorarioBarberoBaseJpaEntity, Long> {

    List<HorarioBarberoBaseJpaEntity> findByDia(DiaSemana dia);
}
