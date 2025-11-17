package com.duoc.semana2.auth;

import com.duoc.semana2.security.JwtService;
import com.duoc.semana2.repository.UsuarioRepository;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

  private final AuthenticationManager authManager;
  private final JwtService jwtService;
  private final UsuarioRepository usuarioRepository;
  boolean isSecure = !"dev".equals(System.getProperty("spring.profiles.active"));

  public AuthController(AuthenticationManager authManager, JwtService jwtService, UsuarioRepository usuarioRepository) {
    this.authManager = authManager;
    this.jwtService = jwtService;
    this.usuarioRepository = usuarioRepository;
  }

  @PostMapping("/login")
  public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest req) {
    Authentication auth = authManager.authenticate(
        new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword())
    );

    UserDetails user = (UserDetails) auth.getPrincipal();
    String role = user.getAuthorities().stream().findFirst().map(a -> a.getAuthority()).orElse("ROLE_USER");
    String token = jwtService.generateToken(user.getUsername(), Map.of("role", role));

    
    ResponseCookie cookie = ResponseCookie.from("JWT", token)
        .httpOnly(true)
        .secure(isSecure)         // Será TRUE cuando pase a producción con HTTPS
        .sameSite("Lax")        
        .path("/")
        .maxAge(60L * 60L * 2L) 
        .build();

    
    return ResponseEntity.ok()
        .header(HttpHeaders.SET_COOKIE, cookie.toString())
        .body(new AuthResponse(token, role));
  }

  @PostMapping("/logout")
  public ResponseEntity<Void> logout() {
    ResponseCookie clear = ResponseCookie.from("JWT", "")
        .httpOnly(true).secure(false).sameSite("Lax").path("/")
        .maxAge(0)
        .build();
    return ResponseEntity.ok()
        .header(HttpHeaders.SET_COOKIE, clear.toString())
        .build();
  }
}

@Getter @Setter
class AuthRequest { 
  private String username; 
  private String password; 
}

@Getter @AllArgsConstructor
class AuthResponse { private String accessToken; private String role; }
