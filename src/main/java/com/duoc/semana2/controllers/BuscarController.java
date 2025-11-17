
package com.duoc.semana2.controllers;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.duoc.semana2.model.Receta;
import com.duoc.semana2.service.RecetaService;

@Controller
@RequestMapping("/buscar")
public class BuscarController {
    
    @Autowired
    private RecetaService recetaService;
    
    @GetMapping
    public String mostrarBusqueda(Model model) {
        // Cargar todas las recetas por defecto
        List<Receta> recetas = recetaService.obtenerTodasLasRecetas();
        model.addAttribute("recetas", recetas);
        return "buscar";
    }
    
    @GetMapping("/filtrar")
    public String buscarRecetas(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String tipoCocina,
            @RequestParam(required = false) String ingredientes,
            @RequestParam(required = false) String pais,
            @RequestParam(required = false) String dificultad,
            Model model) {
        
        List<Receta> recetas = recetaService.buscarRecetas(
            nombre, tipoCocina, ingredientes, pais, dificultad
        );
        
        model.addAttribute("recetas", recetas);
        model.addAttribute("nombre", nombre);
        model.addAttribute("tipoCocina", tipoCocina);
        model.addAttribute("ingredientes", ingredientes);
        model.addAttribute("pais", pais);
        model.addAttribute("dificultad", dificultad);
        
        return "buscar";
    }
}