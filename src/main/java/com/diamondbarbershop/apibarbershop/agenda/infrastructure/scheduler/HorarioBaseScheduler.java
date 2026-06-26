package com.diamondbarbershop.apibarbershop.agenda.infrastructure.scheduler;

import com.diamondbarbershop.apibarbershop.agenda.domain.port.in.ConfirmarHorarioSemanaSiguienteUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Scheduler que confirma automáticamente el horario para la próxima semana
 * cada domingo a las 23:50.
 *
 * Reemplaza el scheduler legacy en `util/HorarioBaseScheduler.java`.
 */
@Component
@RequiredArgsConstructor
public class HorarioBaseScheduler {

    private final ConfirmarHorarioSemanaSiguienteUseCase confirmarHorarioUseCase;

    @Scheduled(cron = "0 50 23 ? * SUN")
    public void confirmarHorarioAutomatico() {
        confirmarHorarioUseCase.confirmar();
    }
}
