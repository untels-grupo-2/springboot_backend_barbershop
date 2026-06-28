package com.diamondbarbershop.apibarbershop.reservas.application;

import com.diamondbarbershop.apibarbershop.reservas.application.event.ReservaDomainEventPublisher;
import com.diamondbarbershop.apibarbershop.reservas.domain.model.Precio;
import com.diamondbarbershop.apibarbershop.reservas.domain.model.Reserva;
import com.diamondbarbershop.apibarbershop.reservas.domain.port.out.ReservaRepository;
import com.diamondbarbershop.apibarbershop.util.EstadoReserva;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GestionarReservaApplicationServiceTest {

    @Mock
    private ReservaRepository reservaRepository;

    @Mock
    private ReservaDomainEventPublisher eventPublisher;

    @InjectMocks
    private GestionarReservaApplicationService service;

    private Reserva crearReservaLimpia() {
        Reserva reserva = Reserva.crear(1L, 2L, 3L, 4L,
                new Precio(50L), LocalDate.now().plusDays(1), null, false);
        reserva.asignarId(100L);
        reserva.pullEvents();
        return reserva;
    }

    @Test
    @DisplayName("Debe confirmar una reserva exitosamente")
    void should_confirmReserva_when_estadoIsCreada() {
        Reserva reserva = crearReservaLimpia();
        when(reservaRepository.findById(100L)).thenReturn(Optional.of(reserva));
        when(reservaRepository.save(any(Reserva.class))).thenAnswer(i -> i.getArgument(0));

        service.confirmar(100L);

        assertThat(reserva.getEstado()).isEqualTo(EstadoReserva.CONFIRMADA);
        verify(reservaRepository).save(reserva);
        verify(eventPublisher).publicar(argThat(list -> !list.isEmpty()));
    }

    @Test
    @DisplayName("Debe lanzar excepcion al confirmar reserva inexistente")
    void should_throwException_when_reservaNotFound() {
        when(reservaRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.confirmar(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("999");
    }

    @Test
    @DisplayName("Debe marcar como realizada una reserva confirmada exitosamente")
    void should_markAsRealizada_when_estadoIsConfirmada() {
        Reserva reserva = crearReservaLimpia();
        reserva.confirmar();
        reserva.pullEvents();
        when(reservaRepository.findById(100L)).thenReturn(Optional.of(reserva));
        when(reservaRepository.save(any(Reserva.class))).thenAnswer(i -> i.getArgument(0));

        service.marcarComoRealizada(100L);

        assertThat(reserva.getEstado()).isEqualTo(EstadoReserva.REALIZADA);
        verify(reservaRepository).save(reserva);
        verify(eventPublisher).publicar(argThat(list -> !list.isEmpty()));
    }

    @Test
    @DisplayName("Debe cancelar una reserva exitosamente con motivo")
    void should_cancelReserva_when_estadoAllowsCancellation() {
        Reserva reserva = crearReservaLimpia();
        when(reservaRepository.findById(100L)).thenReturn(Optional.of(reserva));
        when(reservaRepository.save(any(Reserva.class))).thenAnswer(i -> i.getArgument(0));

        service.cancelar(100L, "cliente solicito cancelacion");

        assertThat(reserva.getEstado()).isEqualTo(EstadoReserva.CANCELADA);
        assertThat(reserva.getMotivoDescripcion()).isEqualTo("cliente solicito cancelacion");
        verify(reservaRepository).save(reserva);
        verify(eventPublisher).publicar(argThat(list -> !list.isEmpty()));
    }
}
