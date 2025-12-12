package com.duoc.semana2.security;

import com.duoc.semana2.model.Usuario;
import com.duoc.semana2.repository.UsuarioRepository;

import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
public class JpaUserDetailsService implements UserDetailsService {

    private final UsuarioRepository repo;

    public JpaUserDetailsService(UsuarioRepository repo) {
        this.repo = repo;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Usuario u = repo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        // Por ahora todos los usuarios tienen el rol básico
        List<GrantedAuthority> roles = List.of(
                new SimpleGrantedAuthority("ROLE_USER")
        );

        return new org.springframework.security.core.userdetails.User(
                u.getUsername(),
                u.getPassword(),
                roles
        );
    }
}
