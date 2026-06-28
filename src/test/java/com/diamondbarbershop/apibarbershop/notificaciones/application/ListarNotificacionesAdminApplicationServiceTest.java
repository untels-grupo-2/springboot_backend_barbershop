package com.diamondbarbershop.apibarbershop.notificaciones.application;

import com.diamondbarbershop.apibarbershop.notificaciones.domain.port.out.NotificacionAdmin;
import com.diamondbarbershop.apibarbershop.notificaciones.domain.port.out.NotificacionAdminRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListarNotificacionesAdminApplicationServiceTest {

    @Mock
    private NotificacionAdminRepository notificacionAdminRepository;

    @InjectMocks
    private ListarNotificacionesAdminApplicationService service;

    @Test
    @DisplayName("Debe retornar pagina de notificaciones del repositorio")
    void should_returnPage_when_listarTodas() {
        Pageable pageable = PageRequest.of(0, 10);
        List<NotificacionAdmin> items = List.of(
                new NotificacionAdmin(1L, "Titulo 1", "Cuerpo 1", "RESERVA_CREADA", false, LocalDateTime.now()),
                new NotificacionAdmin(2L, "Titulo 2", "Cuerpo 2", "RESERVA_CREADA", true, LocalDateTime.now())
        );
        Page<NotificacionAdmin> expected = new PageImpl<>(items, pageable, 2);
        when(notificacionAdminRepository.listar(null, pageable)).thenReturn(expected);

        Page<NotificacionAdmin> result = service.listar(null, pageable);

        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getTotalElements()).isEqualTo(2);
        verify(notificacionAdminRepository).listar(null, pageable);
    }

    @Test
    @DisplayName("Debe filtrar solo notificaciones no leidas cuando se indica")
    void should_filterUnread_when_soloNoLeidasIsTrue() {
        Pageable pageable = PageRequest.of(0, 10);
        List<NotificacionAdmin> items = List.of(
                new NotificacionAdmin(1L, "Titulo 1", "Cuerpo 1", "RESERVA_CREADA", false, LocalDateTime.now())
        );
        Page<NotificacionAdmin> expected = new PageImpl<>(items, pageable, 1);
        when(notificacionAdminRepository.listar(true, pageable)).thenReturn(expected);

        Page<NotificacionAdmin> result = service.listar(true, pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).leida()).isFalse();
        verify(notificacionAdminRepository).listar(true, pageable);
    }
}
