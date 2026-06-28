package com.diamondbarbershop.apibarbershop.shared.infrastructure.metrics;

import com.diamondbarbershop.apibarbershop.reservas.domain.event.ReservaCancelada;
import com.diamondbarbershop.apibarbershop.reservas.domain.event.ReservaConfirmada;
import com.diamondbarbershop.apibarbershop.reservas.domain.event.ReservaCreada;
import com.diamondbarbershop.apibarbershop.reservas.domain.event.ReservaRealizada;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class MetricasNegocioListenerTest {

    private SimpleMeterRegistry registry;
    private MetricasNegocioListener listener;

    @BeforeEach
    void setUp() {
        registry = new SimpleMeterRegistry();
        listener = new MetricasNegocioListener(registry);
    }

    @Test
    @DisplayName("Debe incrementar contador de reservas creadas al recibir ReservaCreada")
    void should_incrementCreadas_when_reservaCreadaReceived() {
        ReservaCreada evento = new ReservaCreada(
                1L, 1L, 2L, 3L, LocalDate.now(), 4L, false, LocalDateTime.now()
        );

        listener.onEvent(evento);

        assertThat(registry.counter("barbershop.reservas.creadas").count()).isEqualTo(1.0);
    }

    @Test
    @DisplayName("Debe incrementar contador de reservas confirmadas al recibir ReservaConfirmada")
    void should_incrementConfirmadas_when_reservaConfirmadaReceived() {
        ReservaConfirmada evento = new ReservaConfirmada(
                1L, 2L, LocalDate.now(), LocalDateTime.now()
        );

        listener.onEvent(evento);

        assertThat(registry.counter("barbershop.reservas.confirmadas").count()).isEqualTo(1.0);
    }

    @Test
    @DisplayName("Debe incrementar contador de reservas realizadas al recibir ReservaRealizada")
    void should_incrementRealizadas_when_reservaRealizadaReceived() {
        ReservaRealizada evento = new ReservaRealizada(
                1L, 2L, 50L, false, LocalDateTime.now()
        );

        listener.onEvent(evento);

        assertThat(registry.counter("barbershop.reservas.realizadas").count()).isEqualTo(1.0);
    }

    @Test
    @DisplayName("Debe incrementar contador de reservas canceladas al recibir ReservaCancelada")
    void should_incrementCanceladas_when_reservaCanceladaReceived() {
        ReservaCancelada evento = new ReservaCancelada(
                1L, 2L, LocalDate.now(), "motivo", LocalDateTime.now()
        );

        listener.onEvent(evento);

        assertThat(registry.counter("barbershop.reservas.canceladas").count()).isEqualTo(1.0);
    }
}
