package com.diamondbarbershop.apibarbershop.reservas.application;

import com.diamondbarbershop.apibarbershop.identidad.domain.exception.UsuarioExistenteException;
import com.diamondbarbershop.apibarbershop.reservas.infrastructure.persistance.ReservaJpaEntity;
import com.diamondbarbershop.apibarbershop.identidad.infrastructure.persistance.UsuarioJpaEntity;
import com.diamondbarbershop.apibarbershop.reservas.infrastructure.persistance.IReservaJpaRepository;
import com.diamondbarbershop.apibarbershop.identidad.infrastructure.persistance.IUsuarioJpaRepository;
import com.diamondbarbershop.apibarbershop.reservas.domain.port.in.SubirComprobanteUseCase;
import com.diamondbarbershop.apibarbershop.shared.domain.port.out.SubidorImagen;
import com.diamondbarbershop.apibarbershop.util.MensajeError;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * Application service — sube el comprobante de pago de una reserva.
 *
 * Valida ownership: solo el cliente que creó la reserva puede subir su
 * comprobante. La URL se persiste directamente en el campo urlPago de la
 * entidad JPA (acceso pragmático mientras no migremos a un puerto out
 * específico para esta operación).
 */
@Service
@RequiredArgsConstructor
public class SubirComprobanteApplicationService implements SubirComprobanteUseCase {

    private static final String CARPETA_IMAGENES = "pagos";

    private final IReservaJpaRepository reservaJpaRepository;
    private final IUsuarioJpaRepository usuariosRepository;
    private final SubidorImagen subidorImagen;

    @Override
    @Transactional
    public void subir(Long reservaId, MultipartFile imagen, String username) {
        UsuarioJpaEntity usuario = usuariosRepository.findByUsername(username)
                .orElseThrow(() -> new UsuarioExistenteException(MensajeError.USUARIO_NO_EXISTENTE));

        ReservaJpaEntity reserva = reservaJpaRepository.findById(reservaId)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada: " + reservaId));

        if (!reserva.getUsuario().getUsuario_id().equals(usuario.getUsuario_id())) {
            throw new RuntimeException("No autorizado: la reserva no pertenece al usuario actual");
        }

        String url = subidorImagen.subir(imagen, CARPETA_IMAGENES);
        reserva.setUrlPago(url);
        reservaJpaRepository.save(reserva);
    }
}
