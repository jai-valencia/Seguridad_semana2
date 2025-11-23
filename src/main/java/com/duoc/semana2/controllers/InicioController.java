package com.duoc.semana2.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;

import com.duoc.semana2.model.Receta;
import com.duoc.semana2.service.RecetaService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class InicioController {

    private final RecetaService recetaService;
    
    @GetMapping("/home")
    public String mostrarInicio(Model model) {
        // Obtener las 3 recetas más populares (puedes definir la lógica)
        List<Receta> recetasPopulares = recetaService.obtenerRecetasPopulares(3);
        
        // Obtener las 3 recetas más recientes
        List<Receta> recetasRecientes = recetaService.obtenerRecetasRecientes(3);
        
        model.addAttribute("recetasPopulares", recetasPopulares);
        model.addAttribute("recetasRecientes", recetasRecientes);
        
        return "inicio";
    }
    
    @GetMapping("/inicio")
    public String redirectToInicio() {
        return "redirect:/inicio";
    }
}