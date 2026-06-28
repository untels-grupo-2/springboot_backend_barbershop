package com.diamondbarbershop.apibarbershop.identidad.application;

import com.diamondbarbershop.apibarbershop.identidad.domain.exception.UsuarioExistenteException;
import com.diamondbarbershop.apibarbershop.identidad.infrastructure.persistance.IUsuarioJpaRepository;
import com.diamondbarbershop.apibarbershop.identidad.infrastructure.persistance.RolJpaEntity;
import com.diamondbarbershop.apibarbershop.identidad.infrastructure.persistance.UsuarioJpaEntity;
import com.diamondbarbershop.apibarbershop.identidad.infrastructure.rest.dto.DtoUsuarioResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultarUsuariosApplicationServiceTest {

    @Mock
    private IUsuarioJpaRepository usuariosRepository;

    @InjectMocks
    private ConsultarUsuariosApplicationService service;

    private UsuarioJpaEntity crearUsuario(Long id, String username, String rolName) {
        UsuarioJpaEntity usuario = new UsuarioJpaEntity();
        usuario.setUsuario_id(id);
        usuario.setUsername(username);
        usuario.setPassword("encoded");
        usuario.setNombre("Nombre");
        usuario.setApellido("Apellido");
        usuario.setEmail(username + "@email.com");
        usuario.setCelular("999000111");
        RolJpaEntity rol = new RolJpaEntity(1L, rolName);
        List<RolJpaEntity> roles = new ArrayList<>();
        roles.add(rol);
        usuario.setRoles(roles);
        return usuario;
    }

    @Test
    @DisplayName("Debe retornar solo usuarios con rol USER al listar clientes")
    void should_returnOnlyUserRole_when_listarClientes() {
        UsuarioJpaEntity cliente = crearUsuario(1L, "cliente1", "USER");
        UsuarioJpaEntity admin = crearUsuario(2L, "admin1", "ADMIN");
        when(usuariosRepository.findAll()).thenReturn(List.of(cliente, admin));

        List<DtoUsuarioResponse> resultado = service.listarClientes();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getUsername()).isEqualTo("cliente1");
        verify(usuariosRepository).findAll();
    }

    @Test
    @DisplayName("Debe retornar lista vacia cuando no hay clientes con rol USER")
    void should_returnEmptyList_when_noUsersWithUserRole() {
        UsuarioJpaEntity admin = crearUsuario(1L, "admin1", "ADMIN");
        when(usuariosRepository.findAll()).thenReturn(List.of(admin));

        List<DtoUsuarioResponse> resultado = service.listarClientes();

        assertThat(resultado).isEmpty();
    }

    @Test
    @DisplayName("Debe retornar usuario cuando existe el id")
    void should_returnUser_when_idExists() {
        UsuarioJpaEntity usuario = crearUsuario(1L, "juanperez", "USER");
        when(usuariosRepository.findById(1L)).thenReturn(Optional.of(usuario));

        DtoUsuarioResponse resultado = service.obtener(1L);

        assertThat(resultado.getUsuario_id()).isEqualTo(1L);
        assertThat(resultado.getUsername()).isEqualTo("juanperez");
        assertThat(resultado.getNombre()).isEqualTo("Nombre");
    }

    @Test
    @DisplayName("Debe lanzar UsuarioExistenteException cuando el id no existe")
    void should_throwException_when_idDoesNotExist() {
        when(usuariosRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.obtener(999L))
                .isInstanceOf(UsuarioExistenteException.class);
    }

    @Test
    @DisplayName("Debe retornar usuario cuando existe el username")
    void should_returnUser_when_usernameExists() {
        UsuarioJpaEntity usuario = crearUsuario(1L, "juanperez", "USER");
        when(usuariosRepository.findByUsername("juanperez")).thenReturn(Optional.of(usuario));

        DtoUsuarioResponse resultado = service.obtenerPorUsername("juanperez");

        assertThat(resultado.getUsername()).isEqualTo("juanperez");
        assertThat(resultado.getEmail()).isEqualTo("juanperez@email.com");
    }

    @Test
    @DisplayName("Debe lanzar UsuarioExistenteException cuando el username no existe")
    void should_throwException_when_usernameDoesNotExist() {
        when(usuariosRepository.findByUsername("inexistente")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.obtenerPorUsername("inexistente"))
                .isInstanceOf(UsuarioExistenteException.class);
    }
}
