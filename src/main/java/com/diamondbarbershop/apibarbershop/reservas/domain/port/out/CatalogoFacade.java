package com.diamondbarbershop.apibarbershop.reservas.domain.port.out;

/**
 * Facade (GoF — Estructural) — PB-16.
 *
 * Puerto de SALIDA del BC Reservas hacia el BC Catálogo.
 *
 * El BC Reservas no debe importar nada del BC Catálogo directamente
 * (entidades JPA, repositorios, servicios). Toda su interacción pasa por
 * este contrato, que el BC Catálogo implementa internamente.
 *
 * Beneficios:
 *   1. Desacoplamiento entre BCs: si Catálogo refactoriza su modelo interno,
 *      el BC Reservas no se ve afectado.
 *   2. Anti-Corruption Layer: traduce los detalles internos de Catálogo
 *      (ServicioEntity, tipos, precios, etc.) a información simple.
 *   3. Punto de control único: las consultas que Reservas hace a Catálogo
 *      se concentran aquí — facilita auditar el acoplamiento.
 *
 * El contrato expone primitivos, no domain models. Reservas NO necesita
 * conocer la estructura interna de un Servicio, solo si existe y si puede
 * ser reservado.
 */
public interface CatalogoFacade {

    /**
     * @return true si existe un servicio con ese ID en el catálogo
     */
    boolean existeServicio(Long servicioId);

    /**
     * @return true si el servicio existe y está activo (disponible para reserva)
     */
    boolean estaActivoServicio(Long servicioId);
}
