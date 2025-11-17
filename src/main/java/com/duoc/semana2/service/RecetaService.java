package com.duoc.semana2.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.duoc.semana2.model.Receta;
import com.duoc.semana2.repository.RecetaRepository;

@Service
public class RecetaService {
    
    @Autowired
    private RecetaRepository recetaRepository;
    
    public Receta obtenerPorId(Long id) {
        return recetaRepository.findById(id).orElse(null);
    }
    
    public List<Receta> obtenerTodasLasRecetas() {
        return recetaRepository.findAll();
    }
    
    public List<Receta> buscarRecetas(String nombre, String tipoCocina, 
                                      String ingredientes, String pais, 
                                      String dificultad) {
        return recetaRepository.buscarConFiltros(
            nombre, tipoCocina, ingredientes, pais, dificultad
        );
    }
}