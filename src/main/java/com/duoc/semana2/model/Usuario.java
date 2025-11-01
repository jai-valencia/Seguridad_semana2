package com.duoc.semana2.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "usuarios")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Usuario {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "username", nullable = false, unique = true, length = 120)
  private String username;

  @Column(name = "password", nullable = false, length = 200)
  private String password; 

  @Column(name = "role", nullable = false, length = 40)
  private String role;
}

