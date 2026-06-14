package com.diamondbarbershop.apibarbershop.reservas.application.chain;

import com.diamondbarbershop.apibarbershop.reservas.domain.port.out.CatalogoFacade;
import com.diamondbarbershop.apibarbershop.reservas.domain.port.out.IdentidadFacade;
import com.diamondbarbershop.apibarbershop.reservas.domain.port.out.PersonalFacade;
import com.diamondbarbershop.apibarbershop.reservas.domain.port.out.ReservaRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración del patrón Chain of Responsibility (PB-15 + PB-16).
 *
 * Aquí —y SOLO aquí— se define el orden de los handlers. Esa concentración
 * es deliberada: si mañana cambia el orden de validaciones o se agrega/elimina
 * una, solo se toca este archivo. Ni el Application Service ni los handlers
 * mismos saben en qué posición están.
 *
 * Orden actual:
 *   1. ValidarFechaReservaHandler         — falla rápido, no requiere BD
 *   2. ValidarBarberoExisteHandler        — consulta BC Personal vía Facade
 *   3. ValidarServicioExisteHandler       — consulta BC Catálogo vía Facade
 *   4. ValidarUsuarioActivoHandler        — consulta BC Identidad vía Facade
 *   5. ValidarHorarioDisponibleHandler    — query del propio BC Reservas (más costosa)
 *
 * Criterio de ordenamiento:
 *   - Validaciones sin BD primero (Fecha) → fallar rápido si el comando es inválido.
 *   - Validaciones de "el ID existe" después (lookups simples vía Facade).
 *   - La query más costosa (¿el slot está ocupado?) al final, porque solo tiene
 *     sentido ejecutarla si todo lo anterior pasó.
 *
 * Demostración de extensibilidad (PB-16):
 *   Cuando se agregaron los 3 handlers nuevos (Barbero, Servicio, Usuario)
 *   en PB-16, NO se modificaron:
 *     - El CrearReservaApplicationService
 *     - Los handlers existentes (Fecha, HorarioDisponible)
 *     - La clase abstracta ValidacionReservaHandler
 *   Solo se agregaron las clases nuevas + esta configuración.
 *   Eso es exactamente lo que prometía Chain of Responsibility.
 */
@Configuration
public class CadenaValidacionReservaConfig {

    @Bean(name = "cadenaValidacionCrearReserva")
    public ValidacionReservaHandler cadenaValidacionCrearReserva(
            ReservaRepository reservaRepository,
            PersonalFacade personalFacade,
            CatalogoFacade catalogoFacade,
            IdentidadFacade identidadFacade
    ) {
        ValidacionReservaHandler fecha             = new ValidarFechaReservaHandler();
        ValidacionReservaHandler barbero           = new ValidarBarberoExisteHandler(personalFacade);
        ValidacionReservaHandler servicio          = new ValidarServicioExisteHandler(catalogoFacade);
        ValidacionReservaHandler usuario           = new ValidarUsuarioActivoHandler(identidadFacade);
        ValidacionReservaHandler horarioDisponible = new ValidarHorarioDisponibleHandler(reservaRepository);

        // Encadenamiento fluido: setNext() retorna el siguiente para encadenar.
        fecha.setNext(barbero)
                .setNext(servicio)
                .setNext(usuario)
                .setNext(horarioDisponible);

        return fecha;
    }
}
