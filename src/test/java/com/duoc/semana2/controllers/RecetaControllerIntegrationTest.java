package com.duoc.semana2.controllers;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.duoc.semana2.model.Receta;
import com.duoc.semana2.service.RecetaService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Test de integración para RecetaController.
 */
@SpringBootTest
@AutoConfigureMockMvc
class RecetaControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RecetaService recetaService;

    // ==========================================================
    // Caso 1: Receta encontrada → mostrar detalle
    // ==========================================================
    @Test
    void verDetalle_conRecetaExistente_debeRetornarVistaDetalleReceta() throws Exception {

        Receta recetaMock = mock(Receta.class); // evita errores sin setters

        when(recetaService.obtenerPorId(10L))
                .thenReturn(recetaMock);

        mockMvc.perform(get("/receta/detalle/10"))
                .andExpect(status().isOk())
                .andExpect(view().name("detalle-receta"))
                .andExpect(model().attributeExists("receta"));
    }

    // ==========================================================
    // Caso 2: Receta inexistente → redirigir a /buscar
    // ==========================================================
    @Test
    void verDetalle_conRecetaInexistente_debeRedirigirABuscar() throws Exception {

        when(recetaService.obtenerPorId(99L))
                .thenReturn(null);

        mockMvc.perform(get("/receta/detalle/99"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/buscar"));
    }
}
