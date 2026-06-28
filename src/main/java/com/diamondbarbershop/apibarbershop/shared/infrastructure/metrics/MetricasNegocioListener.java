package com.diamondbarbershop.apibarbershop.shared.infrastructure.metrics;

import com.diamondbarbershop.apibarbershop.reservas.domain.event.ReservaCancelada;
import com.diamondbarbershop.apibarbershop.reservas.domain.event.ReservaConfirmada;
import com.diamondbarbershop.apibarbershop.reservas.domain.event.ReservaCreada;
import com.diamondbarbershop.apibarbershop.reservas.domain.event.ReservaRealizada;
import com.diamondbarbershop.apibarbershop.shared.domain.event.DomainEvent;
import com.diamondbarbershop.apibarbershop.shared.domain.event.DomainEventListener;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class MetricasNegocioListener implements DomainEventListener {

    private final Counter reservasCreadas;
    private final Counter reservasConfirmadas;
    private final Counter reservasRealizadas;
    private final Counter reservasCanceladas;

    public MetricasNegocioListener(MeterRegistry registry) {
        this.reservasCreadas = Counter.builder("barbershop.reservas.creadas")
                .description("Total de reservas creadas")
                .register(registry);

        this.reservasConfirmadas = Counter.builder("barbershop.reservas.confirmadas")
                .description("Total de reservas confirmadas")
                .register(registry);

        this.reservasRealizadas = Counter.builder("barbershop.reservas.realizadas")
                .description("Total de reservas realizadas (completadas)")
                .register(registry);

        this.reservasCanceladas = Counter.builder("barbershop.reservas.canceladas")
                .description("Total de reservas canceladas")
                .register(registry);
    }

    @Override
    public void onEvent(DomainEvent event) {
        if (event instanceof ReservaCreada) {
            reservasCreadas.increment();
        } else if (event instanceof ReservaConfirmada) {
            reservasConfirmadas.increment();
        } else if (event instanceof ReservaRealizada) {
            reservasRealizadas.increment();
        } else if (event instanceof ReservaCancelada) {
            reservasCanceladas.increment();
        }
    }
}
