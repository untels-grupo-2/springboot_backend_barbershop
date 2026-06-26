package com.diamondbarbershop.apibarbershop.identidad.infrastructure.persistance;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad JPA — tabla `usuarios`.
 *
 * Detalle de infraestructura del BC Identidad. Relaciona con RolJpaEntity
 * vía tabla intermedia `usuario_roles` (ManyToMany interno al BC).
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "usuarios")
public class UsuarioJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "usuario_id")
    private Long usuario_id;

    @NotBlank(message = "El campo username no puede estar vacío")
    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    @NotBlank(message = "El campo password no puede estar vacío")
    private String password;

    @Column(nullable = false)
    @NotBlank(message = "El campo nombre no puede estar vacío")
    private String nombre;

    @Column(nullable = false)
    @NotBlank(message = "El campo apellido no puede estar vacío")
    private String apellido;

    @Column(nullable = false)
    @Email(message = "El correo electrónico no es válido")
    @NotBlank(message = "El campo email no puede estar vacío")
    private String email;

    @NotBlank(message = "El campo celular no puede estar vacío")
    private String celular;

    @Column(nullable = true)
    private String urlUsuario;

    private String tokenPassword;

    @Column(name = "last_token_request")
    private LocalDateTime lastTokenRequest;

    private String refreshToken;
    private LocalDateTime refreshTokenExpiryDate;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(
            name = "usuario_roles",
            joinColumns = @JoinColumn(name = "usuario_id", referencedColumnName = "usuario_id"),
            inverseJoinColumns = @JoinColumn(name = "rol_id", referencedColumnName = "rol_id"))
    private List<RolJpaEntity> roles = new ArrayList<>();
}
