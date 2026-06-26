/**
 * Bounded Context: VALORACIONES (Supporting Domain)
 *
 * Responsabilidad: gestionar las reseñas y calificaciones que los clientes
 * dejan sobre el servicio recibido. Incluye moderación por parte del admin.
 *
 * Estructura hexagonal completa (Sprint 4 — Cierre arquitectónico):
 *   - domain/model/      → Valoracion (aggregate)
 *   - domain/port/in/    → Use cases: Crear, Listar, Desactivar
 *   - domain/port/out/   → ValoracionRepository, ValoracionListadoView (Read Model)
 *   - application/       → 3 application services
 *   - infrastructure/
 *       ├─ persistance/  → ValoracionJpaAdapter
 *       └─ rest/         → RestControllerValoracion
 *
 * Patrón aplicado: Read Model (proyección de solo lectura con nombre y celular
 * del cliente resueltos en un solo query JPA, evitando N+1).
 *
 * Cross-BC outgoing:
 *   - El adapter consulta IUsuarioJpaRepository legacy para resolver el Usuario
 *     JPA al guardar (relación @ManyToOne). Esta dependencia desaparecerá
 *     cuando se migren las JPA entities en una iteración futura.
 */
package com.diamondbarbershop.apibarbershop.valoraciones.domain;
