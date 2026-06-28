package com.diamondbarbershop.apibarbershop.personal.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BarberoTest {

    @Test
    @DisplayName("Debe crear un barbero con estado activo")
    void should_createBarberoWithEstadoActivo_when_crearIsCalled() {
        Barbero barbero = Barbero.crear("Carlos", "http://img.com/carlos.png");

        assertThat(barbero.getNombre()).isEqualTo("Carlos");
        assertThat(barbero.getUrlBarbero()).isEqualTo("http://img.com/carlos.png");
        assertThat(barbero.getEstado()).isEqualTo(1);
    }

    @Test
    @DisplayName("Debe deshabilitar el barbero estableciendo estado en 0")
    void should_setEstadoToZero_when_deshabilitarIsCalled() {
        Barbero barbero = Barbero.crear("Carlos", "http://img.com/carlos.png");

        barbero.deshabilitar();

        assertThat(barbero.getEstado()).isEqualTo(0);
    }

    @Test
    @DisplayName("Debe retornar true cuando el barbero esta activo")
    void should_returnTrue_when_estadoIsOne() {
        Barbero barbero = Barbero.crear("Carlos", "http://img.com/carlos.png");

        assertThat(barbero.isActivo()).isTrue();
    }

    @Test
    @DisplayName("Debe retornar false cuando el barbero esta deshabilitado")
    void should_returnFalse_when_estadoIsZero() {
        Barbero barbero = Barbero.crear("Carlos", "http://img.com/carlos.png");
        barbero.deshabilitar();

        assertThat(barbero.isActivo()).isFalse();
    }

    @Test
    @DisplayName("Debe actualizar los campos del barbero")
    void should_updateFields_when_actualizarIsCalled() {
        Barbero barbero = Barbero.crear("Carlos", "http://img.com/old.png");

        barbero.actualizar("Pedro", "http://img.com/new.png");

        assertThat(barbero.getNombre()).isEqualTo("Pedro");
        assertThat(barbero.getUrlBarbero()).isEqualTo("http://img.com/new.png");
    }

    @Test
    @DisplayName("Debe preservar la URL existente cuando se actualiza con URL nula")
    void should_preserveExistingUrl_when_actualizarWithNullUrl() {
        Barbero barbero = Barbero.crear("Carlos", "http://img.com/original.png");

        barbero.actualizar("Pedro", null);

        assertThat(barbero.getNombre()).isEqualTo("Pedro");
        assertThat(barbero.getUrlBarbero()).isEqualTo("http://img.com/original.png");
    }
}
