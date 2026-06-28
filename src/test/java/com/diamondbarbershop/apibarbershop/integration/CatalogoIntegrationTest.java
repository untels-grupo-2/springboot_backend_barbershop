package com.diamondbarbershop.apibarbershop.integration;

import com.diamondbarbershop.apibarbershop.catalogo.domain.model.Servicio;
import com.diamondbarbershop.apibarbershop.catalogo.domain.port.in.ConsultarServiciosUseCase;
import com.diamondbarbershop.apibarbershop.catalogo.domain.port.in.CrearServicioUseCase;
import com.diamondbarbershop.apibarbershop.catalogo.domain.port.in.DeshabilitarServicioUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CatalogoIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private CrearServicioUseCase crearServicioUseCase;

    @Autowired
    private ConsultarServiciosUseCase consultarServiciosUseCase;

    @Autowired
    private DeshabilitarServicioUseCase deshabilitarServicioUseCase;

    @Test
    @DisplayName("Debe crear un servicio y persistirlo en la BD real")
    void should_createAndPersistServicio() {
        Long id = crearServicioUseCase.crear(new CrearServicioUseCase.CrearServicioCommand(
                "Corte Degradado", 25L, "Corte moderno", 1L, null
        ));

        assertThat(id).isNotNull();

        Servicio servicio = consultarServiciosUseCase.obtener(id);
        assertThat(servicio.getNombre()).isEqualTo("Corte Degradado");
        assertThat(servicio.getPrecio()).isEqualTo(25L);
        assertThat(servicio.getDescripcion()).isEqualTo("Corte moderno");
    }

    @Test
    @DisplayName("Debe listar solo servicios activos despues de deshabilitar uno")
    void should_listOnlyActive_afterDisabling() {
        Long id1 = crearServicioUseCase.crear(new CrearServicioUseCase.CrearServicioCommand(
                "Servicio A", 10L, "Desc A", 1L, null
        ));
        Long id2 = crearServicioUseCase.crear(new CrearServicioUseCase.CrearServicioCommand(
                "Servicio B", 20L, "Desc B", 1L, null
        ));

        deshabilitarServicioUseCase.deshabilitar(id1);

        List<Servicio> activos = consultarServiciosUseCase.listarActivos();
        assertThat(activos).noneMatch(s -> s.getId().equals(id1));
        assertThat(activos).anyMatch(s -> s.getId().equals(id2));
    }
}
