package com.duoc.semana2.auth;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.duoc.semana2.repository.UsuarioRepository;
import com.duoc.semana2.security.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;



@WebMvcTest(AuthController.class)
@Import({AuthController.class})
@AutoConfigureMockMvc(addFilters = false)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // Mockeamos las dependencias inyectadas en el constructor de AuthController
    @MockBean
    private AuthenticationManager authManager;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UsuarioRepository usuarioRepository;

    private static final String VALID_USERNAME = "testuser";
    private static final String VALID_PASSWORD = "password123";
    private static final String VALID_TOKEN = "jwt.fake.token";
    private static final String USER_ROLE = "ROLE_USER";

    private AuthRequest authRequest;

    @BeforeEach
    void setUp() {
        authRequest = new AuthRequest();
        authRequest.setUsername(VALID_USERNAME);
        authRequest.setPassword(VALID_PASSWORD);
    }
    
    // --- Pruebas del endpoint /auth/login ---

    @Test
    void testLogin_Success() throws Exception {
        // 1. Configuración del Mock de Spring Security
        // Creamos un UserDetails que simula el principal autenticado.
        UserDetails userDetails = new User(
            VALID_USERNAME, 
            VALID_PASSWORD, 
            Collections.singleton(new SimpleGrantedAuthority(USER_ROLE))
        );
        
        // Creamos una autenticación simulada que contiene el UserDetails.
        Authentication successfulAuth = new UsernamePasswordAuthenticationToken(
            userDetails, null, userDetails.getAuthorities()
        );
        
        // Cuando authManager.authenticate es llamado con cualquier token, devuelve la autenticación exitosa.
        when(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenReturn(successfulAuth);

        // 2. Configuración del Mock del JWT
        // Cuando jwtService.generateToken es llamado, devuelve el token falso.
        when(jwtService.generateToken(any(String.class), any(Map.class)))
            .thenReturn(VALID_TOKEN);

        // 3. Ejecución y Verificación
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authRequest)))
            .andExpect(status().isOk())
            
            // Verifica el body de la respuesta (AuthResponse)
            .andExpect(jsonPath("$.accessToken").value(VALID_TOKEN))
            .andExpect(jsonPath("$.role").value(USER_ROLE))
            
            // Verifica la cookie JWT
            .andExpect(header().string(HttpHeaders.SET_COOKIE, org.hamcrest.Matchers.containsString("JWT=" + VALID_TOKEN)))
            .andExpect(cookie().exists("JWT"))
            .andExpect(cookie().value("JWT", VALID_TOKEN))
            .andExpect(cookie().httpOnly("JWT", true))
            .andExpect(cookie().maxAge("JWT", 7200)) // 2 horas
            .andExpect(cookie().sameSite("JWT", "Strict"));
    }

    @Test
    void testLogin_BadCredentials() throws Exception {
        // 1. Configuración del Mock (Simular fallo)
        when(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenThrow(new BadCredentialsException("Credenciales incorrectas"));

        // 2. Ejecución y Verificación
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authRequest)))
            .andExpect(status().isUnauthorized()) // 401
            .andExpect(cookie().doesNotExist("JWT")); // No debería emitir cookie
    }

    @Test
    void testLogin_InvalidRequestBody_UsernameMissing() throws Exception {
        // Username vacío (falla @Valid @NotBlank)
        AuthRequest invalidRequest = new AuthRequest();
        invalidRequest.setPassword(VALID_PASSWORD);

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
            .andExpect(status().isBadRequest()); // 400 Bad Request por fallo de validación
    }

    // --- Pruebas del endpoint /auth/logout ---
    
    @Test
    void testLogout_Success() throws Exception {
        mockMvc.perform(post("/auth/logout"))
            .andExpect(status().isOk())
            
            // Verifica que se envíe una cookie "limpia" para invalidar la sesión
            .andExpect(header().string(HttpHeaders.SET_COOKIE, org.hamcrest.Matchers.containsString("JWT=")))
            .andExpect(cookie().exists("JWT"))
            .andExpect(cookie().value("JWT", ""))
            .andExpect(cookie().maxAge("JWT", 0)) // Max age 0 para borrar la cookie
            .andExpect(cookie().httpOnly("JWT", true))
            .andExpect(cookie().sameSite("JWT", "Strict"));
    }
}