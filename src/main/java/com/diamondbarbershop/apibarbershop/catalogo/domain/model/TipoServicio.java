package com.diamondbarbershop.apibarbershop.catalogo.domain.model;

/**
 * Tipo de servicio del catálogo (ej. Corte, Barba, Combo).
 * Domain puro — sin anotaciones JPA.
 */
public class TipoServicio {

    private Long id;
    private String nombre;

    public TipoServicio() {}

    public static TipoServicio reconstitute(Long id, String nombre) {
        TipoServicio t = new TipoServicio();
        t.id = id;
        t.nombre = nombre;
        return t;
    }

    public Long getId()       { return id; }
    public String getNombre() { return nombre; }
}
