package com.diamondbarbershop.apibarbershop.catalogo.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ServicioTest {

    @Test
    @DisplayName("Debe crear un servicio con estado activo")
    void should_createServicioWithEstadoActivo_when_crearIsCalled() {
        Servicio servicio = Servicio.crear("Corte clasico", 30L, "Corte de cabello clasico", "http://img.com/corte.png");

        assertThat(servicio.getNombre()).isEqualTo("Corte clasico");
        assertThat(servicio.getPrecio()).isEqualTo(30L);
        assertThat(servicio.getDescripcion()).isEqualTo("Corte de cabello clasico");
        assertThat(servicio.getUrlServicio()).isEqualTo("http://img.com/corte.png");
        assertThat(servicio.getEstado()).isEqualTo(1);
    }

    @Test
    @DisplayName("Debe lanzar excepcion al crear servicio con precio negativo")
    void should_throwException_when_crearWithNegativePrecio() {
        assertThatThrownBy(() -> Servicio.crear("Corte", -10L, "desc", "http://img.com/x.png"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Debe deshabilitar el servicio estableciendo estado en 0")
    void should_setEstadoToZero_when_deshabilitarIsCalled() {
        Servicio servicio = Servicio.crear("Corte", 20L, "desc", "http://img.com/x.png");

        servicio.deshabilitar();

        assertThat(servicio.getEstado()).isEqualTo(0);
    }

    @Test
    @DisplayName("Debe retornar true cuando el servicio esta activo")
    void should_returnTrue_when_estadoIsOne() {
        Servicio servicio = Servicio.crear("Corte", 20L, "desc", "http://img.com/x.png");

        assertThat(servicio.isActivo()).isTrue();
    }

    @Test
    @DisplayName("Debe retornar false cuando el servicio esta deshabilitado")
    void should_returnFalse_when_estadoIsZero() {
        Servicio servicio = Servicio.crear("Corte", 20L, "desc", "http://img.com/x.png");
        servicio.deshabilitar();

        assertThat(servicio.isActivo()).isFalse();
    }

    @Test
    @DisplayName("Debe actualizar los campos del servicio")
    void should_updateFields_when_actualizarIsCalled() {
        Servicio servicio = Servicio.crear("Corte", 20L, "desc", "http://img.com/old.png");

        servicio.actualizar("Corte premium", 50L, "desc nueva", 2L, "http://img.com/new.png");

        assertThat(servicio.getNombre()).isEqualTo("Corte premium");
        assertThat(servicio.getPrecio()).isEqualTo(50L);
        assertThat(servicio.getDescripcion()).isEqualTo("desc nueva");
        assertThat(servicio.getTipoServicioId()).isEqualTo(2L);
        assertThat(servicio.getUrlServicio()).isEqualTo("http://img.com/new.png");
    }

    @Test
    @DisplayName("Debe preservar la URL existente cuando se actualiza con URL nula")
    void should_preserveExistingUrl_when_actualizarWithNullUrl() {
        Servicio servicio = Servicio.crear("Corte", 20L, "desc", "http://img.com/original.png");

        servicio.actualizar("Corte premium", 50L, "desc nueva", 2L, null);

        assertThat(servicio.getUrlServicio()).isEqualTo("http://img.com/original.png");
    }
}
