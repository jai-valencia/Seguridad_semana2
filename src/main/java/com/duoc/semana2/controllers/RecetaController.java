package com.duoc.semana2.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.duoc.semana2.model.Receta;
import com.duoc.semana2.service.RecetaService;

import lombok.RequiredArgsConstructor;

import org.springframework.ui.Model;


@Controller
@RequestMapping("/receta")
public class RecetaController {
    
    @Autowired
    private RecetaService recetaService;

    public RecetaController(RecetaService recetaService) {
            this.recetaService = recetaService;
        }

    
    
    @GetMapping("/detalle/{id}")
    public String verDetalle(@PathVariable Long id, Model model) {
        Receta receta = recetaService.obtenerPorId(id);
        
        if (receta == null) {
            return "redirect:/buscar";
        }
        
        model.addAttribute("receta", receta);
        return "detalle-receta";
    }
}
