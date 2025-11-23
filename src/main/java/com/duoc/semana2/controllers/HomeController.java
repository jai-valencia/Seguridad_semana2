package com.duoc.semana2.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import ch.qos.logback.core.model.Model;

@Controller
public class HomeController {

  @GetMapping("/")
  public String index() {
    return "login"; 
  }

  @GetMapping("/admin")
  public String admin() {
    return "admin"; 
  }

  @GetMapping("/user")
  public String user() {
    return "user"; 
  }

  @GetMapping("/login")
    public String loginPage() {
        return "login"; 
    }
  
    @GetMapping("/inicio")
    public String inicio() {
        return "inicio"; 
    }

    @GetMapping("/busqueda")
    public String buscar() {
        return "buscar"; 
    }

    @GetMapping("/receta/detalle/{id}")
public String receta(@PathVariable Long id, Model model) {

    return "detalle-receta";
}
}
