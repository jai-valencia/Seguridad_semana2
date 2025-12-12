package com.duoc.semana2.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;

@ExtendWith(MockitoExtension.class)
public class JwtAuthenticationFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private FilterChain filterChain;

    private JwtAuthenticationFilter filter;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        filter = new JwtAuthenticationFilter(jwtService, userDetailsService);
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        
        // Limpiar el contexto de seguridad antes de cada test
        SecurityContextHolder.clearContext();

        // Crear un UserDetails de prueba
        userDetails = User.builder()
                .username("testuser")
                .password("password")
                .authorities(List.of(new SimpleGrantedAuthority("ROLE_USER")))
                .build();
    }

    @AfterEach
    void tearDown() {
        // Limpiar el contexto después de cada test
        SecurityContextHolder.clearContext();
    }

    // --- Tests del Constructor ---

    @Test
    void testConstructor() {
        JwtAuthenticationFilter newFilter = new JwtAuthenticationFilter(jwtService, userDetailsService);
        assertNotNull(newFilter);
    }

    // --- Tests de Autenticación Exitosa con Bearer Token ---

    @Test
    void testDoFilterInternal_ValidBearerToken_SetsAuthentication() throws ServletException, IOException {
        // Arrange
        String token = "valid.jwt.token";
        request.addHeader("Authorization", "Bearer " + token);

        when(jwtService.extractUsername(token)).thenReturn("testuser");
        when(userDetailsService.loadUserByUsername("testuser")).thenReturn(userDetails);
        when(jwtService.isTokenValid(token, "testuser")).thenReturn(true);

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(auth);
        assertEquals("testuser", auth.getName());
        assertEquals(userDetails, auth.getPrincipal());
        
        verify(jwtService, times(1)).extractUsername(token);
        verify(userDetailsService, times(1)).loadUserByUsername("testuser");
        verify(jwtService, times(1)).isTokenValid(token, "testuser");
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_ValidBearerToken_WithAuthorities() throws ServletException, IOException {
        // Arrange
        String token = "valid.jwt.token";
        request.addHeader("Authorization", "Bearer " + token);

        when(jwtService.extractUsername(token)).thenReturn("testuser");
        when(userDetailsService.loadUserByUsername("testuser")).thenReturn(userDetails);
        when(jwtService.isTokenValid(token, "testuser")).thenReturn(true);

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(auth.getAuthorities());
        assertEquals(1, auth.getAuthorities().size());
    }

    // --- Tests con Cookie JWT ---

    @Test
    void testDoFilterInternal_ValidCookieToken_SetsAuthentication() throws ServletException, IOException {
        // Arrange
        String token = "valid.jwt.token";
        Cookie jwtCookie = new Cookie("JWT", token);
        request.setCookies(jwtCookie);

        when(jwtService.extractUsername(token)).thenReturn("testuser");
        when(userDetailsService.loadUserByUsername("testuser")).thenReturn(userDetails);
        when(jwtService.isTokenValid(token, "testuser")).thenReturn(true);

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(auth);
        assertEquals("testuser", auth.getName());
        
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_MultipleCookies_FindsJwtCookie() throws ServletException, IOException {
        // Arrange
        String token = "valid.jwt.token";
        Cookie sessionCookie = new Cookie("SESSIONID", "abc123");
        Cookie jwtCookie = new Cookie("JWT", token);
        Cookie otherCookie = new Cookie("OTHER", "value");
        request.setCookies(sessionCookie, jwtCookie, otherCookie);

        when(jwtService.extractUsername(token)).thenReturn("testuser");
        when(userDetailsService.loadUserByUsername("testuser")).thenReturn(userDetails);
        when(jwtService.isTokenValid(token, "testuser")).thenReturn(true);

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(auth);
        assertEquals("testuser", auth.getName());
    }

    @Test
    void testDoFilterInternal_CookieWithoutJwt_NoAuthentication() throws ServletException, IOException {
        // Arrange
        Cookie otherCookie = new Cookie("OTHER", "value");
        request.setCookies(otherCookie);

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNull(auth);
        
        verify(jwtService, never()).extractUsername(anyString());
        verify(filterChain, times(1)).doFilter(request, response);
    }

    // --- Tests de Token Inválido ---

    @Test
    void testDoFilterInternal_InvalidToken_NoAuthentication() throws ServletException, IOException {
        // Arrange
        String token = "invalid.jwt.token";
        request.addHeader("Authorization", "Bearer " + token);

        when(jwtService.extractUsername(token)).thenReturn("testuser");
        when(userDetailsService.loadUserByUsername("testuser")).thenReturn(userDetails);
        when(jwtService.isTokenValid(token, "testuser")).thenReturn(false);

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNull(auth);
        
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_TokenThrowsException_NoAuthentication() throws ServletException, IOException {
        // Arrange
        String token = "malformed.token";
        request.addHeader("Authorization", "Bearer " + token);

        when(jwtService.extractUsername(token)).thenThrow(new RuntimeException("Invalid token"));

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNull(auth);
        
        verify(filterChain, times(1)).doFilter(request, response);
    }

    // --- Tests sin Token ---

    @Test
    void testDoFilterInternal_NoToken_NoAuthentication() throws ServletException, IOException {
        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNull(auth);
        
        verify(jwtService, never()).extractUsername(anyString());
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_NoCookies_NoAuthentication() throws ServletException, IOException {
        // Arrange - sin cookies
        request.setCookies((Cookie[]) null);

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNull(auth);
        
        verify(filterChain, times(1)).doFilter(request, response);
    }

    // --- Tests de Authorization Header Inválido ---

    @Test
    void testDoFilterInternal_AuthHeaderWithoutBearer_NoAuthentication() throws ServletException, IOException {
        // Arrange
        request.addHeader("Authorization", "Basic dXNlcjpwYXNz");

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNull(auth);
        
        verify(jwtService, never()).extractUsername(anyString());
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_EmptyAuthHeader_NoAuthentication() throws ServletException, IOException {
        // Arrange
        request.addHeader("Authorization", "");

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNull(auth);
        
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_AuthHeaderBearerOnly_NoAuthentication() throws ServletException, IOException {
        // Arrange
        request.addHeader("Authorization", "Bearer ");

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNull(auth);
        
        verify(filterChain, times(1)).doFilter(request, response);
    }

    // --- Tests cuando ya existe autenticación ---

    @Test
    void testDoFilterInternal_AuthenticationAlreadyExists_SkipsValidation() throws ServletException, IOException {
        // Arrange
        String token = "valid.jwt.token";
        request.addHeader("Authorization", "Bearer " + token);

        // Establecer autenticación previa
        Authentication existingAuth = new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                "existinguser", null, List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
        SecurityContextHolder.getContext().setAuthentication(existingAuth);

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(auth);
        assertEquals("existinguser", auth.getName());
        
        // No debe intentar validar el token
        verify(jwtService, never()).extractUsername(anyString());
        verify(filterChain, times(1)).doFilter(request, response);
    }

    // --- Tests del método resolveToken (indirectamente) ---

    @Test
    void testResolveToken_PriorizaBearerTokenOverCookie() throws ServletException, IOException {
        // Arrange
        String bearerToken = "bearer.token";
        String cookieToken = "cookie.token";
        
        request.addHeader("Authorization", "Bearer " + bearerToken);
        Cookie jwtCookie = new Cookie("JWT", cookieToken);
        request.setCookies(jwtCookie);

        when(jwtService.extractUsername(bearerToken)).thenReturn("testuser");
        when(userDetailsService.loadUserByUsername("testuser")).thenReturn(userDetails);
        when(jwtService.isTokenValid(bearerToken, "testuser")).thenReturn(true);

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert - Debe usar el bearer token, no el de la cookie
        verify(jwtService, times(1)).extractUsername(bearerToken);
        verify(jwtService, never()).extractUsername(cookieToken);
    }

    // --- Tests de FilterChain ---

    @Test
    void testDoFilterInternal_AlwaysCallsFilterChain() throws ServletException, IOException {
        // Arrange - múltiples escenarios

        // Caso 1: Sin token
        filter.doFilterInternal(request, response, filterChain);
        verify(filterChain, times(1)).doFilter(request, response);

        // Caso 2: Con token válido
        SecurityContextHolder.clearContext();
        request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer valid.token");
        when(jwtService.extractUsername("valid.token")).thenReturn("testuser");
        when(userDetailsService.loadUserByUsername("testuser")).thenReturn(userDetails);
        when(jwtService.isTokenValid("valid.token", "testuser")).thenReturn(true);
        
        filter.doFilterInternal(request, response, filterChain);
        verify(filterChain, times(2)).doFilter(any(), any());
    }

    // --- Tests de UserDetailsService Exception ---

    @Test
    void testDoFilterInternal_UserDetailsServiceThrowsException_NoAuthentication() throws ServletException, IOException {
        // Arrange
        String token = "valid.jwt.token";
        request.addHeader("Authorization", "Bearer " + token);

        when(jwtService.extractUsername(token)).thenReturn("testuser");
        when(userDetailsService.loadUserByUsername("testuser"))
                .thenThrow(new RuntimeException("User not found"));

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNull(auth);
        
        verify(filterChain, times(1)).doFilter(request, response);
    }

    // --- Test de WebAuthenticationDetails ---

    @Test
    void testDoFilterInternal_SetsWebAuthenticationDetails() throws ServletException, IOException {
        // Arrange
        String token = "valid.jwt.token";
        request.addHeader("Authorization", "Bearer " + token);
        request.setRemoteAddr("192.168.1.1");

        when(jwtService.extractUsername(token)).thenReturn("testuser");
        when(userDetailsService.loadUserByUsername("testuser")).thenReturn(userDetails);
        when(jwtService.isTokenValid(token, "testuser")).thenReturn(true);

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(auth);
        assertNotNull(auth.getDetails());
    }
}