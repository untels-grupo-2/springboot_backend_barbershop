package com.diamondbarbershop.apibarbershop.reservas.application.listeners;

import com.diamondbarbershop.apibarbershop.reservas.domain.event.ReservaConfirmada;
import com.diamondbarbershop.apibarbershop.reservas.domain.event.ReservaCreada;
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
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecompensaListenerTest {

    @Mock
    private ReservaRepository reservaRepository;

    @InjectMocks
    private RecompensaListener listener;

    private Reserva crearReservaPendiente(Long id) {
        Reserva reserva = Reserva.reconstitute(
                id, 1L, 2L, 3L, 4L,
                EstadoReserva.REALIZADA, new Precio(50L),
                null, null, LocalDateTime.now(),
                LocalDate.now().plusDays(1), 0, null
        );
        return reserva;
    }

    @Test
    @DisplayName("Debe consumir reservas pendientes cuando ReservaCreada con usaRecompensa=true")
    void should_consumeReservas_when_reservaCreadaWithRecompensa() {
        ReservaCreada evento = new ReservaCreada(
                10L, 1L, 2L, 3L, LocalDate.now().plusDays(1), 4L, true, LocalDateTime.now()
        );

        Reserva pendiente1 = crearReservaPendiente(1L);
        Reserva pendiente2 = crearReservaPendiente(2L);
        when(reservaRepository.findByClienteId(2L)).thenReturn(List.of(pendiente1, pendiente2));

        listener.onEvent(evento);

        verify(reservaRepository).save(pendiente1);
        verify(reservaRepository).save(pendiente2);
    }

    @Test
    @DisplayName("No debe hacer nada cuando ReservaCreada con usaRecompensa=false")
    void should_doNothing_when_reservaCreadaWithoutRecompensa() {
        ReservaCreada evento = new ReservaCreada(
                10L, 1L, 2L, 3L, LocalDate.now().plusDays(1), 4L, false, LocalDateTime.now()
        );

        listener.onEvent(evento);

        verifyNoInteractions(reservaRepository);
    }

    @Test
    @DisplayName("No debe hacer nada cuando el evento no es ReservaCreada")
    void should_doNothing_when_eventIsNotReservaCreada() {
        ReservaConfirmada evento = new ReservaConfirmada(
                10L, 2L, LocalDate.now().plusDays(1), LocalDateTime.now()
        );

        listener.onEvent(evento);

        verifyNoInteractions(reservaRepository);
    }
}
