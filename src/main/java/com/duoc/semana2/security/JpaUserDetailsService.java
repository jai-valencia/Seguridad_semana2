package com.duoc.semana2.security;

import com.duoc.semana2.model.Usuario;
import com.duoc.semana2.repository.UsuarioRepository;
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
    return org.springframework.security.core.userdetails.User.builder()
        .username(u.getUsername())
        .password(u.getPassword())     
        .authorities(u.getRole())      
        .build();
  }
}
