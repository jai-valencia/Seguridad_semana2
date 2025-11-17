package com.duoc.semana2.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
@Entity
@Table(name = "recetas")
public class Receta {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String nombre;
    private String tipoCocina;
    private String pais;
    private String dificultad;
    private String emoji;
    
    private Integer tiempoPreparacion;
    private Integer tiempoCoccion;
    private Integer porciones;
    private Integer calorias;
    private Integer vistas = 0;
    
    private Boolean publica = true; // Nueva propiedad
    
    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;
    
    @Column(length = 2000)
    private String ingredientes;
    
    @Column(length = 5000)
    private String instrucciones;
    
    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario; // Nueva relación
    
    @ElementCollection
    @CollectionTable(name = "receta_fotos", joinColumns = @JoinColumn(name = "receta_id"))
    @Column(name = "emoji_foto")
    private List<String> fotosEmojis;
    
    @ManyToMany(mappedBy = "recetasFavoritas")
    private List<Usuario> usuariosQueFavoritearon;
    
    // Getters y Setters
    
    public Integer getTiempoTotal() {
        return (tiempoPreparacion != null ? tiempoPreparacion : 0) + 
               (tiempoCoccion != null ? tiempoCoccion : 0);
    }
    
    public List<String> getIngredientesList() {
        if (ingredientes == null || ingredientes.isEmpty()) {
            return new ArrayList<>();
        }
        return Arrays.asList(ingredientes.split("\n"));
    }
    
    public List<String> getInstruccionesList() {
        if (instrucciones == null || instrucciones.isEmpty()) {
            return new ArrayList<>();
        }
        return Arrays.asList(instrucciones.split("\\|\\|"));
    }

    public Object getUsuario() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getUsuario'");
    }

    public void setFechaCreacion(LocalDateTime now) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setFechaCreacion'");
    }

    public void setUsuario(Usuario usuario2) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setUsuario'");
    }
    
}