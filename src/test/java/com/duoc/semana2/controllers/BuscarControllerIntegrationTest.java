package com.duoc.semana2.controllers;

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
import static org.mockito.Mockito.when;


/**
 * Test de integración del BuscarController.
 * Este test no depende de atributos de la clase Receta,
 * por lo tanto es compatible con clases sin setters o records.
 */
@SpringBootTest
@AutoConfigureMockMvc
class BuscarControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RecetaService recetaService;

    // ===========================================================
    // TEST: GET /buscar (carga inicial)
    // ===========================================================
    @Test
    void mostrarBusqueda_debeRetornarVistaBuscar() throws Exception {

        // Evita errores: no se crean objetos Receta
        when(recetaService.obtenerTodasLasRecetas())
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/buscar"))
                .andExpect(status().isOk())
                .andExpect(view().name("buscar"))
                .andExpect(model().attributeExists("recetas"));
    }

    // ===========================================================
    // TEST: GET /buscar/filtrar
    // ===========================================================
    @Test
    void buscarRecetas_debeRetornarVistaBuscarConParametros() throws Exception {

        // Evita problemas con Receta: retornamos lista vacía
        when(recetaService.buscarRecetas(
                "Test", "Chilena", "Tomate", "Chile", "Media"))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/buscar/filtrar")
                .param("nombre", "Test")
                .param("tipoCocina", "Chilena")
                .param("ingredientes", "Tomate")
                .param("pais", "Chile")
                .param("dificultad", "Media"))
                .andExpect(status().isOk())
                .andExpect(view().name("buscar"))
                .andExpect(model().attributeExists("recetas"))
                .andExpect(model().attributeExists("nombre"))
                .andExpect(model().attributeExists("tipoCocina"))
                .andExpect(model().attributeExists("ingredientes"))
                .andExpect(model().attributeExists("pais"))
                .andExpect(model().attributeExists("dificultad"));
    }
}
