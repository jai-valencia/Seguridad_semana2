package com.duoc.semana2.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "usuarios")
@Getter 
@Setter 
@ToString 
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    private String password;

    private String email;

    private String nombre;

    private String nombreCompleto;

    private String titulo;

    @Column(length = 2000)
    private String biografia;

    
    @Builder.Default
    private String rol = "USER";

    
    @Builder.Default
    @EqualsAndHashCode.Exclude
    private LocalDateTime fechaRegistro = LocalDateTime.now();

    // Relación con recetas
    @OneToMany(mappedBy = "usuario")
    @Builder.Default 
    @EqualsAndHashCode.Exclude
    private List<Receta> recetas = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "usuarios_favoritos",
            joinColumns = @JoinColumn(name = "usuario_id"),
            inverseJoinColumns = @JoinColumn(name = "receta_id")
    )
    @Builder.Default 
    @EqualsAndHashCode.Exclude
    private List<Receta> recetasFavoritas = new ArrayList<>();
}