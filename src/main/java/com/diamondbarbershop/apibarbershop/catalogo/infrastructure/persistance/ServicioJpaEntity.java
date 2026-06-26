package com.diamondbarbershop.apibarbershop.catalogo.infrastructure.persistance;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidad JPA — tabla `servicios`.
 *
 * Detalle de infraestructura del BC Catálogo. Relaciona con
 * TipoServicioJpaEntity (interno al mismo BC).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "servicios")
public class ServicioJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "servicio_id")
    private Long servicio_id;

    @Column(nullable = false)
    @NotBlank(message = "El campo nombre no puede estar vacío")
    private String nombre;

    @Column(nullable = false)
    @NotNull(message = "El campo precio no puede estar vacío")
    private Long precio;

    @Column(nullable = false)
    @NotBlank(message = "El campo descripción no puede estar vacío")
    private String descripcion;

    @ManyToOne
    @JoinColumn(name = "tipoServicio_id", nullable = false)
    @NotNull(message = "El campo tipoServicio no puede estar vacío")
    private TipoServicioJpaEntity tipoServicio;

    @Column(nullable = true)
    private String urlServicio;

    @Column(nullable = false)
    private Integer estado;
}
