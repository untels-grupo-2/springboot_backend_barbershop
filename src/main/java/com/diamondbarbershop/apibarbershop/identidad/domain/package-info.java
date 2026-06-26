/**
 * Bounded Context: IDENTIDAD Y ACCESO (Supporting Domain)
 *
 * Responsabilidad: autenticación, autorización (JWT + refresh tokens),
 * reset de password, gestión de usuarios y roles.
 *
 * Estructura hexagonal (Sprint 4 — Cierre arquitectónico):
 *   - domain/port/in/    → Use cases: Registrar, Autenticar, ResetPassword,
 *                          Consultar, Actualizar
 *   - application/       → 5 application services
 *   - infrastructure/
 *       ├─ adapter/      → IdentidadFacadeImpl (anti-corruption layer)
 *       └─ rest/         → RestControllerAuth, RestControllerUsuario
 *
 * Deuda técnica conocida:
 *   Este BC NO tiene domain model puro propio (no hay Usuario.java en
 *   domain/model/). Los application services usan directamente la entidad
 *   JPA `models/Usuario.java` legacy. Razón: la complejidad de Spring Security
 *   (roles ManyToMany, JWT, tokens, BCrypt) no aporta valor refactorizar a
 *   dominio puro hoy. Se refactorizará cuando se introduzcan nuevas reglas
 *   de negocio sobre Usuario (estados de cuenta, MFA, etc.).
 *
 * Cross-BC outgoing: ninguno.
 *
 * Cross-BC incoming:
 *   - BC Reservas usa IdentidadFacade para validar cliente y obtener
 *     email/nombre para notificaciones por correo (PB-39).
 *   - BC Notificaciones usa IdentidadFacade para resolver IDs de admins
 *     a la hora de enviar push notifications (PB-41).
 */
package com.diamondbarbershop.apibarbershop.identidad.domain;
