package com.duoc.semana2.controllers;


import com.duoc.semana2.Semana2Application;
import com.duoc.semana2.controllers.RecetaController;
import com.duoc.semana2.model.Receta;
import com.duoc.semana2.service.RecetaService;
import org.springframework.security.test.context.support.WithMockUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

// Importaciones estáticas para Mockito y MockMvc
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(
    controllers = RecetaController.class,
    excludeAutoConfiguration = ThymeleafAutoConfiguration.class,
  
    excludeFilters = @ComponentScan.Filter(
        type = FilterType.REGEX,
        pattern = ".*SecurityConfig" 
    )
)

@ContextConfiguration(classes = Semana2Application.class)
@WithMockUser
class RecetaControllerTest {

    @Autowired
    private MockMvc mockMvc;


    @MockBean
    private RecetaService recetaService;
    

    private final Receta recetaExistente = new Receta(
        101L, 
        "Cazuela", "Plato Chileno", "Instrucciones", "Ingredientes", "URL", 
        5, 60, null, null, null, null, true, null, "Tags", "🍲", "Comentarios", null, null, null
    );


    @Test
    @DisplayName("GET /receta/detalle/{id} debe mostrar la vista de detalle si la receta existe")
    void testVerDetalle_RecetaExiste() throws Exception {
        Long idExistente = 101L;
        
        // 1. Simular: Cuando se llame obtenerPorId(101L), devuelve la recetaExistente
        when(recetaService.obtenerPorId(idExistente)).thenReturn(recetaExistente);

        // 2. Ejecutar la petición y verificar
        mockMvc.perform(get("/receta/detalle/{id}", idExistente))
                .andExpect(status().isOk())
                .andExpect(view().name("detalle-receta")) // Verifica la vista de destino
                
                // 3. Verificar el Modelo
                .andExpect(model().attributeExists("receta")) // Verifica que el atributo exista
                .andExpect(model().attribute("receta", recetaExistente)); // Verifica que la receta sea la correcta
    }

    // -------------------------------------------------------------------------
    // TEST 2: Caso de Fallo - La receta no existe (Redirección)
    // -------------------------------------------------------------------------
    @Test
    @DisplayName("GET /receta/detalle/{id} debe redirigir a /buscar si la receta no existe")
    void testVerDetalle_RecetaNoExiste() throws Exception {
        Long idNoExistente = 999L;
        
        // 1. Simular: Cuando se llame obtenerPorId(999L), devuelve null
        when(recetaService.obtenerPorId(idNoExistente)).thenReturn(null);

        // 2. Ejecutar la petición y verificar la redirección
        mockMvc.perform(get("/receta/detalle/{id}", idNoExistente))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/buscar")); 
    }
}