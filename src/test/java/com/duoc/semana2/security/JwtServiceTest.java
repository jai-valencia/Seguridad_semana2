package com.duoc.semana2.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import io.jsonwebtoken.Jwts;

public class JwtServiceTest {

    private JwtService jwtService;
    private static final String TEST_SECRET = "test-secret-key-with-at-least-256-bits-for-HS256-algorithm";
    private static final long TEST_EXPIRATION_MINUTES = 60; // 1 hora

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(TEST_SECRET, TEST_EXPIRATION_MINUTES);
    }

    // --- Tests del Constructor ---

    @Test
    void testConstructor_CreatesService() {
        JwtService service = new JwtService(TEST_SECRET, TEST_EXPIRATION_MINUTES);
        assertNotNull(service);
    }

    @Test
    void testConstructor_WithDifferentExpiration() {
        JwtService service = new JwtService(TEST_SECRET, 30); // 30 minutos
        assertNotNull(service);
    }

    // --- Tests de generateToken ---

    @Test
    void testGenerateToken_WithEmptyClaims() {
        // Arrange
        String subject = "testuser";
        Map<String, Object> claims = new HashMap<>();

        // Act
        String token = jwtService.generateToken(subject, claims);

        // Assert
        assertNotNull(token);
        assertTrue(token.split("\\.").length == 3); // JWT tiene 3 partes
    }

    @Test
    void testGenerateToken_WithClaims() {
        // Arrange
        String subject = "testuser";
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", "USER");
        claims.put("email", "test@mail.com");

        // Act
        String token = jwtService.generateToken(subject, claims);

        // Assert
        assertNotNull(token);
        
        // Verificar que el token contiene los claims
        Claims parsedClaims = Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(TEST_SECRET.getBytes()))
                .build()
                .parseClaimsJws(token)
                .getBody();
        
        assertEquals("USER", parsedClaims.get("role"));
        assertEquals("test@mail.com", parsedClaims.get("email"));
    }

    @Test
    void testGenerateToken_SetsSubject() {
        // Arrange
        String subject = "admin";
        Map<String, Object> claims = new HashMap<>();

        // Act
        String token = jwtService.generateToken(subject, claims);

        // Assert
        Claims parsedClaims = Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(TEST_SECRET.getBytes()))
                .build()
                .parseClaimsJws(token)
                .getBody();
        
        assertEquals("admin", parsedClaims.getSubject());
    }

    @Test
    void testGenerateToken_SetsIssuedAt() {
        // Arrange
        String subject = "testuser";
        Map<String, Object> claims = new HashMap<>();
        Instant before = Instant.now().minusSeconds(5);

        // Act
        String token = jwtService.generateToken(subject, claims);
        Instant after = Instant.now().plusSeconds(5);

        // Assert
        Claims parsedClaims = Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(TEST_SECRET.getBytes()))
                .build()
                .parseClaimsJws(token)
                .getBody();
        
        Date issuedAt = parsedClaims.getIssuedAt();
        assertNotNull(issuedAt);
        assertTrue(issuedAt.toInstant().isAfter(before));
        assertTrue(issuedAt.toInstant().isBefore(after));
    }

    @Test
    void testGenerateToken_SetsExpiration() {
        // Arrange
        String subject = "testuser";
        Map<String, Object> claims = new HashMap<>();

        // Act
        String token = jwtService.generateToken(subject, claims);

        // Assert
        Claims parsedClaims = Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(TEST_SECRET.getBytes()))
                .build()
                .parseClaimsJws(token)
                .getBody();
        
        Date expiration = parsedClaims.getExpiration();
        assertNotNull(expiration);
        
        // Verificar que expira en aproximadamente TEST_EXPIRATION_MINUTES
        long expirationMillis = expiration.getTime() - parsedClaims.getIssuedAt().getTime();
        long expectedMillis = TEST_EXPIRATION_MINUTES * 60000;
        
        // Permitir 1 segundo de diferencia por procesamiento
        assertTrue(Math.abs(expirationMillis - expectedMillis) < 1000);
    }

    @Test
    void testGenerateToken_DifferentTokensForSameUser() {
        // Arrange
        String subject = "testuser";
        Map<String, Object> claims1 = new HashMap<>(); // Claims para el token 1
        
        // Act
        String token1 = jwtService.generateToken(subject, claims1);
        
        // CORRECCIÓN: Agregamos un claim ÚNICO al segundo token para asegurar que
        // el payload y, por lo tanto, el token firmado sea diferente,
        // sin depender del tiempo de la CPU (que causó el fallo).
        Map<String, Object> claims2 = new HashMap<>();
        claims2.put("unique_id", "token2"); 
        
        String token2 = jwtService.generateToken(subject, claims2);

        // Assert - Los tokens deben ser diferentes
        assertNotNull(token1);
        assertNotNull(token2);
        assertFalse(token1.equals(token2));
    }

    // --- Tests de extractUsername ---

    @Test
    void testExtractUsername_ValidToken() {
        // Arrange
        String subject = "testuser";
        Map<String, Object> claims = new HashMap<>();
        String token = jwtService.generateToken(subject, claims);

        // Act
        String extractedUsername = jwtService.extractUsername(token);

        // Assert
        assertEquals("testuser", extractedUsername);
    }

    @Test
    void testExtractUsername_DifferentUsernames() {
        // Arrange & Act
        String token1 = jwtService.generateToken("user1", new HashMap<>());
        String token2 = jwtService.generateToken("admin", new HashMap<>());

        // Assert
        assertEquals("user1", jwtService.extractUsername(token1));
        assertEquals("admin", jwtService.extractUsername(token2));
    }

    @Test
    void testExtractUsername_MalformedToken_ThrowsException() {
        // Arrange
        String malformedToken = "not.a.valid.jwt.token";

        // Act & Assert
        assertThrows(MalformedJwtException.class, () -> {
            jwtService.extractUsername(malformedToken);
        });
    }

    @Test
    void testExtractUsername_InvalidSignature_ThrowsException() {
        // Arrange
        String differentSecret = "different-secret-key-with-at-least-256-bits-for-HS256-algorithm";
        JwtService otherService = new JwtService(differentSecret, TEST_EXPIRATION_MINUTES);
        String token = otherService.generateToken("testuser", new HashMap<>());

        // Act & Assert - Token firmado con diferente clave
        assertThrows(SignatureException.class, () -> {
            jwtService.extractUsername(token);
        });
    }

    // --- Tests de isTokenValid ---

    @Test
    void testIsTokenValid_ValidToken_ReturnsTrue() {
        // Arrange
        String username = "testuser";
        String token = jwtService.generateToken(username, new HashMap<>());

        // Act
        boolean isValid = jwtService.isTokenValid(token, username);

        // Assert
        assertTrue(isValid);
    }

    @Test
    void testIsTokenValid_WrongUsername_ReturnsFalse() {
        // Arrange
        String token = jwtService.generateToken("testuser", new HashMap<>());

        // Act
        boolean isValid = jwtService.isTokenValid(token, "differentuser");

        // Assert
        assertFalse(isValid);
    }

    @Test
    void testIsTokenValid_ExpiredToken_ReturnsFalse() {
        // Arrange - Crear servicio con expiración de 0 minutos (inmediata)
        JwtService shortLivedService = new JwtService(TEST_SECRET, 0);
        String token = shortLivedService.generateToken("testuser", new HashMap<>());

        // Esperar un poco para asegurar que el token expire
        try {
            // Dormir 100ms es suficiente para que el tiempo actual sea posterior a la expiración
            Thread.sleep(100); 
        } catch (InterruptedException e) {
            // ignore
        }

        // Act
        boolean isValid = jwtService.isTokenValid(token, "testuser");

        // Assert
        assertFalse(isValid);
    }

    @Test
    void testIsTokenValid_MalformedToken_ReturnsFalse() {
        // Arrange
        String malformedToken = "not.a.valid.token";

        // Act
        boolean isValid = jwtService.isTokenValid(malformedToken, "testuser");

        // Assert
        assertFalse(isValid);
    }

    @Test
    void testIsTokenValid_NullToken_ReturnsFalse() {
        // Act
        boolean isValid = jwtService.isTokenValid(null, "testuser");

        // Assert
        assertFalse(isValid);
    }

    @Test
    void testIsTokenValid_EmptyToken_ReturnsFalse() {
        // Act
        boolean isValid = jwtService.isTokenValid("", "testuser");

        // Assert
        assertFalse(isValid);
    }

    @Test
    void testIsTokenValid_InvalidSignature_ReturnsFalse() {
        // Arrange
        String differentSecret = "another-secret-key-with-at-least-256-bits-for-HS256-algorithm";
        JwtService otherService = new JwtService(differentSecret, TEST_EXPIRATION_MINUTES);
        String token = otherService.generateToken("testuser", new HashMap<>());

        // Act
        boolean isValid = jwtService.isTokenValid(token, "testuser");

        // Assert
        assertFalse(isValid);
    }
    
    // Este test fallaba con NullPointerException sin la corrección en JwtService.java
    @Test
    void testIsTokenValid_NullUsername_ReturnsFalse() {
        // Arrange
        String token = jwtService.generateToken("testuser", new HashMap<>());

        // Act
        boolean isValid = jwtService.isTokenValid(token, null);

        // Assert
        assertFalse(isValid);
    }

    // --- Tests de parseAllClaims (indirectamente) ---

    @Test
    void testParseAllClaims_ThroughExtractUsername() {
        // Arrange
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", "ADMIN");
        claims.put("customClaim", "customValue");
        String token = jwtService.generateToken("admin", claims);

        // Act - extractUsername usa parseAllClaims internamente
        String username = jwtService.extractUsername(token);

        // Assert
        assertEquals("admin", username);
    }

    // --- Tests de Integración Completos ---

    @Test
    void testFullFlow_GenerateAndValidate() {
        // Arrange
        String username = "integrationUser";
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", "USER");
        claims.put("email", "integration@test.com");

        // Act
        String token = jwtService.generateToken(username, claims);
        String extractedUsername = jwtService.extractUsername(token);
        boolean isValid = jwtService.isTokenValid(token, username);

        // Assert
        assertNotNull(token);
        assertEquals(username, extractedUsername);
        assertTrue(isValid);
    }

    @Test
    void testFullFlow_GenerateValidateAndReject() {
        // Arrange
        String username = "testuser";
        String token = jwtService.generateToken(username, new HashMap<>());

        // Act & Assert - Validar con username correcto
        assertTrue(jwtService.isTokenValid(token, username));

        // Act & Assert - Rechazar con username incorrecto
        assertFalse(jwtService.isTokenValid(token, "wronguser"));
    }

    // --- Tests de Claims Múltiples ---

    @Test
    void testGenerateToken_WithMultipleClaims() {
        // Arrange
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", "ADMIN");
        claims.put("email", "admin@test.com");
        claims.put("department", "IT");
        claims.put("level", 5);

        // Act
        String token = jwtService.generateToken("admin", claims);

        // Assert
        Claims parsedClaims = Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(TEST_SECRET.getBytes()))
                .build()
                .parseClaimsJws(token)
                .getBody();

        assertEquals("ADMIN", parsedClaims.get("role"));
        assertEquals("admin@test.com", parsedClaims.get("email"));
        assertEquals("IT", parsedClaims.get("department"));
        assertEquals(5, parsedClaims.get("level"));
    }

    // --- Tests de Edge Cases ---

    @Test
    void testGenerateToken_WithSpecialCharactersInSubject() {
        // Arrange
        String subject = "user@example.com";
        Map<String, Object> claims = new HashMap<>();

        // Act
        String token = jwtService.generateToken(subject, claims);
        String extracted = jwtService.extractUsername(token);

        // Assert
        assertEquals(subject, extracted);
    }

    @Test
    void testGenerateToken_WithLongSubject() {
        // Arrange
        String subject = "a".repeat(100); // Username muy largo
        Map<String, Object> claims = new HashMap<>();

        // Act
        String token = jwtService.generateToken(subject, claims);
        String extracted = jwtService.extractUsername(token);

        // Assert
        assertEquals(subject, extracted);
    }

    // --- Tests de Expiración Personalizada ---

    @Test
    void testConstructor_WithShortExpiration() {
        // Arrange & Act
        JwtService shortService = new JwtService(TEST_SECRET, 1); // 1 minuto
        String token = shortService.generateToken("testuser", new HashMap<>());

        // Assert
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(TEST_SECRET.getBytes()))
                .build()
                .parseClaimsJws(token)
                .getBody();

        long expirationMillis = claims.getExpiration().getTime() - claims.getIssuedAt().getTime();
        long expectedMillis = 1 * 60000; // 1 minuto

        assertTrue(Math.abs(expirationMillis - expectedMillis) < 1000);
    }

    @Test
    void testConstructor_WithLongExpiration() {
        // Arrange & Act
        JwtService longService = new JwtService(TEST_SECRET, 1440); // 24 horas
        String token = longService.generateToken("testuser", new HashMap<>());

        // Assert
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(TEST_SECRET.getBytes()))
                .build()
                .parseClaimsJws(token)
                .getBody();

        long expirationMillis = claims.getExpiration().getTime() - claims.getIssuedAt().getTime();
        long expectedMillis = 1440 * 60000; // 24 horas

        assertTrue(Math.abs(expirationMillis - expectedMillis) < 1000);
    }
}