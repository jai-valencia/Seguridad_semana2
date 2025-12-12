package com.duoc.semana2.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.duoc.semana2.model.Usuario;
import com.duoc.semana2.repository.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
public class JpaUserDetailsServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private JpaUserDetailsService userDetailsService;

    private Usuario testUser;

    @BeforeEach
    void setUp() {
        // Crear un usuario de prueba
        testUser = Usuario.builder()
                .id(1L)
                .username("testuser")
                .password("encodedPassword123")
                .email("test@mail.com")
                .nombre("Test User")
                .rol("USER")
                .build();
    }

    // --- Tests del Constructor ---

    @Test
    void testConstructor() {
        // Verifica que el servicio se crea correctamente con el repositorio
        JpaUserDetailsService service = new JpaUserDetailsService(usuarioRepository);
        assertNotNull(service);
    }

    // --- Tests de loadUserByUsername (caso exitoso) ---

    @Test
    void testLoadUserByUsername_UserExists() {
        // Arrange
        when(usuarioRepository.findByUsername("testuser"))
                .thenReturn(Optional.of(testUser));

        // Act
        UserDetails userDetails = userDetailsService.loadUserByUsername("testuser");

        // Assert
        assertNotNull(userDetails);
        assertEquals("testuser", userDetails.getUsername());
        assertEquals("encodedPassword123", userDetails.getPassword());
        
        // Verificar que tiene exactamente 1 autoridad
        assertEquals(1, userDetails.getAuthorities().size());
        
        // Verificar que el rol es ROLE_USER
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_USER")));
        
        // Verificar que se llamó al repositorio
        verify(usuarioRepository, times(1)).findByUsername("testuser");
    }

    @Test
    void testLoadUserByUsername_UserWithDifferentUsername() {
        // Arrange - Crear otro usuario
        Usuario anotherUser = Usuario.builder()
                .id(2L)
                .username("admin")
                .password("adminPass")
                .email("admin@mail.com")
                .nombre("Admin User")
                .rol("ADMIN")
                .build();

        when(usuarioRepository.findByUsername("admin"))
                .thenReturn(Optional.of(anotherUser));

        // Act
        UserDetails userDetails = userDetailsService.loadUserByUsername("admin");

        // Assert
        assertNotNull(userDetails);
        assertEquals("admin", userDetails.getUsername());
        assertEquals("adminPass", userDetails.getPassword());
        
        verify(usuarioRepository, times(1)).findByUsername("admin");
    }

    // --- Tests de loadUserByUsername (caso de error) ---

    @Test
    void testLoadUserByUsername_UserNotFound() {
        // Arrange
        when(usuarioRepository.findByUsername("nonexistent"))
                .thenReturn(Optional.empty());

        // Act & Assert
        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername("nonexistent")
        );

        assertEquals("Usuario no encontrado", exception.getMessage());
        verify(usuarioRepository, times(1)).findByUsername("nonexistent");
    }

    @Test
    void testLoadUserByUsername_NullUsername() {
        // Arrange
        when(usuarioRepository.findByUsername(null))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(
                UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername(null)
        );

        verify(usuarioRepository, times(1)).findByUsername(null);
    }

    @Test
    void testLoadUserByUsername_EmptyUsername() {
        // Arrange
        when(usuarioRepository.findByUsername(""))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(
                UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername("")
        );

        verify(usuarioRepository, times(1)).findByUsername("");
    }

    // --- Tests de Roles y Authorities ---

    @Test
    void testLoadUserByUsername_AlwaysHasRoleUser() {
        // Arrange - Usuario con rol diferente en la BD
        Usuario adminUser = Usuario.builder()
                .id(3L)
                .username("superadmin")
                .password("superPass")
                .email("super@mail.com")
                .nombre("Super Admin")
                .rol("ADMIN") // Rol diferente en la entidad
                .build();

        when(usuarioRepository.findByUsername("superadmin"))
                .thenReturn(Optional.of(adminUser));

        // Act
        UserDetails userDetails = userDetailsService.loadUserByUsername("superadmin");

        // Assert - Verificar que SIEMPRE retorna ROLE_USER (según tu implementación)
        assertEquals(1, userDetails.getAuthorities().size());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_USER")));
    }

    // --- Tests de Integración del UserDetails ---

    @Test
    void testLoadUserByUsername_UserDetailsProperties() {
        // Arrange
        when(usuarioRepository.findByUsername("testuser"))
                .thenReturn(Optional.of(testUser));

        // Act
        UserDetails userDetails = userDetailsService.loadUserByUsername("testuser");

        // Assert - Verificar propiedades del UserDetails
        assertTrue(userDetails.isEnabled());
        assertTrue(userDetails.isAccountNonExpired());
        assertTrue(userDetails.isAccountNonLocked());
        assertTrue(userDetails.isCredentialsNonExpired());
    }

    // --- Test de Cobertura de Múltiples Llamadas ---

    @Test
    void testLoadUserByUsername_MultipleCalls() {
        // Arrange
        when(usuarioRepository.findByUsername(anyString()))
                .thenReturn(Optional.of(testUser));

        // Act - Llamar múltiples veces
        userDetailsService.loadUserByUsername("testuser");
        userDetailsService.loadUserByUsername("testuser");

        // Assert - Verificar que se llamó 2 veces
        verify(usuarioRepository, times(2)).findByUsername("testuser");
    }

    // --- Test con Usuario Mínimo ---

    @Test
    void testLoadUserByUsername_UserWithMinimalData() {
        // Arrange - Usuario solo con campos requeridos
        Usuario minimalUser = Usuario.builder()
                .username("minimal")
                .password("pass")
                .build();

        when(usuarioRepository.findByUsername("minimal"))
                .thenReturn(Optional.of(minimalUser));

        // Act
        UserDetails userDetails = userDetailsService.loadUserByUsername("minimal");

        // Assert
        assertNotNull(userDetails);
        assertEquals("minimal", userDetails.getUsername());
        assertEquals("pass", userDetails.getPassword());
    }
}