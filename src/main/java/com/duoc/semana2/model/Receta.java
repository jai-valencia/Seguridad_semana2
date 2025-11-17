package com.duoc.semana2.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.JoinColumns;

import io.jsonwebtoken.lang.Arrays;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
    
    private Integer tiempoPreparacion; // minutos
    private Integer tiempoCoccion; // minutos
    private Integer porciones;
    private Integer calorias;
    
    @Column(length = 2000)
    private String ingredientes; // separados por comas o saltos de línea
    
    @Column(length = 5000)
    private String instrucciones; // separadas por ||
    
    @ElementCollection
    @CollectionTable(name = "receta_fotos", joinColumns = JoinColumns(nombre = "receta_id"))
    @Column(name = "emoji_foto")
    private List<String> fotosEmojis; // lista de emojis para fotos
    
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
}    

