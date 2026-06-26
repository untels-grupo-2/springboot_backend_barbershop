package com.diamondbarbershop.apibarbershop.agenda.infrastructure.persistance;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface IHorarioBarberoInstanciaJpaRepository extends JpaRepository<HorarioBarberoInstanciaJpaEntity, Long> {

    void deleteByFechaBetween(LocalDate desde, LocalDate hasta);

    List<HorarioBarberoInstanciaJpaEntity> findByFechaBetween(LocalDate inicio, LocalDate fin);

    List<HorarioBarberoInstanciaJpaEntity> findByFechaAndTipoHorario_Id(LocalDate fecha, Long id);
}
