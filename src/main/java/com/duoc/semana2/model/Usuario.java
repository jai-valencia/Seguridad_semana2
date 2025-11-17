package com.duoc.semana2.model;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "usuarios")
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
    
    private String rol; // "USER" o "ADMIN"
    
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
    private List<Receta> recetas;
    
    @ManyToMany
    @JoinTable(
        name = "favoritos",
        joinColumns = @JoinColumn(name = "usuario_id"),
        inverseJoinColumns = @JoinColumn(name = "receta_id")
    )
    private List<Receta> recetasFavoritas;

    public Object getId() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getId'");
    }

    public String getUsername() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getUsername'");
    }

    public String getPassword() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getPassword'");
    }

    public GrantedAuthority getRole() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getRole'");
    }

    public void setPassword(String encode) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setPassword'");
    }

    public Object getBiografia() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getBiografia'");
    }

    public void setBiografia(Object biografia2) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setBiografia'");
    }

    public Object getEmail() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getEmail'");
    }

    public void setEmail(Object email2) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setEmail'");
    }

    public Object getNombre() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getNombre'");
    }

    public void setNombre(Object nombre2) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setNombre'");
    }
    
    
}

