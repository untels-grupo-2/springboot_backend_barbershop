package com.diamondbarbershop.apibarbershop.agenda.domain.model;

/**
 * Modelo de dominio del BC Agenda — franja horaria dentro de un TipoHorario.
 *
 * Ejemplo: TipoHorario "Mañana" contiene rangos como "08:00-08:30", "08:30-09:00", etc.
 * Cada reserva referencia un HorarioRango específico (el slot concreto que toma
 * el cliente).
 *
 * Es esencialmente datos catálogo: el admin los crea una vez y rara vez cambian.
 */
public class HorarioRango {

    private Long id;
    private String rango;          // formato libre "HH:mm-HH:mm"
    private Long tipoHorarioId;
    private String tipoHorarioNombre;  // resuelto para vistas (mañana/tarde/noche)

    public HorarioRango() {}

    public static HorarioRango reconstitute(Long id, String rango, Long tipoHorarioId, String tipoHorarioNombre) {
        HorarioRango r = new HorarioRango();
        r.id = id;
        r.rango = rango;
        r.tipoHorarioId = tipoHorarioId;
        r.tipoHorarioNombre = tipoHorarioNombre;
        return r;
    }

    public Long getId()                  { return id; }
    public String getRango()             { return rango; }
    public Long getTipoHorarioId()       { return tipoHorarioId; }
    public String getTipoHorarioNombre() { return tipoHorarioNombre; }
}
