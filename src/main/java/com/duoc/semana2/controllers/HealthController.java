package com.duoc.semana2.controllers;

import com.duoc.semana2.repository.UsuarioRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/health")
public class HealthController {
  private final UsuarioRepository repo;
  public HealthController(UsuarioRepository repo){ this.repo = repo; }

  @GetMapping("/db")
  public ResponseEntity<String> db() {
    return ResponseEntity.ok("usuarios: " + repo.count());
  }
}
