package com.diamondbarbershop.apibarbershop.agenda.infrastructure.persistance;

import com.diamondbarbershop.apibarbershop.util.DiaSemana;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IHorarioBarberoBaseJpaRepository extends JpaRepository<HorarioBarberoBaseJpaEntity, Long> {

    List<HorarioBarberoBaseJpaEntity> findByDia(DiaSemana dia);

    @Query("""
            SELECT h FROM HorarioBarberoBaseJpaEntity h
            JOIN FETCH h.barbero b
            JOIN FETCH h.tipoHorario t
            WHERE h.estado = 1
            ORDER BY h.dia, t.id, b.nombre
            """)
    List<HorarioBarberoBaseJpaEntity> findAllActivosConRelaciones();
}
