package com.diamondbarbershop.apibarbershop.security.service;

import com.diamondbarbershop.apibarbershop.identidad.infrastructure.persistance.RolJpaEntity;
import com.diamondbarbershop.apibarbershop.identidad.infrastructure.persistance.UsuarioJpaEntity;
import com.diamondbarbershop.apibarbershop.identidad.infrastructure.persistance.IUsuarioJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomUsersDetailsService implements UserDetailsService {
    private final IUsuarioJpaRepository usuariosRepo;

    //Método para traernos una lista de autoridades por medio de una lista de roles
    public Collection<GrantedAuthority> mapToAuthorities(List<RolJpaEntity> roles) {
        return roles.stream().map(rol -> new SimpleGrantedAuthority(rol.getName())).collect(Collectors.toList());
    }


    //Método para traernos un usuario con todos sus datos por medio de sus username
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UsuarioJpaEntity usuario = usuariosRepo.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("UsuarioJpaEntity no encontrado"));
        return new CustomUserDetails(
                usuario.getUsername(),
                usuario.getPassword(),
                usuario.getNombre(),
                usuario.getApellido(),
                usuario.getEmail(),
                usuario.getRoles().stream()
                        .map(rol -> new SimpleGrantedAuthority(rol.getName()))
                        .collect(Collectors.toList()),
                usuario.getUrlUsuario());
    }


}
