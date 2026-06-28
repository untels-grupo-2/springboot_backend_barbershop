package com.diamondbarbershop.apibarbershop.identidad.application;

import com.diamondbarbershop.apibarbershop.identidad.domain.exception.RolNoEncontradoException;
import com.diamondbarbershop.apibarbershop.identidad.domain.exception.UsuarioExistenteException;
import com.diamondbarbershop.apibarbershop.identidad.infrastructure.persistance.IRolJpaRepository;
import com.diamondbarbershop.apibarbershop.identidad.infrastructure.persistance.IUsuarioJpaRepository;
import com.diamondbarbershop.apibarbershop.identidad.infrastructure.persistance.RolJpaEntity;
import com.diamondbarbershop.apibarbershop.identidad.infrastructure.persistance.UsuarioJpaEntity;
import com.diamondbarbershop.apibarbershop.identidad.infrastructure.rest.dto.DtoRegistro;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegistrarUsuarioApplicationServiceTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private IRolJpaRepository rolesRepository;

    @Mock
    private IUsuarioJpaRepository usuariosRepository;

    @InjectMocks
    private RegistrarUsuarioApplicationService service;

    private DtoRegistro crearDtoRegistro() {
        DtoRegistro dto = new DtoRegistro();
        dto.setUsername("juanperez");
        dto.setPassword("password123");
        dto.setNombre("Juan");
        dto.setApellido("Perez");
        dto.setEmail("juan@email.com");
        dto.setCelular("999888777");
        return dto;
    }

    @Test
    @DisplayName("Debe registrar usuario exitosamente con password codificado y rol asignado")
    void should_registerUser_when_validData() {
        DtoRegistro dto = crearDtoRegistro();
        RolJpaEntity rol = new RolJpaEntity(1L, "USER");
        when(usuariosRepository.existsByUsername("juanperez")).thenReturn(false);
        when(usuariosRepository.existsByEmail("juan@email.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(rolesRepository.findByName("USER")).thenReturn(Optional.of(rol));

        service.registrar(dto, "USER");

        ArgumentCaptor<UsuarioJpaEntity> captor = ArgumentCaptor.forClass(UsuarioJpaEntity.class);
        verify(usuariosRepository).save(captor.capture());
        UsuarioJpaEntity savedUser = captor.getValue();
        assertThat(savedUser.getPassword()).isEqualTo("encodedPassword");
        assertThat(savedUser.getRoles()).hasSize(1);
        assertThat(savedUser.getRoles().get(0).getName()).isEqualTo("USER");
        assertThat(savedUser.getUsername()).isEqualTo("juanperez");
        assertThat(savedUser.getNombre()).isEqualTo("Juan");
    }

    @Test
    @DisplayName("Debe lanzar UsuarioExistenteException cuando el username ya existe")
    void should_throwUsuarioExistenteException_when_usernameExists() {
        DtoRegistro dto = crearDtoRegistro();
        when(usuariosRepository.existsByUsername("juanperez")).thenReturn(true);

        assertThatThrownBy(() -> service.registrar(dto, "USER"))
                .isInstanceOf(UsuarioExistenteException.class);

        verify(usuariosRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe lanzar UsuarioExistenteException cuando el email ya existe")
    void should_throwUsuarioExistenteException_when_emailExists() {
        DtoRegistro dto = crearDtoRegistro();
        when(usuariosRepository.existsByUsername("juanperez")).thenReturn(false);
        when(usuariosRepository.existsByEmail("juan@email.com")).thenReturn(true);

        assertThatThrownBy(() -> service.registrar(dto, "USER"))
                .isInstanceOf(UsuarioExistenteException.class);

        verify(usuariosRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe lanzar RolNoEncontradoException cuando el rol no existe")
    void should_throwRolNoEncontradoException_when_rolNotFound() {
        DtoRegistro dto = crearDtoRegistro();
        when(usuariosRepository.existsByUsername("juanperez")).thenReturn(false);
        when(usuariosRepository.existsByEmail("juan@email.com")).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encoded");
        when(rolesRepository.findByName("ADMIN")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.registrar(dto, "ADMIN"))
                .isInstanceOf(RolNoEncontradoException.class);

        verify(usuariosRepository, never()).save(any());
    }
}
