package com.diamondbarbershop.apibarbershop.agenda.domain.model;

/**
 * Tipo de plantilla inicial de horario que se asigna a un barbero al
 * registrarlo en el sistema (PB-12 — Factory Method).
 *
 * El BC Agenda decide cuántas entradas de HorarioBarberoBase generar
 * según el tipo de plantilla elegido por el administrador.
 *
 * Valores:
 *   - COMPLETA      → 7 días × 3 tipos de horario = 21 entradas (semana entera)
 *   - FIN_DE_SEMANA → Sábado y Domingo × 3 tipos = 6 entradas (barberos solo
 *                     de fines de semana, ej. medio tiempo)
 *
 * Agregar un tipo nuevo en el futuro (ej. SOLO_TARDES, FERIADOS) solo requiere:
 *   1. Agregar el valor a este enum.
 *   2. Crear una nueva subclase de HorarioBaseTemplateCreator.
 *   3. Actualizar el switch en HorarioBaseTemplateCreatorSelector
 *      (Java avisa en compilación si te olvidas un caso del enum).
 */
public enum TipoPlantillaHorario {
    COMPLETA,
    FIN_DE_SEMANA
}
