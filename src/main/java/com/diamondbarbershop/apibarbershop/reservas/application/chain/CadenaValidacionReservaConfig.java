package com.diamondbarbershop.apibarbershop.reservas.application.chain;

import com.diamondbarbershop.apibarbershop.reservas.domain.port.out.ReservaRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración del patrón Chain of Responsibility (PB-15).
 *
 * Aquí —y solo aquí— se define el orden de los handlers. Esa concentración
 * es deliberada: si mañana cambia el orden de validaciones o se agrega/elimina
 * una, solo se toca este archivo. Ni el Application Service ni los handlers
 * mismos saben en qué posición están.
 *
 * Orden actual:
 *   1. ValidarFechaReservaHandler       — falla rápido, no requiere BD
 *   2. ValidarHorarioDisponibleHandler  — requiere consulta a BD
 *
 * Handlers planificados (se agregarán en PB-16 cuando existan las Facades):
 *   3. ValidarBarberoExisteHandler      — necesita PersonalFacade
 *   4. ValidarServicioExisteHandler     — necesita CatalogoFacade
 *   5. ValidarUsuarioActivoHandler      — necesita IdentidadFacade
 *
 * Criterio de ordenamiento: validaciones baratas primero (que no tocan BD),
 * luego las que requieren consultas, para fallar rápido cuando sea posible.
 */
@Configuration
public class CadenaValidacionReservaConfig {

    /**
     * Bean único que expone el primer eslabón de la cadena.
     * El Application Service inyecta esta cabeza y dispara validar().
     *
     * El nombre del bean ("cadenaValidacionCrearReserva") es explícito para
     * evitar colisiones si en el futuro se crea otra cadena (por ejemplo,
     * una para cancelar reserva).
     */
    @Bean(name = "cadenaValidacionCrearReserva")
    public ValidacionReservaHandler cadenaValidacionCrearReserva(
            ReservaRepository reservaRepository
    ) {
        ValidacionReservaHandler fecha = new ValidarFechaReservaHandler();
        ValidacionReservaHandler horarioDisponible =
                new ValidarHorarioDisponibleHandler(reservaRepository);

        // setNext() retorna el siguiente, permite encadenar fluidamente.
        fecha.setNext(horarioDisponible);

        return fecha;
    }
}
