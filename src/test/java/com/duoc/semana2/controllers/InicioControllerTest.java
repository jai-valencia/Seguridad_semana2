package com.duoc.semana2.controllers;

import com.duoc.semana2.Semana2Application;
import com.duoc.semana2.controllers.InicioController;
import com.duoc.semana2.model.Receta;
import com.duoc.semana2.service.RecetaService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

/**
 * Pruebas unitarias para InicioController.
 * Verifica la manipulación del Modelo y el mapeo de vistas.
 */
@WebMvcTest(
    controllers = InicioController.class,
    excludeAutoConfiguration = ThymeleafAutoConfiguration.class,
    // Excluir la configuración de seguridad para evitar redirecciones no deseadas en el test
    excludeFilters = @ComponentScan.Filter(
        type = FilterType.REGEX,
        pattern = ".*SecurityConfig" // Ajustar si tu clase de seguridad tiene otro nombre
    )
)
@ContextConfiguration(classes = Semana2Application.class)
@WithMockUser(roles = {"USER"}) // Simula un usuario logueado con rol USER
class InicioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // Simular el servicio que el controlador utiliza
    @MockBean
    private RecetaService recetaService;

    private final List<Receta> recetasPopulares = Arrays.asList(
        // Receta 1: Tarta
        new Receta(
            1L,                     // 1. ID (Long)
            "Tarta de Manzana",     // 2. Nombre (String)
            "Descripción corta",    // 3. DescripcionCorta (String)
            "Instrucciones largas", // 4. Instrucciones (String)
            "Ingredientes CSV",     // 5. Ingredientes (String)
            "Imagen URL",           // 6. ImagenUrl (String)
            5,                      // 7. Valoracion (Integer) <-- Usaremos este para la popularidad
            10,                     // 8. TiempoPreparacion (Integer)
            null,                   // 9. Calorias (Integer)
            null,                   // 10. Dificultad (Integer)
            null,                   // 11. Porciones (Integer)
            null,                   // 12. Categoria (Integer)
            false,                  // 13. Publicada (Boolean)
            null,                   // 14. FechaCreacion (LocalDateTime)
            "Etiquetas",            // 15. Tags (String)
            "Emoji",                // 16. Emoji (String)
            "Comentarios",          // 17. Comentarios (String)
            null,                   // 18. Usuario (Usuario)
            null,                   // 19. List<String> (lista de ingredientes?)
            null                    // 20. List<Usuario> (lista de likes?)
        ),
        // Receta 2: Sopa
        new Receta(
            2L, "Sopa de Verduras", "Descripción", "Instrucciones", "Ingredientes", "URL", 
            4, 20, null, null, null, null, false, null, "Tags", "🍜", "Comentarios", null, null, null
        )
    );
    
    private final List<Receta> recetasRecientes = Arrays.asList(
        // Receta 3: Ensalada
        new Receta(
            3L, "Ensalada César", "Descripción", "Instrucciones", "Ingredientes", "URL", 
            1, 5, null, null, null, null, true, null, "Tags", "🥗", "Comentarios", null, null, null
        ),
        // Receta 4: Pasta
        new Receta(
            4L, "Pasta Boloñesa", "Descripción", "Instrucciones", "Ingredientes", "URL", 
            1, 30, null, null, null, null, true, null, "Tags", "🍝", "Comentarios", null, null, null
        )
    );

    // -------------------------------------------------------------------------
    // TEST 1: GET /home
    // -------------------------------------------------------------------------
    @Test
    @DisplayName("GET /home debe cargar recetas y retornar la vista 'inicio'")
    void testMostrarInicio() throws Exception {
        // 1. Simular el comportamiento del servicio
        when(recetaService.obtenerRecetasPopulares(3)).thenReturn(recetasPopulares);
        when(recetaService.obtenerRecetasRecientes(3)).thenReturn(recetasRecientes);

        // 2. Ejecutar la petición y verificar
        mockMvc.perform(get("/home"))
                .andExpect(status().isOk())
                .andExpect(view().name("inicio")) // Verifica que retorna el nombre de vista correcto
                
                // 3. Verificar el Modelo
                .andExpect(model().attributeExists("recetasPopulares", "recetasRecientes")) // Verifica que existan los atributos
                .andExpect(model().attribute("recetasPopulares", recetasPopulares)) // Verifica el contenido de recetasPopulares
                .andExpect(model().attribute("recetasRecientes", recetasRecientes)); // Verifica el contenido de recetasRecientes
    }

    // -------------------------------------------------------------------------
    // TEST 2: GET /inicio (Redirección)
    // -------------------------------------------------------------------------
    @Test
    @DisplayName("GET /inicio debe redirigir a /inicio")
    void testRedirectToInicio() throws Exception {
        mockMvc.perform(get("/inicio"))
                .andExpect(status().is3xxRedirection()) // Verifica que es una redirección
                .andExpect(redirectedUrl("/inicio")); // Verifica el destino de la redirección
    }
}