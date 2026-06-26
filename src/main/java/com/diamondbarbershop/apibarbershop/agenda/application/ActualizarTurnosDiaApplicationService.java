package com.diamondbarbershop.apibarbershop.agenda.application;

import com.diamondbarbershop.apibarbershop.agenda.domain.model.HorarioBarberoBase;
import com.diamondbarbershop.apibarbershop.agenda.domain.port.in.ActualizarTurnosDiaUseCase;
import com.diamondbarbershop.apibarbershop.agenda.domain.port.out.HorarioBarberoBaseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Application service — actualiza los turnos asignados de un día específico
 * en la plantilla base.
 *
 * Lógica:
 *   1. Cargar todas las entradas del día.
 *   2. Para cada entrada, ver si el barbero está en la lista del Map del comando
 *      para su tipoHorario → asignarTurno() o quitarTurno().
 *   3. Persistir todas las entradas modificadas.
 */
@Service
@RequiredArgsConstructor
public class ActualizarTurnosDiaApplicationService implements ActualizarTurnosDiaUseCase {

    private final HorarioBarberoBaseRepository horarioBaseRepository;

    @Override
    @Transactional
    public void actualizar(ActualizarTurnosDiaCommand command) {
        List<HorarioBarberoBase> registrosDia = horarioBaseRepository.findByDia(command.dia());

        for (HorarioBarberoBase registro : registrosDia) {
            List<Long> barberosAsignados =
                    command.turnosPorTipo().getOrDefault(registro.getTipoHorarioId(), List.of());

            if (barberosAsignados.contains(registro.getBarberoId())) {
                registro.asignarTurno();
            } else {
                registro.quitarTurno();
            }
        }

        horarioBaseRepository.saveAll(registrosDia);
    }
}
