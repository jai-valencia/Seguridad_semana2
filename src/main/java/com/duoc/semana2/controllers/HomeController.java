package com.duoc.semana2.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

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

    @GetMapping("/buscar")
    public String buscar() {
        return "buscar"; 
    }

    @GetMapping("/receta/detalle/{id}")
    public String receta() {
        return "detalle-receta"; 
    }
}
