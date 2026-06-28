package com.diamondbarbershop.apibarbershop.reservas.domain.model;

import com.diamondbarbershop.apibarbershop.reservas.domain.event.ReservaCancelada;
import com.diamondbarbershop.apibarbershop.reservas.domain.event.ReservaConfirmada;
import com.diamondbarbershop.apibarbershop.reservas.domain.event.ReservaCreada;
import com.diamondbarbershop.apibarbershop.reservas.domain.event.ReservaRealizada;
import com.diamondbarbershop.apibarbershop.shared.domain.event.DomainEvent;
import com.diamondbarbershop.apibarbershop.util.EstadoReserva;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ReservaTest {

    private static final Long BARBERO_ID = 1L;
    private static final Long CLIENTE_ID = 2L;
    private static final Long SERVICIO_ID = 3L;
    private static final Long HORARIO_RANGO_ID = 4L;
    private static final Precio PRECIO = new Precio(50L);
    private static final LocalDate FECHA_FUTURA = LocalDate.now().plusDays(1);

    private Reserva crearReserva() {
        return Reserva.crear(BARBERO_ID, CLIENTE_ID, SERVICIO_ID, HORARIO_RANGO_ID,
                PRECIO, FECHA_FUTURA, "sin adicionales", false);
    }

    private Reserva crearReservaConEventosLimpiados() {
        Reserva reserva = crearReserva();
        reserva.pullEvents();
        return reserva;
    }

    @Test
    @DisplayName("Debe crear una reserva en estado CREADA y emitir evento ReservaCreada")
    void should_createWithEstadoCreada_when_crearIsCalled() {
        Reserva reserva = Reserva.crear(BARBERO_ID, CLIENTE_ID, SERVICIO_ID, HORARIO_RANGO_ID,
                PRECIO, FECHA_FUTURA, "corte especial", false);

        assertThat(reserva.getEstado()).isEqualTo(EstadoReserva.CREADA);
        assertThat(reserva.getBarberoId()).isEqualTo(BARBERO_ID);
        assertThat(reserva.getClienteId()).isEqualTo(CLIENTE_ID);

        List<DomainEvent> eventos = reserva.pullEvents();
        assertThat(eventos).hasSize(1);
        assertThat(eventos.get(0)).isInstanceOf(ReservaCreada.class);
    }

    @Test
    @DisplayName("Debe confirmar una reserva desde estado CREADA y emitir ReservaConfirmada")
    void should_confirm_when_estadoIsCreada() {
        Reserva reserva = crearReservaConEventosLimpiados();

        reserva.confirmar();

        assertThat(reserva.getEstado()).isEqualTo(EstadoReserva.CONFIRMADA);
        List<DomainEvent> eventos = reserva.pullEvents();
        assertThat(eventos).hasSize(1);
        assertThat(eventos.get(0)).isInstanceOf(ReservaConfirmada.class);
    }

    @Test
    @DisplayName("Debe marcar como realizada una reserva desde estado CONFIRMADA y emitir ReservaRealizada")
    void should_markAsRealizada_when_estadoIsConfirmada() {
        Reserva reserva = crearReservaConEventosLimpiados();
        reserva.confirmar();
        reserva.pullEvents();

        reserva.marcarComoRealizada();

        assertThat(reserva.getEstado()).isEqualTo(EstadoReserva.REALIZADA);
        List<DomainEvent> eventos = reserva.pullEvents();
        assertThat(eventos).hasSize(1);
        assertThat(eventos.get(0)).isInstanceOf(ReservaRealizada.class);
    }

    @Test
    @DisplayName("Debe cancelar una reserva desde estado CREADA y emitir ReservaCancelada")
    void should_cancel_when_estadoIsCreada() {
        Reserva reserva = crearReservaConEventosLimpiados();

        reserva.cancelar("cliente no puede asistir");

        assertThat(reserva.getEstado()).isEqualTo(EstadoReserva.CANCELADA);
        assertThat(reserva.getMotivoDescripcion()).isEqualTo("cliente no puede asistir");
        List<DomainEvent> eventos = reserva.pullEvents();
        assertThat(eventos).hasSize(1);
        assertThat(eventos.get(0)).isInstanceOf(ReservaCancelada.class);
    }

    @Test
    @DisplayName("Debe cancelar una reserva desde estado CONFIRMADA")
    void should_cancel_when_estadoIsConfirmada() {
        Reserva reserva = crearReservaConEventosLimpiados();
        reserva.confirmar();
        reserva.pullEvents();

        reserva.cancelar("barbero no disponible");

        assertThat(reserva.getEstado()).isEqualTo(EstadoReserva.CANCELADA);
    }

    @Test
    @DisplayName("Debe lanzar excepcion al confirmar una reserva que no esta en estado CREADA")
    void should_throwException_when_confirmingNonCreada() {
        Reserva reserva = crearReservaConEventosLimpiados();
        reserva.confirmar();

        assertThatThrownBy(reserva::confirmar)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("CREADA");
    }

    @Test
    @DisplayName("Debe lanzar excepcion al marcar como realizada una reserva que no esta CONFIRMADA")
    void should_throwException_when_markingRealizadaNonConfirmada() {
        Reserva reserva = crearReservaConEventosLimpiados();

        assertThatThrownBy(reserva::marcarComoRealizada)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("CONFIRMADA");
    }

    @Test
    @DisplayName("Debe lanzar excepcion al cancelar una reserva en estado REALIZADA")
    void should_throwException_when_cancellingRealizada() {
        Reserva reserva = crearReservaConEventosLimpiados();
        reserva.confirmar();
        reserva.marcarComoRealizada();

        assertThatThrownBy(() -> reserva.cancelar("motivo"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("realizada");
    }

    @Test
    @DisplayName("pullEvents debe retornar eventos acumulados y limpiar la lista interna")
    void should_returnAndClearEvents_when_pullEventsCalled() {
        Reserva reserva = crearReserva();

        List<DomainEvent> primeraLlamada = reserva.pullEvents();
        List<DomainEvent> segundaLlamada = reserva.pullEvents();

        assertThat(primeraLlamada).hasSize(1);
        assertThat(segundaLlamada).isEmpty();
    }

    @Test
    @DisplayName("Debe marcar estRecompensa=1 al crear con usaRecompensa=true")
    void should_setEstRecompensa_when_usaRecompensaIsTrue() {
        Reserva reserva = Reserva.crear(BARBERO_ID, CLIENTE_ID, SERVICIO_ID, HORARIO_RANGO_ID,
                new Precio(0L), FECHA_FUTURA, null, true);

        assertThat(reserva.getEstRecompensa()).isEqualTo(1);
    }

    @Test
    @DisplayName("consumirParaRecompensa debe cambiar estRecompensa a 1")
    void should_setEstRecompensaToOne_when_consumirParaRecompensaCalled() {
        Reserva reserva = crearReservaConEventosLimpiados();
        assertThat(reserva.getEstRecompensa()).isEqualTo(0);

        reserva.consumirParaRecompensa();

        assertThat(reserva.getEstRecompensa()).isEqualTo(1);
    }
}
