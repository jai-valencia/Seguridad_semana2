package com.duoc.semana2.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "usuarios")
@Data                // Lombok: genera getters, setters, toString, equals, hashcode
@NoArgsConstructor   // Constructor vacío requerido por JPA
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    private String nombre;

    @Column(unique = true)
    private String email;

    @Column(length = 500)
    private String biografia;

    @Column(name = "fecha_registro")
    private LocalDateTime fechaRegistro;

    private String rol; // USER o ADMIN

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
    private List<Receta> recetas = new ArrayList<>();

    @ManyToMany
    @JoinTable(
        name = "favoritos",
        joinColumns = @JoinColumn(name = "usuario_id"),
        inverseJoinColumns = @JoinColumn(name = "receta_id")
    )
    private List<Receta> recetasFavoritas = new ArrayList<>();
}
