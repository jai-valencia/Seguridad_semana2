package com.duoc.semana2.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.duoc.semana2.model.Usuario;
import com.duoc.semana2.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public Usuario obtenerPorUsername(String username) {
        return usuarioRepository.findByUsername(username).orElse(null);
    }

    public void actualizarPerfil(String username, Usuario usuarioActualizado, String nuevaPassword) {

        Usuario usuario = obtenerPorUsername(username);

        if (usuario != null) {

            // Tu entidad Usuario SOLO tiene username, email, password
            usuario.setEmail(usuarioActualizado.getEmail());

            if (nuevaPassword != null && !nuevaPassword.isEmpty()) {
                usuario.setPassword(passwordEncoder.encode(nuevaPassword));
            }

            usuarioRepository.save(usuario);
        }
    }

    public Usuario actualizarUsuario(Long id, Usuario usuarioActualizado) {

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        usuario.setUsername(usuarioActualizado.getUsername());
        usuario.setEmail(usuarioActualizado.getEmail());

        if (usuarioActualizado.getPassword() != null && !usuarioActualizado.getPassword().isEmpty()) {
            usuario.setPassword(usuarioActualizado.getPassword());
        }

        return usuarioRepository.save(usuario);
    }
}
