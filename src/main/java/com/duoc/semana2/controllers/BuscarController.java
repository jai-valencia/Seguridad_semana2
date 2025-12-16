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

import lombok.RequiredArgsConstructor;


@Controller
public class BuscarController {

    private final RecetaService recetaService;

    public BuscarController(RecetaService recetaService) {
        this.recetaService = recetaService;
    }

    @GetMapping("/buscar")
    public String mostrarBusqueda(Model model) {
        model.addAttribute("recetas", recetaService.obtenerTodasLasRecetas());
        return "buscar";
    }

    @GetMapping("/buscar/filtrar")
    public String buscarRecetas(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String tipoCocina,
            @RequestParam(required = false) String ingredientes,
            @RequestParam(required = false) String pais,
            @RequestParam(required = false) String dificultad,
            Model model
    ) {
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

