package com.diamondbarbershop.apibarbershop.reservas.application.strategy;

import com.diamondbarbershop.apibarbershop.reservas.domain.port.in.CrearReservaUseCase.CrearReservaCommand;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class MontoCalculoStrategySelectorTest {

    @Mock
    private PrecioEstandarStrategy precioEstandarStrategy;

    @Mock
    private PrecioFidelidadStrategy precioFidelidadStrategy;

    @InjectMocks
    private MontoCalculoStrategySelector selector;

    private CrearReservaCommand buildCommand(boolean usarRecompensa) {
        return new CrearReservaCommand(
                1L, 2L, 3L, 4L, 50L,
                LocalDate.now().plusDays(1),
                null,
                usarRecompensa
        );
    }

    @Test
    @DisplayName("Debe seleccionar PrecioFidelidadStrategy cuando usarRecompensa es true")
    void should_selectFidelidad_when_usarRecompensaIsTrue() {
        CrearReservaCommand command = buildCommand(true);

        MontoCalculoStrategy result = selector.seleccionar(command);

        assertThat(result).isSameAs(precioFidelidadStrategy);
    }

    @Test
    @DisplayName("Debe seleccionar PrecioEstandarStrategy cuando usarRecompensa es false")
    void should_selectEstandar_when_usarRecompensaIsFalse() {
        CrearReservaCommand command = buildCommand(false);

        MontoCalculoStrategy result = selector.seleccionar(command);

        assertThat(result).isSameAs(precioEstandarStrategy);
    }

    @Test
    @DisplayName("PrecioEstandarStrategy debe retornar el precio del servicio")
    void should_returnPrecioServicio_when_estrategiaEstandar() {
        PrecioEstandarStrategy strategy = new PrecioEstandarStrategy();
        CrearReservaCommand command = buildCommand(false);

        Long resultado = strategy.calcular(command);

        assertThat(resultado).isEqualTo(50L);
    }

    @Test
    @DisplayName("PrecioFidelidadStrategy debe retornar 0")
    void should_returnZero_when_estrategiaFidelidad() {
        PrecioFidelidadStrategy strategy = new PrecioFidelidadStrategy();
        CrearReservaCommand command = buildCommand(true);

        Long resultado = strategy.calcular(command);

        assertThat(resultado).isEqualTo(0L);
    }
}
