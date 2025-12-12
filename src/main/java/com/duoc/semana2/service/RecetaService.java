package com.duoc.semana2.service;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.duoc.semana2.model.Receta;
import com.duoc.semana2.repository.RecetaRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RecetaService {
    
    private final RecetaRepository recetaRepository;

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

    public List<Receta> obtenerRecetasPopulares(int limite) {
        return recetaRepository.findTopRecetas(PageRequest.of(0, limite));
    }
    
    public List<Receta> obtenerRecetasRecientes(int limite) {
        return recetaRepository.findRecentRecetas(PageRequest.of(0, limite));
    }

    public List<Receta> obtenerRecetasPorUsuario(Long usuarioId) {
        return recetaRepository.findByUsuarioId(usuarioId);
    }
    
    public List<Receta> obtenerRecetasFavoritas(Long usuarioId) {
        return recetaRepository.findFavoritasByUsuarioId(usuarioId);
    }
    
    public void guardarReceta(Receta receta) {
        recetaRepository.save(receta);
    }
    
    public void eliminarReceta(Long id) {
        recetaRepository.deleteById(id);
    }
}
