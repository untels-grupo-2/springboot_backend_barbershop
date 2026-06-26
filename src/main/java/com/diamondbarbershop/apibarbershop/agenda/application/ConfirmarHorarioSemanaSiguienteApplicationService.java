package com.diamondbarbershop.apibarbershop.agenda.application;

import com.diamondbarbershop.apibarbershop.agenda.domain.model.HorarioBarberoBase;
import com.diamondbarbershop.apibarbershop.agenda.domain.model.HorarioBarberoInstancia;
import com.diamondbarbershop.apibarbershop.agenda.domain.port.in.ConfirmarHorarioSemanaSiguienteUseCase;
import com.diamondbarbershop.apibarbershop.agenda.domain.port.out.HorarioBarberoBaseRepository;
import com.diamondbarbershop.apibarbershop.agenda.domain.port.out.HorarioInstanciaRepository;
import com.diamondbarbershop.apibarbershop.util.DiaSemana;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

/**
 * Application service — genera las instancias de horario para la próxima semana
 * a partir de la plantilla base activa.
 *
 * Disparado por:
 *   - Admin manualmente (PUT /horarios-base/confirmacion)
 *   - HorarioBaseScheduler cada domingo 23:50
 *
 * NOTA TRANSITORIA: actualmente sobrescribe sin eliminar la semana siguiente
 * (esa parte se delega al adapter porque requiere acceso al JPA repo de
 * Instancia para deleteByFechaBetween). Cuando se completen los adapters
 * con métodos específicos, este servicio se mantiene igual.
 */
@Service
@RequiredArgsConstructor
public class ConfirmarHorarioSemanaSiguienteApplicationService
        implements ConfirmarHorarioSemanaSiguienteUseCase {

    private final HorarioBarberoBaseRepository horarioBaseRepository;
    private final HorarioInstanciaRepository horarioInstanciaRepository;

    @Override
    @Transactional
    public void confirmar() {
        LocalDate proximoLunes = LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.MONDAY));

        List<HorarioBarberoBase> baseActivos = horarioBaseRepository.findActivos();

        for (HorarioBarberoBase base : baseActivos) {
            DiaSemana dia = base.getDia();
            LocalDate fechaExacta = proximoLunes.plusDays(dia.ordinal());

            HorarioBarberoInstancia nueva = new HorarioBarberoInstancia();
            nueva.setBarberoId(base.getBarberoId());
            nueva.setTipoHorarioId(base.getTipoHorarioId());
            nueva.setDia(dia);
            nueva.setFecha(fechaExacta);
            nueva.setEstId(1);

            horarioInstanciaRepository.save(nueva);
        }
    }
}
