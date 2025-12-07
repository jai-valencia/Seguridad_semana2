package com.duoc.semana2.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import com.duoc.semana2.model.Usuario;
import com.duoc.semana2.repository.UsuarioRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService usuarioService;

    // ======================================================
    // obtenerPorUsername
    // ======================================================
    @Test
    void obtenerPorUsername_existe() {
        Usuario usuario = new Usuario();
        usuario.setUsername("jaime");

        when(usuarioRepository.findByUsername("jaime"))
                .thenReturn(Optional.of(usuario));

        Usuario resultado = usuarioService.obtenerPorUsername("jaime");

        assertNotNull(resultado);
        assertEquals("jaime", resultado.getUsername());
    }

    @Test
    void obtenerPorUsername_noExiste() {
        when(usuarioRepository.findByUsername("x"))
                .thenReturn(Optional.empty());

        Usuario resultado = usuarioService.obtenerPorUsername("x");

        assertNull(resultado);
    }

    // ======================================================
    // actualizarPerfil
    // ======================================================
    @Test
    void actualizarPerfil_sinPasswordNueva() {
        Usuario BD = new Usuario();
        BD.setUsername("jaime");
        BD.setNombre("old");
        BD.setEmail("old@test.com");

        Usuario actualizado = new Usuario();
        actualizado.setNombre("new");
        actualizado.setEmail("new@test.com");
        actualizado.setBiografia("bio");

        when(usuarioRepository.findByUsername("jaime"))
                .thenReturn(Optional.of(BD));

        usuarioService.actualizarPerfil("jaime", actualizado, "");

        assertEquals("new", BD.getNombre());
        assertEquals("new@test.com", BD.getEmail());
        assertEquals("bio", BD.getBiografia());

        verify(usuarioRepository).save(BD);
    }

    @Test
    void actualizarPerfil_conPasswordNueva() {
        Usuario BD = new Usuario();
        BD.setUsername("jaime");
        BD.setPassword("old");

        Usuario actualizado = new Usuario();

        when(usuarioRepository.findByUsername("jaime"))
                .thenReturn(Optional.of(BD));

        when(passwordEncoder.encode("1234")).thenReturn("HASH");

        usuarioService.actualizarPerfil("jaime", actualizado, "1234");

        assertEquals("HASH", BD.getPassword());
        verify(usuarioRepository).save(BD);
    }

    @Test
    void actualizarPerfil_usuarioNoExiste() {
        when(usuarioRepository.findByUsername("x"))
                .thenReturn(Optional.empty());

        usuarioService.actualizarPerfil("x", new Usuario(), "pass");

        verify(usuarioRepository, never()).save(any());
    }
}
