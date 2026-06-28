package com.diamondbarbershop.apibarbershop.identidad.application;

import com.diamondbarbershop.apibarbershop.identidad.domain.exception.UsuarioExistenteException;
import com.diamondbarbershop.apibarbershop.identidad.infrastructure.persistance.IUsuarioJpaRepository;
import com.diamondbarbershop.apibarbershop.identidad.infrastructure.persistance.UsuarioJpaEntity;
import com.diamondbarbershop.apibarbershop.identidad.infrastructure.rest.dto.DtoUsuario;
import com.diamondbarbershop.apibarbershop.shared.domain.port.out.SubidorImagen;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ActualizarUsuarioApplicationServiceTest {

    @Mock
    private IUsuarioJpaRepository usuariosRepository;

    @Mock
    private SubidorImagen subidorImagen;

    @InjectMocks
    private ActualizarUsuarioApplicationService service;

    private UsuarioJpaEntity crearUsuario(Long id, String username) {
        UsuarioJpaEntity usuario = new UsuarioJpaEntity();
        usuario.setUsuario_id(id);
        usuario.setUsername(username);
        usuario.setPassword("encoded");
        usuario.setNombre("NombreViejo");
        usuario.setApellido("ApellidoViejo");
        usuario.setEmail("viejo@email.com");
        usuario.setCelular("000000000");
        return usuario;
    }

    private DtoUsuario crearDtoUsuario() {
        DtoUsuario dto = new DtoUsuario();
        dto.setNombre("NombreNuevo");
        dto.setApellido("ApellidoNuevo");
        dto.setEmail("nuevo@email.com");
        dto.setCelular("999111222");
        return dto;
    }

    @Test
    @DisplayName("Debe actualizar usuario exitosamente con imagen")
    void should_updateUserWithImage_when_imagenProvided() {
        UsuarioJpaEntity usuario = crearUsuario(1L, "juanperez");
        DtoUsuario dto = crearDtoUsuario();
        MultipartFile imagen = mock(MultipartFile.class);
        when(usuariosRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(subidorImagen.subir(imagen, "usuarios")).thenReturn("https://cdn.example.com/img.jpg");

        service.actualizar(1L, dto, imagen);

        assertThat(usuario.getNombre()).isEqualTo("NombreNuevo");
        assertThat(usuario.getApellido()).isEqualTo("ApellidoNuevo");
        assertThat(usuario.getEmail()).isEqualTo("nuevo@email.com");
        assertThat(usuario.getCelular()).isEqualTo("999111222");
        assertThat(usuario.getUrlUsuario()).isEqualTo("https://cdn.example.com/img.jpg");
        verify(subidorImagen).subir(imagen, "usuarios");
        verify(usuariosRepository).save(usuario);
    }

    @Test
    @DisplayName("Debe actualizar usuario exitosamente sin imagen")
    void should_updateUserWithoutImage_when_imagenIsNull() {
        UsuarioJpaEntity usuario = crearUsuario(1L, "juanperez");
        usuario.setUrlUsuario("https://cdn.example.com/old.jpg");
        DtoUsuario dto = crearDtoUsuario();
        when(usuariosRepository.findById(1L)).thenReturn(Optional.of(usuario));

        service.actualizar(1L, dto, null);

        assertThat(usuario.getNombre()).isEqualTo("NombreNuevo");
        assertThat(usuario.getApellido()).isEqualTo("ApellidoNuevo");
        assertThat(usuario.getUrlUsuario()).isEqualTo("https://cdn.example.com/old.jpg");
        verify(subidorImagen, never()).subir(any(), any());
        verify(usuariosRepository).save(usuario);
    }

    @Test
    @DisplayName("Debe lanzar UsuarioExistenteException cuando el usuario no existe por id")
    void should_throwException_when_userNotFoundById() {
        DtoUsuario dto = crearDtoUsuario();
        when(usuariosRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.actualizar(999L, dto, null))
                .isInstanceOf(UsuarioExistenteException.class);

        verify(usuariosRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe actualizar usuario exitosamente por username")
    void should_updateUser_when_usernameExists() {
        UsuarioJpaEntity usuario = crearUsuario(1L, "juanperez");
        DtoUsuario dto = crearDtoUsuario();
        when(usuariosRepository.findByUsername("juanperez")).thenReturn(Optional.of(usuario));

        service.actualizarPorUsername("juanperez", dto, null);

        assertThat(usuario.getNombre()).isEqualTo("NombreNuevo");
        assertThat(usuario.getApellido()).isEqualTo("ApellidoNuevo");
        assertThat(usuario.getEmail()).isEqualTo("nuevo@email.com");
        assertThat(usuario.getCelular()).isEqualTo("999111222");
        verify(usuariosRepository).save(usuario);
    }

    @Test
    @DisplayName("Debe lanzar UsuarioExistenteException al actualizar por username inexistente")
    void should_throwException_when_usernameNotFound() {
        DtoUsuario dto = crearDtoUsuario();
        when(usuariosRepository.findByUsername("inexistente")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.actualizarPorUsername("inexistente", dto, null))
                .isInstanceOf(UsuarioExistenteException.class);

        verify(usuariosRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe actualizar por username con imagen correctamente")
    void should_updateUserByUsernameWithImage_when_imagenProvided() {
        UsuarioJpaEntity usuario = crearUsuario(1L, "juanperez");
        DtoUsuario dto = crearDtoUsuario();
        MultipartFile imagen = mock(MultipartFile.class);
        when(usuariosRepository.findByUsername("juanperez")).thenReturn(Optional.of(usuario));
        when(subidorImagen.subir(eq(imagen), eq("usuarios"))).thenReturn("https://cdn.example.com/new.jpg");

        service.actualizarPorUsername("juanperez", dto, imagen);

        assertThat(usuario.getUrlUsuario()).isEqualTo("https://cdn.example.com/new.jpg");
        verify(subidorImagen).subir(imagen, "usuarios");
        verify(usuariosRepository).save(usuario);
    }
}
