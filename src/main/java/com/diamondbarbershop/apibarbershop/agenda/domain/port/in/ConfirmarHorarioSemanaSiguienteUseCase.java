package com.diamondbarbershop.apibarbershop.agenda.domain.port.in;

/**
 * Puerto de entrada — generar las instancias de HorarioBarberoInstancia
 * para la próxima semana a partir de la plantilla HorarioBarberoBase.
 *
 * Disparado:
 *   - Manualmente desde el panel admin (PUT /horarios-base/confirmacion).
 *   - Automáticamente por el HorarioBaseScheduler cada domingo a las 23:50.
 */
public interface ConfirmarHorarioSemanaSiguienteUseCase {

    void confirmar();
}
