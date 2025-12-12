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
        return "admin-view";
    }

    @GetMapping("/user")
    public String user() {
        return "user";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/inicio")
    public String inicio() {
        return "inicio-view"; // ← FIX para evitar loop en test
    }

    @GetMapping("/busqueda")
    public String busqueda() {
        return "buscar";
    }

    @GetMapping("/receta/detalle/{id}")
    public String detalle() {
        return "detalle-receta";
    }
}
