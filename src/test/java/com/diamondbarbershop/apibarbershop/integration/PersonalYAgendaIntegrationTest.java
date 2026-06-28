package com.diamondbarbershop.apibarbershop.integration;

import com.diamondbarbershop.apibarbershop.agenda.domain.model.HorarioBaseView;
import com.diamondbarbershop.apibarbershop.agenda.domain.model.HorarioBarberoBase;
import com.diamondbarbershop.apibarbershop.agenda.domain.model.TipoPlantillaHorario;
import com.diamondbarbershop.apibarbershop.agenda.domain.port.in.ActualizarTurnosDiaUseCase;
import com.diamondbarbershop.apibarbershop.agenda.domain.port.in.ConfirmarHorarioSemanaSiguienteUseCase;
import com.diamondbarbershop.apibarbershop.agenda.domain.port.in.ConsultarHorarioBaseUseCase;
import com.diamondbarbershop.apibarbershop.agenda.domain.port.out.HorarioBarberoBaseRepository;
import com.diamondbarbershop.apibarbershop.agenda.infrastructure.persistance.IHorarioBarberoInstanciaJpaRepository;
import com.diamondbarbershop.apibarbershop.personal.domain.model.Barbero;
import com.diamondbarbershop.apibarbershop.personal.domain.port.in.ConsultarBarberosUseCase;
import com.diamondbarbershop.apibarbershop.personal.domain.port.in.CrearBarberoUseCase;
import com.diamondbarbershop.apibarbershop.util.DiaSemana;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class PersonalYAgendaIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private CrearBarberoUseCase crearBarberoUseCase;

    @Autowired
    private ConsultarBarberosUseCase consultarBarberosUseCase;

    @Autowired
    private ActualizarTurnosDiaUseCase actualizarTurnosDiaUseCase;

    @Autowired
    private ConsultarHorarioBaseUseCase consultarHorarioBaseUseCase;

    @Autowired
    private ConfirmarHorarioSemanaSiguienteUseCase confirmarHorarioUseCase;

    @Autowired
    private HorarioBarberoBaseRepository horarioBaseRepository;

    @Autowired
    private IHorarioBarberoInstanciaJpaRepository instanciaJpaRepository;

    @Test
    @DisplayName("Crear barbero con plantilla COMPLETA genera 21 entradas en horario base")
    void should_create21BaseEntries_when_plantillaCompleta() {
        Long barberoId = crearBarberoUseCase.crear(new CrearBarberoUseCase.CrearBarberoCommand(
                "Carlos Test", null, TipoPlantillaHorario.COMPLETA
        ));

        Barbero barbero = consultarBarberosUseCase.obtener(barberoId);
        assertThat(barbero.getNombre()).isEqualTo("Carlos Test");

        Map<DiaSemana, List<HorarioBaseView>> horarios = consultarHorarioBaseUseCase.listarAgrupadoPorDia();
        long entradasBarbero = horarios.values().stream()
                .flatMap(List::stream)
                .filter(v -> v.barberoId().equals(barberoId))
                .count();

        assertThat(entradasBarbero).isEqualTo(21);
    }

    @Test
    @DisplayName("Crear barbero con plantilla FIN_DE_SEMANA genera 6 entradas en horario base")
    void should_create6BaseEntries_when_plantillaFinDeSemana() {
        Long barberoId = crearBarberoUseCase.crear(new CrearBarberoUseCase.CrearBarberoCommand(
                "Luis Weekend", null, TipoPlantillaHorario.FIN_DE_SEMANA
        ));

        Map<DiaSemana, List<HorarioBaseView>> horarios = consultarHorarioBaseUseCase.listarAgrupadoPorDia();
        long entradasBarbero = horarios.values().stream()
                .flatMap(List::stream)
                .filter(v -> v.barberoId().equals(barberoId))
                .count();

        assertThat(entradasBarbero).isEqualTo(6);
    }

    @Test
    @DisplayName("Flujo completo: asignar turnos en base, confirmar, y verificar instancias")
    void should_generateInstancias_when_confirmarHorario() {
        Long barberoId = crearBarberoUseCase.crear(new CrearBarberoUseCase.CrearBarberoCommand(
                "Pedro Flujo", null, TipoPlantillaHorario.COMPLETA
        ));

        actualizarTurnosDiaUseCase.actualizar(new ActualizarTurnosDiaUseCase.ActualizarTurnosDiaCommand(
                DiaSemana.LUNES,
                Map.of(1L, List.of(barberoId))
        ));

        List<HorarioBarberoBase> lunesRegistros = horarioBaseRepository.findByDia(DiaSemana.LUNES);
        long asignados = lunesRegistros.stream()
                .filter(r -> r.getBarberoId().equals(barberoId) && r.estaActivoEnEsteTurno())
                .count();
        assertThat(asignados).isEqualTo(1);

        confirmarHorarioUseCase.confirmar();

        LocalDate proximoLunes = LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.MONDAY));
        LocalDate proximoDomingo = proximoLunes.plusDays(6);
        var instancias = instanciaJpaRepository.findByFechaBetween(proximoLunes, proximoDomingo);
        assertThat(instancias).isNotEmpty();

        long instanciasBarbero = instancias.stream()
                .filter(i -> i.getBarbero().getBarbero_id().equals(barberoId))
                .count();
        assertThat(instanciasBarbero).isGreaterThanOrEqualTo(1);
    }

    @Test
    @DisplayName("Confirmar horario dos veces no duplica instancias (idempotencia)")
    void should_notDuplicate_when_confirmarTwice() {
        Long barberoId = crearBarberoUseCase.crear(new CrearBarberoUseCase.CrearBarberoCommand(
                "Ana Idempotente", null, TipoPlantillaHorario.COMPLETA
        ));

        actualizarTurnosDiaUseCase.actualizar(new ActualizarTurnosDiaUseCase.ActualizarTurnosDiaCommand(
                DiaSemana.MARTES,
                Map.of(1L, List.of(barberoId))
        ));

        confirmarHorarioUseCase.confirmar();

        LocalDate proximoLunes = LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.MONDAY));
        LocalDate proximoDomingo = proximoLunes.plusDays(6);
        long countPrimera = instanciaJpaRepository.findByFechaBetween(proximoLunes, proximoDomingo).stream()
                .filter(i -> i.getBarbero().getBarbero_id().equals(barberoId))
                .count();

        confirmarHorarioUseCase.confirmar();

        long countSegunda = instanciaJpaRepository.findByFechaBetween(proximoLunes, proximoDomingo).stream()
                .filter(i -> i.getBarbero().getBarbero_id().equals(barberoId))
                .count();

        assertThat(countSegunda).isEqualTo(countPrimera);
    }
}
