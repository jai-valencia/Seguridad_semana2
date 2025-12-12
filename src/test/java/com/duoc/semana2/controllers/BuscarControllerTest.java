package com.duoc.semana2.controllers;

import com.duoc.semana2.Semana2Application;
import com.duoc.semana2.controllers.BuscarController;
import com.duoc.semana2.model.Receta;
import com.duoc.semana2.service.RecetaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser; 
import org.springframework.test.context.ContextConfiguration;


import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(BuscarController.class) 
@ContextConfiguration(classes = Semana2Application.class) 
@WithMockUser
class BuscarControllerTest {

    @Autowired
    private MockMvc mockMvc; // Objeto para simular peticiones HTTP

    // Simula el RecetaService, evitando que se ejecute la lógica real
    @MockBean
    private RecetaService recetaService;

    private Receta receta1;
    private Receta receta2;
    private List<Receta> recetasBase;

    @BeforeEach
    void setUp() {
        // Inicialización de datos de prueba (no necesitan persistir, solo existir)
        receta1 = Receta.builder().id(1L).nombre("Pastel de Choclo").vistas(5).build();
        receta2 = Receta.builder().id(2L).nombre("Empanadas").vistas(10).build();
        recetasBase = Arrays.asList(receta1, receta2);
    }

    // --- Test para GET /buscar (Mostrar Busqueda Inicial) ---

    @Test
    @DisplayName("GET /buscar debe cargar todas las recetas y retornar la vista 'buscar'")
    void testMostrarBusqueda() throws Exception {
        // Configuración del Mock: Cuando se llame a obtenerTodasLasRecetas, retorna recetasBase
        when(recetaService.obtenerTodasLasRecetas()).thenReturn(recetasBase);

        // Simulación de la petición
        mockMvc.perform(get("/buscar"))
                .andExpect(status().isOk()) // Espera un status 200 OK
                .andExpect(view().name("buscar")) // Espera que retorne la vista "buscar"
                .andExpect(model().attributeExists("recetas")) // Verifica que el modelo contenga el atributo
                .andExpect(model().attribute("recetas", recetasBase)); // Verifica que el atributo sea la lista

        // Verifica que el método del servicio fue llamado una vez
        verify(recetaService, times(1)).obtenerTodasLasRecetas();
    }

    // --- Test para GET /buscar/filtrar (Búsqueda con Filtros) ---

    @Test
    @DisplayName("GET /buscar/filtrar sin parámetros debe llamar a buscarRecetas con nulos")
    void testBuscarRecetas_SinFiltros() throws Exception {
        // Configuración del Mock
        when(recetaService.buscarRecetas(any(), any(), any(), any(), any())).thenReturn(recetasBase);

        // Simulación de la petición sin parámetros
        mockMvc.perform(get("/buscar/filtrar"))
                .andExpect(status().isOk())
                .andExpect(view().name("buscar"))
                .andExpect(model().attribute("recetas", recetasBase));

        // Verifica que se llamó al servicio con *todos* los parámetros nulos (null)
        verify(recetaService, times(1)).buscarRecetas(
                isNull(), isNull(), isNull(), isNull(), isNull()
        );
    }
    
    @Test
    @DisplayName("GET /buscar/filtrar con parámetro 'nombre' debe retornar resultados filtrados")
    void testBuscarRecetas_ConFiltroNombre() throws Exception {
        String nombreBuscado = "Pastel";
        List<Receta> resultado = Collections.singletonList(receta1);
        
        // Configuración del Mock: Si se busca por nombre, retorna solo receta1
        when(recetaService.buscarRecetas(eq(nombreBuscado), any(), any(), any(), any())).thenReturn(resultado);

        // Simulación de la petición con el parámetro 'nombre'
        mockMvc.perform(get("/buscar/filtrar")
                .param("nombre", nombreBuscado))
                .andExpect(status().isOk())
                .andExpect(view().name("buscar"))
                .andExpect(model().attribute("recetas", resultado))
                .andExpect(model().attribute("nombre", nombreBuscado)); // Verifica que el filtro se añadió al modelo

        // Verifica la llamada exacta al servicio
        verify(recetaService, times(1)).buscarRecetas(
                eq(nombreBuscado), isNull(), isNull(), isNull(), isNull()
        );
    }
    
    @Test
    @DisplayName("GET /buscar/filtrar con múltiples parámetros debe llamar al servicio correctamente")
    void testBuscarRecetas_ConMultiplesFiltros() throws Exception {
        String tipoCocina = "Chilena";
        String dificultad = "Fácil";
        
        when(recetaService.buscarRecetas(any(), any(), any(), any(), any())).thenReturn(recetasBase);

        // Simulación de la petición
        mockMvc.perform(get("/buscar/filtrar")
                .param("tipoCocina", tipoCocina)
                .param("dificultad", dificultad))
                .andExpect(status().isOk())
                .andExpect(model().attribute("tipoCocina", tipoCocina))
                .andExpect(model().attribute("dificultad", dificultad));

        // Verifica que el servicio fue llamado con los parámetros correctos
        verify(recetaService, times(1)).buscarRecetas(
                isNull(), eq(tipoCocina), isNull(), isNull(), eq(dificultad)
        );
    }
}