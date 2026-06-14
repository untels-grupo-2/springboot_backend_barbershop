package com.diamondbarbershop.apibarbershop.reservas.domain.port.in;

import java.time.LocalDate;

/**
 * Puerto de ENTRADA (Inbound Port) — caso de uso "Crear Reserva".
 *
 * ¿Qué es un puerto de entrada en Arquitectura Hexagonal?
 *   Es la INTERFAZ que define qué puede hacer el exterior con el sistema.
 *   El "exterior" en este caso es el RestController (adaptador de entrada).
 *
 * Flujo:
 *   HTTP Request
 *     → ReservaController (adaptador REST)
 *     → CrearReservaUseCase (este puerto)
 *     → ReservaApplicationService (implementación del puerto)
 *     → Reserva.crear() (lógica de dominio)
 *     → ReservaRepository.save() (puerto de salida)
 *     → ReservaJpaAdapter (adaptador de persistencia)
 *     → Base de datos
 *
 * ¿Por qué usar Command objects en lugar de parámetros sueltos?
 *   - Hace el método más legible y extensible.
 *   - Si se agrega un campo, no cambia la firma del método.
 *   - El Command puede tener su propia validación.
 */
public interface CrearReservaUseCase {

    /**
     * @return ID de la reserva creada
     */
    Long crear(CrearReservaCommand command);

    /**
     * Command object: encapsula todos los datos necesarios para crear una reserva.
     * Es un Record de Java — inmutable por diseño.
     *
     * Campo usarRecompensa (PB-11 Strategy):
     *   Si es true, el cliente declara que quiere consumir su recompensa de fidelidad
     *   acumulada (precio = 0). El MontoCalculoStrategySelector lo lee y aplica
     *   PrecioFidelidadStrategy. Si es false, aplica PrecioEstandarStrategy.
     *
     *   Hoy el backend confía en el flag (decisión explícita del cliente).
     *   En PB-16 (Facade), se validará contra IdentidadFacade que el cliente
     *   efectivamente tenga la recompensa disponible antes de aplicarla.
     */
    record CrearReservaCommand(
            Long barberoId,
            Long clienteId,
            Long servicioId,
            Long horarioRangoId,
            Long precioServicio,
            LocalDate fechaReserva,
            String adicionales,
            boolean usarRecompensa
    ) {}
}
