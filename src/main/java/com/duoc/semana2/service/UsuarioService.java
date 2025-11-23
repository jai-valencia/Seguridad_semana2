package com.duoc.semana2.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.duoc.semana2.model.Usuario;
import com.duoc.semana2.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsuarioService {
    
    private UsuarioRepository usuarioRepository;
    
    
    private PasswordEncoder passwordEncoder;
    
    public Usuario obtenerPorUsername(String username) {
        return usuarioRepository.findByUsername(username).orElse(null);
    }
    
    public void actualizarPerfil(String username, Usuario usuarioActualizado, String nuevaPassword) {
        Usuario usuario = obtenerPorUsername(username);
        
        if (usuario != null) {
            usuario.setNombre(usuarioActualizado.getNombre());
            usuario.setEmail(usuarioActualizado.getEmail());
            usuario.setBiografia(usuarioActualizado.getBiografia());
            
            if (nuevaPassword != null && !nuevaPassword.isEmpty()) {
                usuario.setPassword(passwordEncoder.encode(nuevaPassword));
            }
            
            usuarioRepository.save(usuario);
        }
    }
}
