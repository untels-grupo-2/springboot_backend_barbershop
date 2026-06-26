package com.diamondbarbershop.apibarbershop.reservas.infrastructure.rest;

import com.diamondbarbershop.apibarbershop.agenda.domain.port.in.ListarBarberosDisponiblesUseCase;
import com.diamondbarbershop.apibarbershop.agenda.infrastructure.rest.dto.DtoBarberoDisponible;
import com.diamondbarbershop.apibarbershop.shared.infrastructure.rest.ApiResponse;
import com.diamondbarbershop.apibarbershop.reservas.infrastructure.rest.dto.DtoReserva;
import com.diamondbarbershop.apibarbershop.reservas.infrastructure.rest.dto.DtoReporteResponse;
import com.diamondbarbershop.apibarbershop.reservas.infrastructure.rest.dto.DtoReservaResponse;
import com.diamondbarbershop.apibarbershop.catalogo.domain.exception.ServicioNoEncontradoException;
import com.diamondbarbershop.apibarbershop.identidad.domain.exception.UsuarioExistenteException;
import com.diamondbarbershop.apibarbershop.identidad.infrastructure.persistance.UsuarioJpaEntity;
import com.diamondbarbershop.apibarbershop.identidad.infrastructure.persistance.IUsuarioJpaRepository;
import com.diamondbarbershop.apibarbershop.reservas.domain.port.in.ConsultarReservasUseCase;
import com.diamondbarbershop.apibarbershop.reservas.domain.port.in.CrearReservaUseCase;
import com.diamondbarbershop.apibarbershop.reservas.domain.port.in.FiltroReservaQuery;
import com.diamondbarbershop.apibarbershop.reservas.domain.port.in.GestionarReservaUseCase;
import com.diamondbarbershop.apibarbershop.reservas.domain.port.in.ListarReservasUsuarioUseCase;
import com.diamondbarbershop.apibarbershop.reservas.domain.port.in.ObtenerReportesUseCase;
import com.diamondbarbershop.apibarbershop.reservas.domain.port.in.SubirComprobanteUseCase;
import com.diamondbarbershop.apibarbershop.reservas.domain.port.in.VerificarRecompensaDisponibleUseCase;
import com.diamondbarbershop.apibarbershop.reservas.domain.port.out.CatalogoFacade;
import com.diamondbarbershop.apibarbershop.util.EstadoReserva;
import com.diamondbarbershop.apibarbershop.util.MensajeError;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

/**
 * Controller del BC Reservas — totalmente migrado a hexagonal.
 *
 * Ya no inyecta ReservaService legacy ni IServicioJpaRepository.
 * Todo se hace vía use cases del propio BC Reservas + facades para datos
 * cross-BC.
 *
 * El endpoint /barberos-disponibles consume el use case del BC Agenda
 * (no de Reservas) porque la disponibilidad la conoce Agenda.
 */
@RestController
@RequestMapping("/reservas")
@RequiredArgsConstructor
public class RestControllerReserva {

    private final CrearReservaUseCase crearReservaUseCase;
    private final GestionarReservaUseCase gestionarReservaUseCase;
    private final ConsultarReservasUseCase consultarReservasUseCase;
    private final SubirComprobanteUseCase subirComprobanteUseCase;
    private final ListarReservasUsuarioUseCase listarReservasUsuarioUseCase;
    private final VerificarRecompensaDisponibleUseCase verificarRecompensaUseCase;
    private final ObtenerReportesUseCase obtenerReportesUseCase;
    private final ListarBarberosDisponiblesUseCase listarBarberosDisponiblesUseCase;

    private final IUsuarioJpaRepository usuariosRepository;     // solo para resolver username → id
    private final CatalogoFacade catalogoFacade;              // obtener precio del servicio

    // ── Crear reserva ────────────────────────────────────────────────────────────

