package com.diamondbarbershop.apibarbershop.reservas.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PrecioTest {

    @Test
    @DisplayName("Debe crear un Precio con monto valido")
    void should_createPrecio_when_montoIsValid() {
        Precio precio = new Precio(100L);

        assertThat(precio.getMonto()).isEqualTo(100L);
    }

    @Test
    @DisplayName("Debe crear un Precio con monto cero")
    void should_createPrecio_when_montoIsZero() {
        Precio precio = new Precio(0L);

        assertThat(precio.getMonto()).isEqualTo(0L);
    }

    @Test
    @DisplayName("Debe lanzar excepcion al crear Precio con monto negativo")
    void should_throwException_when_montoIsNegative() {
        assertThatThrownBy(() -> new Precio(-1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("negativo");
    }

    @Test
    @DisplayName("Debe lanzar excepcion al crear Precio con monto nulo")
    void should_throwException_when_montoIsNull() {
        assertThatThrownBy(() -> new Precio(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("nulo");
    }

    @Test
    @DisplayName("Dos Precio con el mismo monto deben ser iguales")
    void should_beEqual_when_sameMonto() {
        Precio precio1 = new Precio(50L);
        Precio precio2 = new Precio(50L);

        assertThat(precio1).isEqualTo(precio2);
        assertThat(precio1.hashCode()).isEqualTo(precio2.hashCode());
    }

    @Test
    @DisplayName("Dos Precio con diferente monto no deben ser iguales")
    void should_notBeEqual_when_differentMonto() {
        Precio precio1 = new Precio(50L);
        Precio precio2 = new Precio(100L);

        assertThat(precio1).isNotEqualTo(precio2);
    }
}
