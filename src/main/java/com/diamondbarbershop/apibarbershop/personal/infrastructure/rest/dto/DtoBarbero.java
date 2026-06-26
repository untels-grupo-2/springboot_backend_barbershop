package com.diamondbarbershop.apibarbershop.personal.infrastructure.rest.dto;

import com.diamondbarbershop.apibarbershop.agenda.domain.model.TipoPlantillaHorario;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DtoBarbero {

    @NotBlank(message = "El campo nombre no puede estar vacío.")
    private String nombre;

    /**
     * Tipo de plantilla inicial de horario a generar para el barbero (PB-12).
     * Opcional — si no se envía, se usa COMPLETA por compatibilidad con el
     * comportamiento previo a PB-12.
     */
    private TipoPlantillaHorario tipoPlantilla = TipoPlantillaHorario.COMPLETA;
}
