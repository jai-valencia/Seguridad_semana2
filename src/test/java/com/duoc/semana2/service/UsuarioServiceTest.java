package com.duoc.semana2.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.duoc.semana2.model.Usuario;
import com.duoc.semana2.repository.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
public class UsuarioServiceTest {

    // 1. Mocks de dependencias
    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    // 2. Inyectamos los mocks en el servicio
    @InjectMocks
    private UsuarioService usuarioService;

    // 3. Objetos de prueba
    private Usuario usuarioExistente;
    private Usuario usuarioActualizadoData;

    @BeforeEach
    void setUp() {
        usuarioExistente = new Usuario();
        usuarioExistente.setId(1L);
        usuarioExistente.setUsername("testuser");
        usuarioExistente.setEmail("old@duoc.cl");
        usuarioExistente.setPassword("hashedpassword");

        usuarioActualizadoData = new Usuario();
        usuarioActualizadoData.setUsername("newuser");
        usuarioActualizadoData.setEmail("new@duoc.cl");
    }

    // =========================================================================
    //                      TESTS PARA obtenerPorUsername
    // =========================================================================

    @Test
    void testObtenerPorUsername_UsuarioExiste() {
        // Simular que el repositorio encuentra el usuario
        when(usuarioRepository.findByUsername("testuser")).thenReturn(Optional.of(usuarioExistente));

        Usuario resultado = usuarioService.obtenerPorUsername("testuser");

        assertNotNull(resultado);
        assertEquals("testuser", resultado.getUsername());
    }

    @Test
    void testObtenerPorUsername_UsuarioNoExiste() {
        // Simular que el repositorio NO encuentra el usuario (cubre .orElse(null))
        when(usuarioRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        Usuario resultado = usuarioService.obtenerPorUsername("nonexistent");

        assertNull(resultado);
    }

    // =========================================================================
    //                      TESTS PARA actualizarPerfil
    // (Cubre 100% de Ramas del método: if (usuario != null) y if (nuevaPassword...))
    // =========================================================================

    @Test
    void testActualizarPerfil_ConNuevaPassword() {
        String nuevaPass = "newpass123";

        // 1. Mocks de búsqueda y codificación
        when(usuarioRepository.findByUsername("testuser")).thenReturn(Optional.of(usuarioExistente));
        when(passwordEncoder.encode(nuevaPass)).thenReturn("newhashedpassword");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioExistente);

        // 2. Ejecución
        usuarioService.actualizarPerfil("testuser", usuarioActualizadoData, nuevaPass);

        // 3. Verificaciones
        // Asegura que la nueva password se codificó
        verify(passwordEncoder, times(1)).encode(nuevaPass);
        // Asegura que se guardó el usuario
        verify(usuarioRepository, times(1)).save(any(Usuario.class));

        // Verifica que se actualizaron los datos correctos
        assertEquals("new@duoc.cl", usuarioExistente.getEmail());
        assertEquals("newhashedpassword", usuarioExistente.getPassword());
    }

    @Test
void testActualizarPerfil_SinNuevaPassword() {
    // 1. Mocks de búsqueda
    when(usuarioRepository.findByUsername("testuser")).thenReturn(Optional.of(usuarioExistente));
    when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioExistente);

    // 2. Ejecución con password vacía/nula (cubre la rama 'else' del if de password)
    usuarioService.actualizarPerfil("testuser", usuarioActualizadoData, "");

    // 3. Verificaciones
    verify(passwordEncoder, never()).encode(anyString());
    verify(usuarioRepository, times(1)).save(any(Usuario.class));

    // VERIFICACIÓN CORREGIDA: Esperamos el nuevo email, la password debe ser la antigua
    assertEquals("new@duoc.cl", usuarioExistente.getEmail()); // ¡CORREGIDO!
    assertEquals("hashedpassword", usuarioExistente.getPassword());
}

    @Test
    void testActualizarPerfil_UsuarioNoEncontrado() {
        // 1. Mocks de búsqueda (cubre la rama 'else' del if principal)
        when(usuarioRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // 2. Ejecución
        usuarioService.actualizarPerfil("nonexistent", usuarioActualizadoData, "anypass");

        // 3. Verificaciones
        // Asegura que NO se intentó guardar el usuario
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    // =========================================================================
    //                      TESTS PARA actualizarUsuario
    // (Cubre 100% de Ramas: orElseThrow y if (usuarioActualizado.getPassword...))
    // =========================================================================

    @Test
    void testActualizarUsuario_ExitoConPassword() {
        Long id = 1L;
        usuarioActualizadoData.setPassword("newpass");

        // 1. Mocks de búsqueda
        when(usuarioRepository.findById(id)).thenReturn(Optional.of(usuarioExistente));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioExistente);

        // 2. Ejecución
        Usuario resultado = usuarioService.actualizarUsuario(id, usuarioActualizadoData);

        // 3. Verificaciones
        // Asegura que los campos se actualizaron
        assertEquals("newuser", resultado.getUsername());
        assertEquals("new@duoc.cl", resultado.getEmail());
        assertEquals("newpass", resultado.getPassword()); // Asumimos que aquí no se hashea
        
        // Verifica que no se llamó al encoder (ya que el servicio no lo usa aquí)
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    void testActualizarUsuario_ExitoSinPassword() {
        Long id = 1L;
        // usuarioActualizadoData no tiene password seteada

        // 1. Mocks de búsqueda
        when(usuarioRepository.findById(id)).thenReturn(Optional.of(usuarioExistente));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioExistente);

        // 2. Ejecución (cubre la rama 'else' del if de password)
        Usuario resultado = usuarioService.actualizarUsuario(id, usuarioActualizadoData);

        // 3. Verificaciones
        // Asegura que la password ANTIGUA se mantuvo
        assertEquals("newuser", resultado.getUsername());
        assertEquals("hashedpassword", resultado.getPassword()); 
    }

    @Test
    void testActualizarUsuario_UsuarioNoEncontrado() {
        Long id = 99L;

        // 1. Mocks (cubre la rama del orElseThrow)
        when(usuarioRepository.findById(id)).thenReturn(Optional.empty());

        // 2. Ejecución: Debe lanzar la excepción
        assertThrows(RuntimeException.class, () -> {
            usuarioService.actualizarUsuario(id, usuarioActualizadoData);
        }, "Se esperaba RuntimeException por usuario no encontrado");
        
        // 3. Verificación
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }
}