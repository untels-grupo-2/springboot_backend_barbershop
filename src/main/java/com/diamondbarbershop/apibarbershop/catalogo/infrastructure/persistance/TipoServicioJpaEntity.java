package com.diamondbarbershop.apibarbershop.catalogo.infrastructure.persistance;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidad JPA — tabla `tipo_servicios`.
 *
 * Detalle de infraestructura del BC Catálogo. Datos catálogo (seed) —
 * no se crean dinámicamente vía API.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tipo_servicios")
public class TipoServicioJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tipoServicio_Id")
    private Long tipoServicio_id;

    @Column(nullable = false)
    private String nombre;
}
