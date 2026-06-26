/**
 * Bounded Context: CATÁLOGO DE SERVICIOS (Supporting Domain)
 *
 * Responsabilidad: gestionar los servicios que ofrece la barbería y sus precios.
 *
 * Estructura hexagonal completa (Sprint 4 — Cierre arquitectónico):
 *   - domain/model/      → Servicio (aggregate), TipoServicio
 *   - domain/port/in/    → Use cases: Crear, Actualizar, Consultar, Deshabilitar, ListarTipos
 *   - domain/port/out/   → ServicioRepository, TipoServicioRepository
 *   - application/       → 5 application services (uno por use case)
 *   - infrastructure/
 *       ├─ persistance/  → ServicioJpaAdapter, TipoServicioJpaAdapter
 *       ├─ adapter/      → CatalogoFacadeImpl (anti-corruption layer hacia BC Reservas)
 *       └─ rest/         → RestControllerServicio
 *
 * Cross-BC outgoing: ninguno (Catálogo es supporting domain — no necesita
 * llamar a otros BCs).
 *
 * Cross-BC incoming (otros BCs lo consultan vía Facade):
 *   - BC Reservas usa CatalogoFacade para validar existencia/precio del servicio.
 */
package com.diamondbarbershop.apibarbershop.catalogo.domain;
