package com.diamondbarbershop.apibarbershop.agenda.domain.model;

import com.diamondbarbershop.apibarbershop.util.DiaSemana;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class HorarioBarberoBaseTest {

    private HorarioBarberoBase crearRegistro(Integer estId) {
        HorarioBarberoBase registro = new HorarioBarberoBase();
        registro.setId(1L);
        registro.setBarberoId(10L);
        registro.setTipoHorarioId(100L);
        registro.setDia(DiaSemana.LUNES);
        registro.setEstId(estId);
        registro.setEstado(1);
        return registro;
    }

    @Test
    @DisplayName("Debe retornar true en estaActivoEnEsteTurno cuando estId es 1")
    void should_returnTrue_when_estIdIsOne() {
        HorarioBarberoBase registro = crearRegistro(1);

        assertThat(registro.estaActivoEnEsteTurno()).isTrue();
    }

    @Test
    @DisplayName("Debe retornar false en estaActivoEnEsteTurno cuando estId es null")
    void should_returnFalse_when_estIdIsNull() {
        HorarioBarberoBase registro = crearRegistro(null);

        assertThat(registro.estaActivoEnEsteTurno()).isFalse();
    }

    @Test
    @DisplayName("Debe retornar false en estaActivoEnEsteTurno cuando estId es diferente de 1")
    void should_returnFalse_when_estIdIsNotOne() {
        HorarioBarberoBase registro = crearRegistro(0);

        assertThat(registro.estaActivoEnEsteTurno()).isFalse();
    }

    @Test
    @DisplayName("Debe asignar turno estableciendo estId en 1")
    void should_setEstIdToOne_when_asignarTurno() {
        HorarioBarberoBase registro = crearRegistro(null);

        registro.asignarTurno();

        assertThat(registro.getEstId()).isEqualTo(1);
        assertThat(registro.estaActivoEnEsteTurno()).isTrue();
    }

    @Test
    @DisplayName("Debe quitar turno estableciendo estId en null")
    void should_setEstIdToNull_when_quitarTurno() {
        HorarioBarberoBase registro = crearRegistro(1);

        registro.quitarTurno();

        assertThat(registro.getEstId()).isNull();
        assertThat(registro.estaActivoEnEsteTurno()).isFalse();
    }

    @Test
    @DisplayName("Debe asignar turno cuando ya estaba asignado sin efectos secundarios")
    void should_remainActive_when_asignarTurnoAlreadyActive() {
        HorarioBarberoBase registro = crearRegistro(1);

        registro.asignarTurno();

        assertThat(registro.getEstId()).isEqualTo(1);
    }

    @Test
    @DisplayName("Debe quitar turno cuando ya estaba sin turno sin efectos secundarios")
    void should_remainInactive_when_quitarTurnoAlreadyInactive() {
        HorarioBarberoBase registro = crearRegistro(null);

        registro.quitarTurno();

        assertThat(registro.getEstId()).isNull();
    }

    @Test
    @DisplayName("Debe mantener los campos correctos al crear un registro")
    void should_preserveFields_when_created() {
        HorarioBarberoBase registro = crearRegistro(1);

        assertThat(registro.getId()).isEqualTo(1L);
        assertThat(registro.getBarberoId()).isEqualTo(10L);
        assertThat(registro.getTipoHorarioId()).isEqualTo(100L);
        assertThat(registro.getDia()).isEqualTo(DiaSemana.LUNES);
        assertThat(registro.getEstado()).isEqualTo(1);
    }
}
