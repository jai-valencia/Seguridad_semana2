package com.duoc.semana2.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Test de integración para HomeController.
 * Valida que cada endpoint retorne la vista correspondiente.
 */
@SpringBootTest
@AutoConfigureMockMvc
class HomeControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    // ======================================
    // GET /
    // ======================================
    @Test
    void index_debeRetornarVistaLogin() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }

    // ======================================
    // GET /admin
    // ======================================
    @Test
    void admin_debeRetornarVistaAdmin() throws Exception {
        mockMvc.perform(get("/admin"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin"));
    }

    // ======================================
    // GET /user
    // ======================================
    @Test
    void user_debeRetornarVistaUser() throws Exception {
        mockMvc.perform(get("/user"))
                .andExpect(status().isOk())
                .andExpect(view().name("user"));
    }

    // ======================================
    // GET /login
    // ======================================
    @Test
    void loginPage_debeRetornarVistaLogin() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }

    // ======================================
    // GET /inicio
    // ======================================
    @Test
    void inicio_debeRetornarVistaInicio() throws Exception {
        mockMvc.perform(get("/inicio"))
                .andExpect(status().isOk())
                .andExpect(view().name("inicio"));
    }

    // ======================================
    // GET /buscar
    // ======================================
    @Test
    void buscar_debeRetornarVistaBuscar() throws Exception {
        mockMvc.perform(get("/buscar"))
                .andExpect(status().isOk())
                .andExpect(view().name("buscar"));
    }

    // ======================================
    // GET /receta/detalle/{id}
    // ======================================
    @Test
    void recetaDetalle_debeRetornarVistaDetalleReceta() throws Exception {
        mockMvc.perform(get("/receta/detalle/10"))
                .andExpect(status().isOk())
                .andExpect(view().name("detalle-receta"));
    }
}
