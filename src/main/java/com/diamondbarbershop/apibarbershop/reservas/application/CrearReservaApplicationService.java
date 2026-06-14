package com.diamondbarbershop.apibarbershop.reservas.application;

import com.diamondbarbershop.apibarbershop.reservas.application.chain.ValidacionReservaHandler;
import com.diamondbarbershop.apibarbershop.reservas.application.event.ReservaDomainEventPublisher;
import com.diamondbarbershop.apibarbershop.reservas.application.strategy.MontoCalculoStrategy;
import com.diamondbarbershop.apibarbershop.reservas.application.strategy.MontoCalculoStrategySelector;
import com.diamondbarbershop.apibarbershop.reservas.domain.model.Precio;
import com.diamondbarbershop.apibarbershop.reservas.domain.model.Reserva;
import com.diamondbarbershop.apibarbershop.reservas.domain.port.in.CrearReservaUseCase;
import com.diamondbarbershop.apibarbershop.reservas.domain.port.out.ReservaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * Caso de uso: Crear Reserva.
 *
 * Application Service — orquesta el flujo sin tener lógica de negocio propia.
 *
 * Responsabilidades:
 *   1. Disparar la cadena de validaciones (Chain of Responsibility — PB-15).
 *   2. Seleccionar y aplicar la estrategia de cálculo del monto (Strategy — PB-11).
 *   3. Delegar la creación al aggregate Reserva.crear() con el monto calculado.
 *   4. Persistir a través del puerto ReservaRepository.
 *   5. Publicar los Domain Events emitidos por el aggregate.
 */
@Service
@RequiredArgsConstructor
public class CrearReservaApplicationService implements CrearReservaUseCase {

    private final ReservaRepository reservaRepository;

    /**
     * Cabeza de la cadena de validaciones — definida como @Bean en
     * CadenaValidacionReservaConfig.
     */
    private final ValidacionReservaHandler cadenaValidacion;

    /**
     * Selector que decide qué estrategia de cálculo de monto aplicar
     * (estándar o fidelidad, por ahora).
     */
    private final MontoCalculoStrategySelector montoCalculoStrategySelector;

    /**
     * Publisher de Domain Events (PB-13) — distribuye los eventos emitidos
     * por el aggregate a los listeners registrados.
     */
    private final ReservaDomainEventPublisher eventPublisher;

    @Override
    @Transactional
    public Long crear(CrearReservaCommand command) {

        // 1. Disparar la cadena de validaciones (PB-15).
        //    Si cualquier handler falla, lanza ReservaValidacionException
        //    y la ejecución termina aquí.
        cadenaValidacion.validar(command);

        // 2. Aplicar Strategy de cálculo del monto (PB-11).
        //    El selector decide según el flag command.usarRecompensa().
        //    El monto resultante es el que se persiste, no command.precioServicio().
        MontoCalculoStrategy strategy = montoCalculoStrategySelector.seleccionar(command);
        Long montoFinal = strategy.calcular(command);

        // 3. El dominio crea el aggregate con sus invariantes y emite el evento.
        //    El Precio recibido ya viene calculado por la strategy (puede ser 0
        //    si se usa recompensa).
        Reserva reserva = Reserva.crear(
                command.barberoId(),
                command.clienteId(),
                command.servicioId(),
                command.horarioRangoId(),
                new Precio(montoFinal),
                command.fechaReserva(),
                command.adicionales(),
                command.usarRecompensa()
        );

        // 4. Persistir a través del puerto (no del JPA directamente).
        Reserva guardada = reservaRepository.save(reserva);

        // 5. Publicar eventos emitidos por el aggregate (PB-13).
        //    El publisher distribuye a todos los listeners registrados:
        //      - RecompensaListener: si usaRecompensa = true, consume las
        //        reservas anteriores del cliente.
        //      - (Futuros) PushNotificacionAdminListener (PB-41),
        //        NotificacionEmailClienteListener (PB-39).
        //    Si algún listener falla, hace rollback de TODA la transacción
        //    (incluye el save de la nueva reserva).
        eventPublisher.publicar(guardada.pullEvents());

        return guardada.getId();
    }
}
