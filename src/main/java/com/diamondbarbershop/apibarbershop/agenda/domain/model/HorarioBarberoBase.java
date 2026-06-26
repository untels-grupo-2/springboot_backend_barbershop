package com.diamondbarbershop.apibarbershop.agenda.domain.model;

import com.diamondbarbershop.apibarbershop.util.DiaSemana;

/**
 * Modelo de dominio del BC Agenda — plantilla SEMANAL recurrente del horario
 * de un barbero (no una fecha concreta).
 *
 * El admin asigna en esta plantilla qué turnos trabaja cada barbero cada
 * día de la semana. El HorarioBaseScheduler convierte la plantilla en
 * instancias concretas (HorarioBarberoInstancia) cada semana.
 *
 * Reglas:
 *   - estId = 1 → el barbero trabaja ese turno habitualmente.
 *   - estId = null → descansa.
 *   - estado = 1 → la entrada está activa (1 por defecto). 0 = inactiva (no se usa, baja lógica).
 */
public class HorarioBarberoBase {

    private Long id;
    private Long barberoId;
    private Long tipoHorarioId;
    private DiaSemana dia;
    private Integer estId;
    private Integer estado;

    public HorarioBarberoBase() {}

    public boolean estaActivoEnEsteTurno() {
        return estId != null && estId == 1;
    }

    public void asignarTurno()    { this.estId = 1; }
    public void quitarTurno()     { this.estId = null; }

    public Long getId()              { return id; }
    public Long getBarberoId()       { return barberoId; }
    public Long getTipoHorarioId()   { return tipoHorarioId; }
    public DiaSemana getDia()        { return dia; }
    public Integer getEstId()        { return estId; }
    public Integer getEstado()       { return estado; }

    public void setId(Long id)                        { this.id = id; }
    public void setBarberoId(Long barberoId)          { this.barberoId = barberoId; }
    public void setTipoHorarioId(Long tipoHorarioId)  { this.tipoHorarioId = tipoHorarioId; }
    public void setDia(DiaSemana dia)                 { this.dia = dia; }
    public void setEstId(Integer estId)               { this.estId = estId; }
    public void setEstado(Integer estado)             { this.estado = estado; }
}