    @PostMapping
    public ResponseEntity<ApiResponse<Object>> crearReserva(
            @RequestBody DtoReserva dto,
            Authentication authentication) {
        crearReservaInterno(dto, authentication, false);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.succes("Reserva creada correctamente", null));
    }

    @PostMapping("/recompensa")
    public ResponseEntity<ApiResponse<Object>> crearReservaRecompensa(
            @RequestBody DtoReserva dto,
            Authentication authentication) {
        // El flag usarRecompensa = true activa PrecioFidelidadStrategy (PB-11)
        // y el RecompensaListener (PB-13) consume las reservas anteriores.
        crearReservaInterno(dto, authentication, true);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.succes("Reserva con recompensa creada correctamente", null));
    }

    private void crearReservaInterno(DtoReserva dto, Authentication authentication, boolean usarRecompensa) {
        UsuarioJpaEntity usuario = usuariosRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new UsuarioExistenteException(MensajeError.USUARIO_NO_EXISTENTE));

        Long precio = catalogoFacade.obtenerPrecio(dto.getServicioId())
                .orElseThrow(() -> new ServicioNoEncontradoException(MensajeError.SERVICIO_NO_ENCONTRADO));

        crearReservaUseCase.crear(new CrearReservaUseCase.CrearReservaCommand(
                dto.getBarberoId(),
                usuario.getUsuario_id(),
                dto.getServicioId(),
                dto.getHorarioRangoId(),
                precio,
                dto.getFechaReserva(),
                dto.getAdicionales(),
                usarRecompensa
        ));
    }

    // ── Subir comprobante ────────────────────────────────────────────────────────

    @PostMapping("/{id}/comprobante")
    public ResponseEntity<ApiResponse<Object>> subirComprobante(
            @PathVariable Long id,
            @RequestPart("imagen") MultipartFile imagen,
            Authentication authentication) {
        subirComprobanteUseCase.subir(id, imagen, authentication.getName());
        return ResponseEntity.ok(ApiResponse.succes("Comprobante subido", null));
    }

    // ── Listados ─────────────────────────────────────────────────────────────────

    @GetMapping("/admin")
    public ResponseEntity<ApiResponse<Page<DtoReservaResponse>>> listarReservas(
            @RequestParam(required = false) Long barberoId,
            @RequestParam(required = false) Long clienteId,
            @RequestParam(required = false) EstadoReserva estado,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaDesde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaHasta,
            Pageable pageable) {
        FiltroReservaQuery filtro = new FiltroReservaQuery(
                barberoId, clienteId, estado, fechaDesde, fechaHasta);
        return ResponseEntity.ok(ApiResponse.succes(
                "Lista de reservas",
                consultarReservasUseCase.listar(filtro, pageable)));
    }

    @GetMapping("/mis-reservas")
    public ResponseEntity<ApiResponse<List<DtoReservaResponse>>> listarMisReservas(Authentication authentication) {
        return ResponseEntity.ok(ApiResponse.succes(
                "Lista de mis reservas",
                listarReservasUsuarioUseCase.listarPorUsername(authentication.getName())));
    }

    @GetMapping("/barberos-disponibles")
    public ResponseEntity<ApiResponse<List<DtoBarberoDisponible>>> listarBarberosDisponibles(
            @RequestParam("fecha") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            @RequestParam("tipoHorarioId") Long tipoHorarioId,
            @RequestParam("horarioRangoId") Long horarioRangoId) {
        return ResponseEntity.ok(ApiResponse.succes(
                "Lista de barberos disponibles",
                listarBarberosDisponiblesUseCase.listar(fecha, tipoHorarioId, horarioRangoId)));
    }

    // ── Cambio de estado ─────────────────────────────────────────────────────────

    @PutMapping("/{id}/estado")
    public ResponseEntity<ApiResponse<Object>> cambiarEstado(
            @PathVariable Long id,
            @RequestParam("estado") EstadoReserva estado,
            @RequestParam(value = "motivoDescripcion", required = false) String motivoDescripcion) {
        switch (estado) {
            case CONFIRMADA -> gestionarReservaUseCase.confirmar(id);
            case REALIZADA  -> gestionarReservaUseCase.marcarComoRealizada(id);
            case CANCELADA  -> gestionarReservaUseCase.cancelar(id, motivoDescripcion);
            default         -> throw new IllegalArgumentException(
                    "Estado no soportado: " + estado);
        }
        return ResponseEntity.ok(ApiResponse.succes("Estado actualizado", null));
    }

    // ── Recompensa ───────────────────────────────────────────────────────────────

    @GetMapping("/recompensa/estado")
    public ResponseEntity<ApiResponse<Boolean>> consultarRecompensa(Authentication authentication) {
        boolean estado = verificarRecompensaUseCase.tieneRecompensa(authentication.getName());
        return ResponseEntity.ok(ApiResponse.succes("Estado enviado", estado));
    }

    // ── Reportes ─────────────────────────────────────────────────────────────────

    @GetMapping("/reportes")
    public ResponseEntity<ApiResponse<DtoReporteResponse>> obtenerReportes(
            @RequestParam LocalDate fechaInicio,
            @RequestParam LocalDate fechaFin,
            @RequestParam(required = false) String servicio) {
        return ResponseEntity.ok(ApiResponse.succes(
                "Reportes obtenidos",
                obtenerReportesUseCase.obtener(fechaInicio, fechaFin, servicio)));
    }
}
