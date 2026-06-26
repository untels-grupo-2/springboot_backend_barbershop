package com.diamondbarbershop.apibarbershop.agenda.infrastructure;

import com.diamondbarbershop.apibarbershop.agenda.domain.port.out.ConsultarReservasActivasPort;
import com.diamondbarbershop.apibarbershop.agenda.domain.port.out.HorarioInstanciaRepository;
import com.diamondbarbershop.apibarbershop.agenda.domain.service.CalcularDisponibilidadBarberoService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración Spring para exponer el Domain Service del BC Agenda
 * como bean.
 *
 * Por qué un @Bean explícito y no @Service en la clase:
 *   CalcularDisponibilidadBarberoService es DOMINIO PURO — no debe tener
 *   anotaciones de Spring. Sin embargo, necesitamos que Spring lo administre
 *   para inyectarlo en los Application Services. La forma idiomática hexagonal
 *   es declararlo aquí, en la capa de infraestructura, sin contaminar el dominio.
 */
@Configuration
public class AgendaDomainConfig {

    @Bean
    public CalcularDisponibilidadBarberoService calcularDisponibilidadBarberoService(
            HorarioInstanciaRepository horarioInstanciaRepository,
            ConsultarReservasActivasPort reservasActivasPort
    ) {
        return new CalcularDisponibilidadBarberoService(
                horarioInstanciaRepository, reservasActivasPort
        );
    }
}
