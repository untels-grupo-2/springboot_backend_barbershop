package com.diamondbarbershop.apibarbershop.controllers;

import com.diamondbarbershop.apibarbershop.dtos.barbero.response.DtoBarberoDisponible;
import com.diamondbarbershop.apibarbershop.dtos.common.ApiResponse;
import com.diamondbarbershop.apibarbershop.dtos.reserva.request.DtoReserva;
import com.diamondbarbershop.apibarbershop.dtos.reserva.response.DtoReporteResponse;
import com.diamondbarbershop.apibarbershop.dtos.reserva.response.DtoReservaResponse;
import com.diamondbarbershop.apibarbershop.models.ServicioEntity;
import com.diamondbarbershop.apibarbershop.models.Usuario;
import com.diamondbarbershop.apibarbershop.repositories.IServicioRepository;
import com.diamondbarbershop.apibarbershop.repositories.IUsuariosRepository;
import com.diamondbarbershop.apibarbershop.reservas.domain.port.in.CrearReservaUseCase;
import com.diamondbarbershop.apibarbershop.reservas.domain.port.in.GestionarReservaUseCase;
import com.diamondbarbershop.apibarbershop.services.ReservaService;
import com.diamondbarbershop.apibarbershop.util.EstadoReserva;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/reservas")
@RequiredArgsConstructor
public class RestControllerReserva {

    private final CrearReservaUseCase crearReservaUseCase;
    private final GestionarReservaUseCase gestionarReservaUseCase;
    private final IUsuariosRepository usuariosRepository;
    private final IServicioRepository servicioRepository;
    private final ReservaService reservaService;

    @GetMapping("/barberos-disponibles")
    public ResponseEntity<ApiResponse<List<DtoBarberoDisponible>>> listarBarberosDisponibles(
            @RequestParam("fecha") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            @RequestParam("tipoHorarioId") Long tipoHorarioId,
            @RequestParam("horarioRangoId") Long horarioRangoId) {
        List<DtoBarberoDisponible> barberos = reservaService.listarBarberosDisponibles(fecha, tipoHorarioId, horarioRangoId);
        return ResponseEntity.ok(ApiResponse.succes("Lista de barberos disponibles",barberos));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Object>> crearReserva(
            @RequestBody DtoReserva dto,
            Authentication authentication) {

        // El controller resuelve clienteId y precio antes de construir el comando
        Usuario usuario = usuariosRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        ServicioEntity servicioEntity = servicioRepository.findById(dto.getServicioId())
                .orElseThrow(() -> new RuntimeException("ServicioEntity no encontrado"));

        crearReservaUseCase.crear(new CrearReservaUseCase.CrearReservaCommand(
                dto.getBarberoId(),
                usuario.getUsuario_id(),
                dto.getServicioId(),
                dto.getHorarioRangoId(),
                servicioEntity.getPrecio(),
                dto.getFechaReserva(),
                dto.getAdicionales(),
                false  // usarRecompensa: este endpoint es la creación normal sin recompensa
        ));

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.succes("Reserva creada correctamente", null));
    }

    @PostMapping("/{id}/comprobante")
    public ResponseEntity<ApiResponse<Object>> subirComprobante(
            @PathVariable Long id,
            @RequestPart("imagen") MultipartFile imagen,
            Authentication authentication) {
        reservaService.subirComprobante(id, imagen, authentication);
        return ResponseEntity.ok(ApiResponse.succes("Comprobante subido", null));
    }

    @GetMapping("/admin")
    public ResponseEntity<ApiResponse<List<DtoReservaResponse>>> listarReservas(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            @RequestParam(required = false) EstadoReserva estado,
            @RequestParam(required = false) Long usuarioId ) {
                List<DtoReservaResponse> reservas = reservaService.listarReservas(fecha, estado,usuarioId);
        return ResponseEntity.ok(ApiResponse.succes("Lista de reservas",reservas));
    }


    @PutMapping("/{id}/estado")
    public ResponseEntity<ApiResponse<Object>> cambiarEstado(
            @PathVariable Long id,
            @RequestParam("estado") EstadoReserva estado,
            @RequestParam(value = "motivoDescripcion", required = false) String motivoDescripcion) {
        // El use case encapsula la transición — el dominio valida que sea un estado válido
        switch (estado) {
            case CONFIRMADA -> gestionarReservaUseCase.confirmar(id);
            case REALIZADA  -> gestionarReservaUseCase.marcarComoRealizada(id);
            case CANCELADA  -> gestionarReservaUseCase.cancelar(id, motivoDescripcion);
            default -> reservaService.cambiarEstado(id, estado, motivoDescripcion);
        }

        return ResponseEntity.ok(ApiResponse.succes("Estado actualizado", null));
    }


    @GetMapping("/mis-reservas")
    public ResponseEntity<ApiResponse<List<DtoReservaResponse>>> listarMisReservas(Authentication authentication) {
        List<DtoReservaResponse> reservas = reservaService.listarReservasPorUsuario(authentication);
        return ResponseEntity.ok(ApiResponse.succes("Lista de mis reservas",reservas));
    }

    @GetMapping("/recompensa/estado")
    public ResponseEntity<ApiResponse<Boolean>> consultarRecompensa(Authentication authentication) {
        Boolean estado = reservaService.buscarReservasRecompensa(authentication);
        return ResponseEntity.ok(ApiResponse.succes("Estado enviado",estado));
    }

    @PostMapping("/recompensa")
    public ResponseEntity<ApiResponse<Object>> crearReservaRecompensa(
            @RequestBody DtoReserva dto,
            Authentication authentication) {
        reservaService.crearReservaRecompensa(dto,authentication);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.succes("Reserva creada correctamente", null));
    }

    @GetMapping("/reportes")
    public ResponseEntity<ApiResponse<DtoReporteResponse>> obtenerReportes(
            @RequestParam LocalDate fechaInicio,
            @RequestParam LocalDate fechaFin,
            @RequestParam (required = false) String servicio){
        DtoReporteResponse reporte = reservaService.obtenerReportes(fechaInicio,fechaFin,servicio);
        return ResponseEntity.ok(ApiResponse.succes("Reportes obtenidos", reporte));
    }
}
