package com.diamondbarbershop.apibarbershop.valoraciones.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ValoracionTest {

    @Test
    @DisplayName("Debe crear una valoracion con datos validos")
    void should_createValoracion_when_datosValidos() {
        Valoracion valoracion = Valoracion.crear(4, true, "Excelente servicio", 10L);

        assertThat(valoracion.getPuntuacion()).isEqualTo(4);
        assertThat(valoracion.getUtil()).isTrue();
        assertThat(valoracion.getMensaje()).isEqualTo("Excelente servicio");
        assertThat(valoracion.getClienteId()).isEqualTo(10L);
        assertThat(valoracion.getEstado()).isEqualTo(1);
    }

    @Test
    @DisplayName("Debe lanzar excepcion al crear valoracion con puntuacion nula")
    void should_throwException_when_puntuacionIsNull() {
        assertThatThrownBy(() -> Valoracion.crear(null, true, "Buen corte", 1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("puntuación");
    }

    @Test
    @DisplayName("Debe lanzar excepcion al crear valoracion con puntuacion 0")
    void should_throwException_when_puntuacionIsZero() {
        assertThatThrownBy(() -> Valoracion.crear(0, true, "Malo", 1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("puntuación");
    }

    @Test
    @DisplayName("Debe lanzar excepcion al crear valoracion con puntuacion 6")
    void should_throwException_when_puntuacionIsSix() {
        assertThatThrownBy(() -> Valoracion.crear(6, false, "Increible", 1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("puntuación");
    }

    @Test
    @DisplayName("Debe retornar true en isActiva cuando estado es 1")
    void should_returnTrue_when_estadoIsOne() {
        Valoracion valoracion = Valoracion.crear(5, true, "Perfecto", 1L);

        assertThat(valoracion.isActiva()).isTrue();
    }

    @Test
    @DisplayName("Debe retornar false en isActiva despues de desactivar")
    void should_returnFalse_when_desactivada() {
        Valoracion valoracion = Valoracion.crear(3, false, "Regular", 1L);

        valoracion.desactivar();

        assertThat(valoracion.isActiva()).isFalse();
        assertThat(valoracion.getEstado()).isEqualTo(0);
    }
}
