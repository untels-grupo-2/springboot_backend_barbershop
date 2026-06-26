package com.diamondbarbershop.apibarbershop.reservas.infrastructure.mapper;

import com.diamondbarbershop.apibarbershop.reservas.infrastructure.rest.dto.DtoReservaResponse;
import com.diamondbarbershop.apibarbershop.reservas.infrastructure.persistance.ReservaJpaEntity;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.context.annotation.Bean;

import java.util.List;

/**
 * Data Mapper — transforma ReservaJpaEntity (infraestructura JPA)
 * en DtoReservaResponse (contrato HTTP con el cliente).
 *
 * MapStruct genera la implementación concreta en tiempo de compilación.
 * componentModel = "spring" → la clase generada es un @Component inyectable.
 *
 * Campos con lógica especial:
 *   - estado:      EstadoReserva (enum) → String (.name())
 *   - montoTotal:  no viene de la entidad individual, se calcula
 *                  sobre una lista → se ignora aquí y se asigna
 *                  manualmente en el servicio.
 */
@Mapper(componentModel = "spring")
public interface ReservaEntityMapper {

    @Mapping(source = "reserva_id", target = "reservaId")
    @Mapping(source = "barbero.nombre", target = "barberoNombre")
    @Mapping(source = "usuario.nombre", target = "usuarioNombre")
    @Mapping(source = "usuario.usuario_id", target = "usuarioId")
    @Mapping(source = "horarioRango.rango", target = "horarioRango")
    @Mapping(source = "servicioEntity.nombre", target = "servicioNombre")
    @Mapping(target = "estado", expression = "java(reserva.getEstado().name())")
    @Mapping(target = "montoTotal", ignore = true)
    DtoReservaResponse toDto(ReservaJpaEntity reserva);

    // MapStruct genera automáticamente la implementación de esta lista
    // llamando toDto() por cada elemento
    List<DtoReservaResponse> toDtoList(List<ReservaJpaEntity> reservaEntities);
}
