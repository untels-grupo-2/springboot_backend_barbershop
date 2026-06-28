package com.diamondbarbershop.apibarbershop.reservas.application;

import com.diamondbarbershop.apibarbershop.reservas.application.chain.ReservaValidacionException;
import com.diamondbarbershop.apibarbershop.reservas.application.chain.ValidacionReservaHandler;
import com.diamondbarbershop.apibarbershop.reservas.application.event.ReservaDomainEventPublisher;
import com.diamondbarbershop.apibarbershop.reservas.application.strategy.MontoCalculoStrategy;
import com.diamondbarbershop.apibarbershop.reservas.application.strategy.MontoCalculoStrategySelector;
import com.diamondbarbershop.apibarbershop.reservas.domain.model.Reserva;
import com.diamondbarbershop.apibarbershop.reservas.domain.port.in.CrearReservaUseCase.CrearReservaCommand;
import com.diamondbarbershop.apibarbershop.reservas.domain.port.out.ReservaRepository;
import com.diamondbarbershop.apibarbershop.shared.domain.event.DomainEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CrearReservaApplicationServiceTest {

    @Mock
    private ReservaRepository reservaRepository;

    @Mock
    private ValidacionReservaHandler cadenaValidacion;

    @Mock
    private MontoCalculoStrategySelector montoCalculoStrategySelector;

    @Mock
    private ReservaDomainEventPublisher eventPublisher;

    @InjectMocks
    private CrearReservaApplicationService service;

    private CrearReservaCommand buildCommand(boolean usarRecompensa) {
        return new CrearReservaCommand(
                1L, 2L, 3L, 4L, 50L,
                LocalDate.now().plusDays(1),
                "sin adicionales",
                usarRecompensa
        );
    }

    @Test
    @DisplayName("Debe crear reserva exitosamente y retornar su ID")
    void should_createReserva_when_allStepsSucceed() {
        CrearReservaCommand command = buildCommand(false);
        MontoCalculoStrategy strategy = mock(MontoCalculoStrategy.class);
        when(montoCalculoStrategySelector.seleccionar(command)).thenReturn(strategy);
        when(strategy.calcular(command)).thenReturn(50L);
        when(reservaRepository.save(any(Reserva.class))).thenAnswer(invocation -> {
            Reserva r = invocation.getArgument(0);
            r.asignarId(10L);
            return r;
        });

        Long id = service.crear(command);

        assertThat(id).isEqualTo(10L);
        verify(cadenaValidacion).validar(command);
        verify(montoCalculoStrategySelector).seleccionar(command);
        verify(reservaRepository).save(any(Reserva.class));
        verify(eventPublisher).publicar(argThat(list -> !list.isEmpty()));
    }

    @Test
    @DisplayName("Debe lanzar excepcion cuando la validacion falla")
    void should_throwException_when_validationFails() {
        CrearReservaCommand command = buildCommand(false);
        doThrow(new ReservaValidacionException("TestHandler", "fallo de validacion"))
                .when(cadenaValidacion).validar(command);

        assertThatThrownBy(() -> service.crear(command))
                .isInstanceOf(ReservaValidacionException.class);

        verify(reservaRepository, never()).save(any());
        verify(eventPublisher, never()).publicar(any());
    }

    @Test
    @DisplayName("Debe crear reserva con recompensa usando monto 0")
    void should_createWithZeroMonto_when_usarRecompensaIsTrue() {
        CrearReservaCommand command = buildCommand(true);
        MontoCalculoStrategy strategy = mock(MontoCalculoStrategy.class);
        when(montoCalculoStrategySelector.seleccionar(command)).thenReturn(strategy);
        when(strategy.calcular(command)).thenReturn(0L);
        when(reservaRepository.save(any(Reserva.class))).thenAnswer(invocation -> {
            Reserva r = invocation.getArgument(0);
            r.asignarId(20L);
            return r;
        });

        Long id = service.crear(command);

        assertThat(id).isEqualTo(20L);
        verify(reservaRepository).save(argThat(reserva ->
                reserva.getPrecio().getMonto().equals(0L)));
    }
}
