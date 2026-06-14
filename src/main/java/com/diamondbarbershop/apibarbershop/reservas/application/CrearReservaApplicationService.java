package com.diamondbarbershop.apibarbershop.reservas.application;

import com.diamondbarbershop.apibarbershop.reservas.application.chain.ValidacionReservaHandler;
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
 * Después del refactor de PB-15 (Chain of Responsibility), este servicio
 * delega TODAS las validaciones de pre-condición a la cadena inyectada.
 * Aquí solo queda la secuencia: validar → crear (dominio) → persistir → publicar.
 *
 * Responsabilidades:
 *   1. Disparar la cadena de validaciones (Chain of Responsibility — PB-15).
 *   2. Delegar la creación al aggregate Reserva.crear().
 *   3. Persistir a través del puerto ReservaRepository.
 *   4. Publicar los Domain Events emitidos por el aggregate.
 */
@Service
@RequiredArgsConstructor
public class CrearReservaApplicationService implements CrearReservaUseCase {

    private final ReservaRepository reservaRepository;

    /**
     * Cabeza de la cadena de validaciones — definida como @Bean en
     * CadenaValidacionReservaConfig. Es el único bean de su tipo en el contexto,
     * los handlers concretos NO son @Component (se construyen con new dentro
     * del @Bean), por eso no hace falta @Qualifier.
     */
    private final ValidacionReservaHandler cadenaValidacion;

    @Override
    @Transactional
    public Long crear(CrearReservaCommand command) {

        // 1. Disparar la cadena de validaciones (PB-15).
        //    Si cualquier handler falla, lanza ReservaValidacionException
        //    y la ejecución termina aquí — no llegamos a crear ni a persistir.
        cadenaValidacion.validar(command);

        // 2. El dominio crea el aggregate con sus invariantes y emite el evento.
        Reserva reserva = Reserva.crear(
                command.barberoId(),
                command.clienteId(),
                command.servicioId(),
                command.horarioRangoId(),
                new Precio(command.precioServicio()),
                command.fechaReserva(),
                command.adicionales()
        );

        // 3. Persistir a través del puerto (no del JPA directamente).
        Reserva guardada = reservaRepository.save(reserva);

        // 4. Publicar eventos emitidos por el aggregate.
        //    Por ahora solo log — en PB-13 conectaremos el publisher real.
        guardada.pullEvents().forEach(
                event -> System.out.println("[Domain event emitido] "
                        + event.getClass().getSimpleName()));

        return guardada.getId();
    }
}
