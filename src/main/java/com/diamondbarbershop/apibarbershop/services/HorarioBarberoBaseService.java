package com.diamondbarbershop.apibarbershop.services;

import com.diamondbarbershop.apibarbershop.dtos.horarioBase.DtoHorarioBase;
import com.diamondbarbershop.apibarbershop.models.HorarioBarberoBase;
import com.diamondbarbershop.apibarbershop.models.HorarioBarberoInstancia;
import com.diamondbarbershop.apibarbershop.repositories.IBarberoRepository;
import com.diamondbarbershop.apibarbershop.repositories.IHorarioBarberoBaseRepository;
import com.diamondbarbershop.apibarbershop.repositories.IHorarioBarberoInstanciaRepository;
import com.diamondbarbershop.apibarbershop.repositories.ITipoHorarioRepository;
import com.diamondbarbershop.apibarbershop.util.DiaSemana;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HorarioBarberoBaseService {

    private final IHorarioBarberoBaseRepository horarioBarberoBaseRepository;
    private final IBarberoRepository barberoRepository;
    private final ITipoHorarioRepository tipoHorarioRepository;
    private final IHorarioBarberoInstanciaRepository horarioBarberoInstanciaRepository;

    // PB-12: el método crearHorarioBaseInicial() fue eliminado.
    // La creación de la plantilla inicial de un barbero ahora se delega al
    // Factory Method en agenda/application/factory/:
    //   - HorarioBaseTemplateCreator  (clase abstracta con el Template Method)
    //   - PlantillaCompletaCreator    / PlantillaFinDeSemanaCreator (concretos)
    //   - HorarioBaseTemplateCreatorSelector (elige el Creator según el DTO)
    // Ver BarberoService.crear() para el punto de uso.

    @Transactional
    public void actualizarTurnosDia(DtoHorarioBase dtoHorarioBase){
        DiaSemana dia =  dtoHorarioBase.getDia();
        Map<Long,List<Long>> turnosPorTipo = dtoHorarioBase.getTurnosPorTipo();

        List<HorarioBarberoBase> registrosDia = horarioBarberoBaseRepository.findByDia(dia);

        for (HorarioBarberoBase registro : registrosDia){
            Long tipoHorarioId = registro.getTipoHorario().getId();
            Long barberoId = registro.getBarbero().getBarbero_id();

            List<Long> barberosAsignados = turnosPorTipo.getOrDefault(tipoHorarioId,List.of());

            if (barberosAsignados.contains(barberoId)){
                registro.setEst_id(1);
            } else {
                registro.setEst_id(null);
            }
        }
        horarioBarberoBaseRepository.saveAll(registrosDia);
    }

    @Transactional
    public void confirmarHorarioBaseParaSemanasSiguientes(){

        LocalDate proximoLunes = obtenerProximoLunes();
        LocalDate proximoDomingo = proximoLunes.plusDays(6);

        // Elimina horarios de la próxima semana antes de insertar
        horarioBarberoInstanciaRepository.deleteByFechaBetween(proximoLunes, proximoDomingo);

        List<HorarioBarberoBase> baseActivos = horarioBarberoBaseRepository.findAll()
                .stream()
                .filter(r -> r.getEst_id() != null && r.getEst_id()==1)
                .collect(Collectors.toList());


        List<HorarioBarberoInstancia> instancias = new ArrayList<>();
        for(HorarioBarberoBase base : baseActivos){
            DiaSemana dia = base.getDia();
            LocalDate fechaExacta = proximoLunes.plusDays(dia.ordinal());

            HorarioBarberoInstancia nueva = new HorarioBarberoInstancia();
            nueva.setBarbero(base.getBarbero());
            nueva.setTipoHorario(base.getTipoHorario());
            nueva.setDia(dia);
            nueva.setFecha(fechaExacta);

            instancias.add(nueva);
        }
        horarioBarberoInstanciaRepository.saveAll(instancias);
    }

    private LocalDate obtenerProximoLunes() {
        LocalDate hoy = LocalDate.now();
        return hoy.with(TemporalAdjusters.next(DayOfWeek.MONDAY));
    }


}
