package com.duoc.semana2.auth;

import com.duoc.semana2.security.JwtService;
import com.duoc.semana2.repository.UsuarioRepository;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {

  private static final long TOKEN_MAX_AGE_SECONDS = 7200L; // 2 horas
  private static final String JWT_COOKIE_NAME = "JWT";
  
  private final AuthenticationManager authManager;
  private final JwtService jwtService;
  private final UsuarioRepository usuarioRepository;
  private final boolean isSecure;

  public AuthController(
      AuthenticationManager authManager, 
      JwtService jwtService, 
      UsuarioRepository usuarioRepository,
      @Value("${spring.profiles.active:prod}") String activeProfile) {
    this.authManager = authManager;
    this.jwtService = jwtService;
    this.usuarioRepository = usuarioRepository;
    this.isSecure = !"dev".equals(activeProfile);
  }

  @PostMapping("/login")
  public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest req) {
    try {
      Authentication auth = authManager.authenticate(
          new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword())
      );

      UserDetails user = (UserDetails) auth.getPrincipal();
      String role = user.getAuthorities().stream()
          .findFirst()
          .map(a -> a.getAuthority())
          .orElse("ROLE_USER");
      
      String token = jwtService.generateToken(user.getUsername(), Map.of("role", role));

      ResponseCookie cookie = ResponseCookie.from(JWT_COOKIE_NAME, token)
          .httpOnly(true)
          .secure(isSecure)         
          .sameSite("Strict")  
          .path("/")
          .maxAge(TOKEN_MAX_AGE_SECONDS) 
          .build();

      log.info("Usuario autenticado exitosamente: {}", user.getUsername());
      
      return ResponseEntity.ok()
          .header(HttpHeaders.SET_COOKIE, cookie.toString())
          .body(new AuthResponse(token, role));
          
    } catch (BadCredentialsException e) {
      log.warn("Intento de login fallido para usuario: {}", req.getUsername());
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
  }

  @PostMapping("/logout")
  public ResponseEntity<Void> logout() {
    ResponseCookie clear = ResponseCookie.from(JWT_COOKIE_NAME, "")
        .httpOnly(true)
        .secure(isSecure) 
        .sameSite("Strict")
        .path("/")
        .maxAge(0)
        .build();
    
    log.info("Usuario deslogueado exitosamente");
    
    return ResponseEntity.ok()
        .header(HttpHeaders.SET_COOKIE, clear.toString())
        .build();
  }
}

@Getter 
@Setter
class AuthRequest { 
  @NotBlank(message = "El username no puede estar vacío")
  private String username; 
  
  @NotBlank(message = "El password no puede estar vacío")
  private String password; 
}

@Getter 
@AllArgsConstructor
class AuthResponse { 
  private String accessToken; 
  private String role; 
}