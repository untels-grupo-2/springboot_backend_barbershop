package com.diamondbarbershop.apibarbershop.repositories;

import com.diamondbarbershop.apibarbershop.models.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IUsuariosRepository extends JpaRepository<Usuario, Long> {
    //Método para poder buscar un usuario mediante su nombre
    Optional<Usuario> findByUsername(String username);

    Optional<Usuario> findByTokenPassword(String password);

    Optional<Usuario> findByRefreshToken(String refreshToken);

    //Metodo para confirmar si email existe en al bd
    Boolean existsByEmail(String email);

    //Método para poder verificar si un usuario existe en nuestra base de datos
    Boolean existsByUsername(String username);

    /**
     * Devuelve los usuarios que tienen un rol con el nombre especificado.
     * Spring Data resuelve la consulta automáticamente usando la relación
     * ManyToMany Usuario.roles ↔ Rol.name.
     *
     * Usado por IdentidadFacadeImpl.obtenerIdsUsuariosConRol() para PB-41.
     */
    List<Usuario> findByRoles_Name(String roleName);
}
