package com.duoc.semana2.security;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.SessionManagementConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.SecurityFilterChain;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class SecurityConfigTest {

    @SuppressWarnings("unchecked")
    @Test
    void testFilterChainExecutesAllCode() throws Exception {
        // Arrange
        SecurityConfig config = new SecurityConfig();
        HttpSecurity http = mock(HttpSecurity.class, RETURNS_DEEP_STUBS);
        JwtAuthenticationFilter jwtFilter = mock(JwtAuthenticationFilter.class);
        DaoAuthenticationProvider authProvider = mock(DaoAuthenticationProvider.class);

        // Preparar captores para los lambdas
        ArgumentCaptor<Customizer<CsrfConfigurer<HttpSecurity>>> csrfCaptor = 
            ArgumentCaptor.forClass(Customizer.class);
        ArgumentCaptor<Customizer<SessionManagementConfigurer<HttpSecurity>>> sessionCaptor = 
            ArgumentCaptor.forClass(Customizer.class);
        ArgumentCaptor<Customizer<AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry>> authCaptor = 
            ArgumentCaptor.forClass(Customizer.class);

        // Configurar el comportamiento del mock
        when(http.csrf(csrfCaptor.capture())).thenReturn(http);
        when(http.sessionManagement(sessionCaptor.capture())).thenReturn(http);
        when(http.authorizeHttpRequests(authCaptor.capture())).thenReturn(http);
        when(http.authenticationProvider(any())).thenReturn(http);
        when(http.addFilterBefore(any(), any())).thenReturn(http);
        
        DefaultSecurityFilterChain fakeChain = mock(DefaultSecurityFilterChain.class);
        when(http.build()).thenReturn(fakeChain);

        // Act - ejecutar el método que queremos testear
        SecurityFilterChain result = config.filterChain(http, jwtFilter, authProvider);

        // Assert básicos
        assertNotNull(result);
        assertSame(fakeChain, result);

        // IMPORTANTE: Ahora ejecutamos manualmente cada lambda capturado
        
        // 1. Ejecutar lambda de CSRF (líneas 51-53)
        CsrfConfigurer<HttpSecurity> csrfConfigurer = mock(CsrfConfigurer.class, RETURNS_DEEP_STUBS);
        when(csrfConfigurer.ignoringRequestMatchers(anyString())).thenReturn(csrfConfigurer);
        csrfCaptor.getValue().customize(csrfConfigurer);
        verify(csrfConfigurer).ignoringRequestMatchers(eq("/api/**"));

        // 2. Ejecutar lambda de SessionManagement (línea 55)
        SessionManagementConfigurer<HttpSecurity> sessionConfigurer = 
            mock(SessionManagementConfigurer.class, RETURNS_DEEP_STUBS);
        when(sessionConfigurer.sessionCreationPolicy(any(SessionCreationPolicy.class))).thenReturn(sessionConfigurer);
        sessionCaptor.getValue().customize(sessionConfigurer);
        verify(sessionConfigurer).sessionCreationPolicy(eq(SessionCreationPolicy.STATELESS));

        // 3. Ejecutar lambda de AuthorizeHttpRequests (líneas 56-67)
        AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry authRegistry = 
            mock(AuthorizeHttpRequestsConfigurer.AuthorizationManagerRequestMatcherRegistry.class, RETURNS_DEEP_STUBS);
        
        AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizedUrl authorizedUrl = 
            mock(AuthorizeHttpRequestsConfigurer.AuthorizedUrl.class, RETURNS_DEEP_STUBS);
        
        // Mockear cada llamada a requestMatchers para que devuelva authorizedUrl
        when(authRegistry.requestMatchers(any(String[].class))).thenReturn(authorizedUrl);
        when(authRegistry.anyRequest()).thenReturn(authorizedUrl);
        when(authorizedUrl.permitAll()).thenReturn(authRegistry);
        when(authorizedUrl.hasRole(anyString())).thenReturn(authRegistry);
        when(authorizedUrl.hasAnyRole(anyString(), anyString())).thenReturn(authRegistry);
        when(authorizedUrl.authenticated()).thenReturn(authRegistry);
        
        // Ejecutar el lambda
        authCaptor.getValue().customize(authRegistry);
        
        // Verificar que se ejecutaron todas las configuraciones
        verify(authRegistry, atLeast(1)).requestMatchers(any(String[].class));
        verify(authRegistry, times(1)).anyRequest();
    }

    @Test
    void testAuthenticationProvider() {
        SecurityConfig config = new SecurityConfig();
        UserDetailsService userDetailsService = mock(UserDetailsService.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);

        DaoAuthenticationProvider provider = config.authenticationProvider(userDetailsService, passwordEncoder);

        assertNotNull(provider);
        assertInstanceOf(DaoAuthenticationProvider.class, provider);
    }

    @Test
    void testPasswordEncoder() {
        SecurityConfig config = new SecurityConfig();
        PasswordEncoder encoder = config.passwordEncoder();

        assertNotNull(encoder);
        
        String rawPassword = "testPassword123";
        String encodedPassword = encoder.encode(rawPassword);
        
        assertNotNull(encodedPassword);
        assertNotEquals(rawPassword, encodedPassword);
        assertTrue(encoder.matches(rawPassword, encodedPassword));
        assertFalse(encoder.matches("wrongPassword", encodedPassword));
    }

    @Test
    void testAuthenticationManager() throws Exception {
        SecurityConfig config = new SecurityConfig();
        AuthenticationConfiguration authConfig = mock(AuthenticationConfiguration.class);
        AuthenticationManager mockAuthManager = mock(AuthenticationManager.class);
        
        when(authConfig.getAuthenticationManager()).thenReturn(mockAuthManager);

        AuthenticationManager result = config.authenticationManager(authConfig);

        assertNotNull(result);
        assertSame(mockAuthManager, result);
        verify(authConfig).getAuthenticationManager();
    }

    @Test
    void testJwtAuthenticationFilter() {
        SecurityConfig config = new SecurityConfig();
        JwtService jwtService = mock(JwtService.class);
        UserDetailsService userDetailsService = mock(UserDetailsService.class);

        JwtAuthenticationFilter filter = config.jwtAuthenticationFilter(jwtService, userDetailsService);

        assertNotNull(filter);
        assertInstanceOf(JwtAuthenticationFilter.class, filter);
    }

    @Test
    void testConstructor() {
        SecurityConfig config = new SecurityConfig();
        assertNotNull(config);
    }

    @Test
    void testPasswordEncoderBCrypt() {
        SecurityConfig config = new SecurityConfig();
        PasswordEncoder encoder = config.passwordEncoder();

        String password1 = "password123";
        String password2 = "password456";
        
        String encoded1 = encoder.encode(password1);
        String encoded2 = encoder.encode(password2);
        
        assertNotEquals(encoded1, encoded2);
        assertTrue(encoder.matches(password1, encoded1));
        assertTrue(encoder.matches(password2, encoded2));
    }

    @Test
    void testPasswordEncoderSalt() {
        SecurityConfig config = new SecurityConfig();
        PasswordEncoder encoder = config.passwordEncoder();

        String password = "samePassword";
        String hash1 = encoder.encode(password);
        String hash2 = encoder.encode(password);
        
        assertNotEquals(hash1, hash2);
        assertTrue(encoder.matches(password, hash1));
        assertTrue(encoder.matches(password, hash2));
    }

    @Test
    void testPasswordEncoderInvalid() {
        SecurityConfig config = new SecurityConfig();
        PasswordEncoder encoder = config.passwordEncoder();

        String password = "correctPassword";
        String encoded = encoder.encode(password);
        
        assertFalse(encoder.matches("wrongPassword", encoded));
        assertFalse(encoder.matches("", encoded));
    }
}