package com.duoc.semana2.controllers;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Collections;

import com.duoc.semana2.service.RecetaService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Test de integración para InicioController.
 */
@SpringBootTest
@AutoConfigureMockMvc
class InicioControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RecetaService recetaService;

    // ==========================================================
    // GET /inicio
    // ==========================================================
    @Test
    void mostrarInicio_debeRetornarVistaInicioConListas() throws Exception {

        // Evitar errores: retornamos listas vacías, no se crean objetos Receta
        when(recetaService.obtenerRecetasPopulares(3))
                .thenReturn(Collections.emptyList());

        when(recetaService.obtenerRecetasRecientes(3))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/inicio"))
                .andExpect(status().isOk())
                .andExpect(view().name("inicio"))
                .andExpect(model().attributeExists("recetasPopulares"))
                .andExpect(model().attributeExists("recetasRecientes"));
    }

    // ==========================================================
    // GET /
    // ==========================================================
    @Test
    void redirectToInicio_debeRedirigirAInicio() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/inicio"));
    }
}
