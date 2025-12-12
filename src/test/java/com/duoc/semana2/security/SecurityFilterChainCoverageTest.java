package com.duoc.semana2.security;

import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.SecurityFilterChain;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SecurityFilterChainCoverageTest {  // ← Nombre correcto aquí

    @Test
    void testFilterChainCoversAll() throws Exception {
        // Arrange
        SecurityConfig config = new SecurityConfig();

        // HttpSecurity totalmente mockeado (deep stubs para el fluent API)
        HttpSecurity http = mock(HttpSecurity.class, RETURNS_DEEP_STUBS);

        JwtAuthenticationFilter jwtFilter = mock(JwtAuthenticationFilter.class);
        DaoAuthenticationProvider authProvider = mock(DaoAuthenticationProvider.class);

        // Lo que devolverá http.build()
        DefaultSecurityFilterChain fakeChain = mock(DefaultSecurityFilterChain.class);
        when(http.build()).thenReturn(fakeChain);

        // Act: llamamos al método REAL que queremos cubrir
        SecurityFilterChain result = config.filterChain(http, jwtFilter, authProvider);

        // Assert básicos
        assertNotNull(result);
        assertEquals(fakeChain, result);

        // Verificamos SOLO lo que Mockito ve seguro en el mock raíz
        verify(http, atLeastOnce()).csrf(any());
        verify(http).build();

        // No verificamos authorizeHttpRequests / sessionManagement / authenticationProvider
        // porque esas invocaciones se hacen sobre otros objetos internos.
    }
}