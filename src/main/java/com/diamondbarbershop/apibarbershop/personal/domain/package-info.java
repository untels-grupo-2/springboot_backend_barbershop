/**
 * Bounded Context: PERSONAL (Supporting Domain)
 *
 * Responsabilidad: gestionar los barberos de la barbería (alta, baja, edición).
 *
 * Estructura hexagonal completa (Sprint 4 — Cierre arquitectónico):
 *   - domain/model/      → Barbero (aggregate)
 *   - domain/port/in/    → Use cases: Crear, Actualizar, Consultar, Deshabilitar
 *   - domain/port/out/   → BarberoRepository
 *   - application/       → 4 application services
 *   - infrastructure/
 *       ├─ persistance/  → BarberoJpaAdapter
 *       ├─ adapter/      → PersonalFacadeImpl (anti-corruption layer hacia BC Reservas)
 *       └─ rest/         → RestControllerBarbero
 *
 * Cross-BC outgoing:
 *   - Al crear un barbero, dispara el Factory Method del BC Agenda
 *     (HorarioBaseTemplateCreatorSelector) para generar su plantilla inicial
 *     de horario (PB-12).
 *
 * Cross-BC incoming:
 *   - BC Reservas usa PersonalFacade para validar existencia/estado del barbero.
 */
package com.diamondbarbershop.apibarbershop.personal.domain;
