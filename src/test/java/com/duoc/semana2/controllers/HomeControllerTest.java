package com.duoc.semana2.controllers;

import com.duoc.semana2.controllers.HomeController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
// No necesitamos SecurityMockMvcRequestPostProcessors.unauthenticated() aquí

/**
 * Pruebas unitarias para HomeController usando standaloneSetup para forzar el ViewResolver.
 * Esto evita los errores de Circular View Path y No ModelAndView Found.
 */
@ExtendWith(MockitoExtension.class) // Usamos Mockito para el contexto
class HomeControllerTest {

    private MockMvc mockMvc;

    @InjectMocks
    private HomeController homeController;

    @BeforeEach
    void setup() {
        // Configuramos un ViewResolver simple para evitar el Circular View Path
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/WEB-INF/views/"); // Ficticio
        viewResolver.setSuffix(".jsp"); // Ficticio

        this.mockMvc = MockMvcBuilders
                .standaloneSetup(homeController) // Instancia directa del controlador
                .setViewResolvers(viewResolver)   // Forzamos el ViewResolver de prueba
                .build();
    }

    
    
    @Test
    @DisplayName("GET / debe retornar la vista de login")
    void testIndex() throws Exception {
        mockMvc.perform(get("/")) 
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }

    @Test
    @DisplayName("GET /login debe retornar la vista de login")
    void testLogin() throws Exception {
        mockMvc.perform(get("/login")) 
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }
    
    @Test
    @DisplayName("GET /inicio debe retornar la vista de inicio-view")
    void testInicio() throws Exception {
        mockMvc.perform(get("/inicio"))
                .andExpect(status().isOk())
                .andExpect(view().name("inicio-view"));
    }

    @Test
    @DisplayName("GET /busqueda debe retornar la vista de buscar")
    void testBusqueda() throws Exception {
        mockMvc.perform(get("/busqueda"))
                .andExpect(status().isOk())
                .andExpect(view().name("buscar"));
    }
    
    // --- Rutas Protegidas (NOTA: Las pruebas de seguridad no funcionan en standaloneSetup) ---
    // Si necesitas probar la seguridad de los roles, necesitarás un test separado con @WebMvcTest.
    // Aquí solo probamos que el controlador devuelve el nombre correcto, asumiendo que el filtro
    // de seguridad está deshabilitado.

    @Test
    @DisplayName("GET /admin debe retornar la vista de admin-view")
    void testAdmin() throws Exception {
        mockMvc.perform(get("/admin"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin-view"));
    }

    @Test
    @DisplayName("GET /user debe retornar la vista de user")
    void testUser() throws Exception {
        mockMvc.perform(get("/user"))
                .andExpect(status().isOk())
                .andExpect(view().name("user"));
    }

    @Test
    @DisplayName("GET /receta/detalle/{id} debe retornar la vista de detalle-receta")
    void testDetalle() throws Exception {
        long idReceta = 42L;
        
        mockMvc.perform(get("/receta/detalle/{id}", idReceta))
                .andExpect(status().isOk())
                .andExpect(view().name("detalle-receta"));
    }
}