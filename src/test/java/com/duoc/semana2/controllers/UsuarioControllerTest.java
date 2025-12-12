package com.duoc.semana2.controllers;

import com.duoc.semana2.model.Receta;
import com.duoc.semana2.model.Usuario;
import com.duoc.semana2.service.RecetaService;
import com.duoc.semana2.service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.duoc.semana2.controllers.UsuarioController;


@WebMvcTest(
    controllers = UsuarioController.class,
    excludeAutoConfiguration = ThymeleafAutoConfiguration.class,
    excludeFilters = @ComponentScan.Filter(
        type = FilterType.REGEX,
        pattern = ".*SecurityConfig"
    )
)
@WithMockUser(username = "testuser", roles = {"USER"}) // Usuario mock para toda la clase
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RecetaService recetaService;

    @MockBean
    private UsuarioService usuarioService;

    // --- Constantes y Mocks ---
    private final String USERNAME = "testuser";
    private final Long USER_ID = 1L;
    
    private Usuario mockUsuario;
    private Receta mockRecetaPropia;
    private Receta mockRecetaAjena;
    private Usuario otroUser; 

    @BeforeEach
    void setup() {
        // Inicializar objetos Mocks
        mockUsuario = new Usuario();
        otroUser = new Usuario();
        
        // Configuración de mockUsuario
        mockUsuario.setId(USER_ID);
        mockUsuario.setUsername(USERNAME);
        mockUsuario.setPassword("password_hash");
        mockUsuario.setNombreCompleto("Test User");
        
        // Configuración de usuario ajeno
        otroUser.setId(2L);
        otroUser.setUsername("otroUser");
        otroUser.setNombreCompleto("Otro User");

        // --- INICIALIZACIÓN DE RECETAS USANDO EL CONSTRUCTOR DE 20 ARGUMENTOS (Descubierto por Maven) ---
        
        // Receta Propia
        mockRecetaPropia = new Receta(
            101L,               // 1. Long (ID)
            "Tarta Propia",     // 2. String (Titulo)
            "Descripción",      // 3. String
            "Instrucciones",    // 4. String
            "Ingredientes",     // 5. String
            "URL Imagen",       // 6. String
            0,                  // 7. Integer
            0,                  // 8. Integer
            0,                  // 9. Integer
            0,                  // 10. Integer
            0,                  // 11. Integer
            0,                  // 12. Integer (Los seis enteros seguidos)
            true,               // 13. Boolean
            LocalDateTime.now(),// 14. LocalDateTime
            null,               // 15. String
            null,               // 16. String
            null,               // 17. String
            mockUsuario,        // 18. Usuario
            null,               // 19. List<String>
            null                // 20. List<Usuario>
        );
        
        // Receta Ajena
        mockRecetaAjena = new Receta(
            102L,               // 1. Long (ID)
            "Sopa Ajena",       // 2. String (Titulo)
            "Descripción",      // 3. String
            "Instrucciones",    // 4. String
            "Ingredientes",     // 5. String
            "URL Imagen",       // 6. String
            0,                  // 7. Integer
            0,                  // 8. Integer
            0,                  // 9. Integer
            0,                  // 10. Integer
            0,                  // 11. Integer
            0,                  // 12. Integer
            true,               // 13. Boolean
            LocalDateTime.now(),// 14. LocalDateTime
            null,               // 15. String
            null,               // 16. String
            null,               // 17. String
            otroUser,           // 18. Usuario (Ajeno)
            null,               // 19. List<String>
            null                // 20. List<Usuario>
        );
        // -------------------------------------------------------------------
        
        when(usuarioService.obtenerPorUsername(USERNAME)).thenReturn(mockUsuario);
    }

    // =========================================================================
    // TEST 1: Dashboard
    // =========================================================================
    @Test
    @DisplayName("GET /usuario/dashboard debe cargar el dashboard con recetas y favoritos")
    void testMostrarDashboard_CargaExitosa() throws Exception {
        List<Receta> misRecetas = List.of(mockRecetaPropia);
        List<Receta> favoritos = Collections.emptyList();

        when(recetaService.obtenerRecetasPorUsuario(USER_ID)).thenReturn(misRecetas);
        when(recetaService.obtenerRecetasFavoritas(USER_ID)).thenReturn(favoritos);

        mockMvc.perform(get("/usuario/dashboard"))
                .andExpect(status().isOk())
                .andExpect(view().name("usuario-dashboard"))
                .andExpect(model().attributeExists("usuario", "misRecetas", "recetasFavoritas"));

        verify(usuarioService, times(1)).obtenerPorUsername(USERNAME);
    }

    // =========================================================================
    // TEST 2: Nueva Receta - Formulario
    // =========================================================================
    @Test
    @DisplayName("GET /usuario/nueva-receta debe mostrar el formulario")
    void testMostrarFormularioNuevaReceta() throws Exception {
        mockMvc.perform(get("/usuario/nueva-receta"))
                .andExpect(status().isOk())
                .andExpect(view().name("formulario-receta"))
                .andExpect(model().attributeExists("receta"));
    }

    // =========================================================================
    // TEST 3: Guardar Receta - Creación exitosa
    // =========================================================================
    @Test
    @DisplayName("POST /usuario/guardar-receta debe guardar la receta y redirigir al dashboard")
    void testGuardarReceta_CreacionExitosa() throws Exception {
        Receta recetaForm = new Receta(); 
        
        recetaForm.setNombre("Nueva Receta Test"); 
       

        mockMvc.perform(post("/usuario/guardar-receta")
                        .flashAttr("receta", recetaForm)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/usuario/dashboard"))
                .andExpect(flash().attributeExists("mensaje"));

        verify(recetaService, times(1)).guardarReceta(any(Receta.class));
    }
    
    // =========================================================================
    // RESTO DE TESTS (4 a 8)
    // =========================================================================

    @Test
    @DisplayName("GET /usuario/editar-receta/{id} debe mostrar formulario si la receta pertenece al usuario")
    void testMostrarFormularioEditar_RecetaPropia() throws Exception {
        Long idReceta = 101L;

        when(recetaService.obtenerPorId(idReceta)).thenReturn(mockRecetaPropia);

        mockMvc.perform(get("/usuario/editar-receta/{id}", idReceta))
                .andExpect(status().isOk())
                .andExpect(view().name("formulario-receta"))
                .andExpect(model().attributeExists("receta"));
    }

    @Test
    @DisplayName("GET /usuario/editar-receta/{id} debe redirigir si la receta es ajena o no existe")
    void testMostrarFormularioEditar_RecetaAjenaONoExiste() throws Exception {
        Long idRecetaAjena = 102L;
        Long idRecetaNoExiste = 999L;

        when(recetaService.obtenerPorId(idRecetaAjena)).thenReturn(mockRecetaAjena);
        mockMvc.perform(get("/usuario/editar-receta/{id}", idRecetaAjena))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/usuario/dashboard"));

        when(recetaService.obtenerPorId(idRecetaNoExiste)).thenReturn(null);
        mockMvc.perform(get("/usuario/editar-receta/{id}", idRecetaNoExiste))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/usuario/dashboard"));
    }

    @Test
    @DisplayName("DELETE /usuario/eliminar-receta/{id} debe eliminar la receta si pertenece al usuario")
    void testEliminarReceta_Exitoso() throws Exception {
        Long idReceta = 101L;

        when(recetaService.obtenerPorId(idReceta)).thenReturn(mockRecetaPropia);
        doNothing().when(recetaService).eliminarReceta(idReceta);

        mockMvc.perform(delete("/usuario/eliminar-receta/{id}", idReceta)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/usuario/dashboard"))
                .andExpect(flash().attributeExists("mensaje"));

        verify(recetaService, times(1)).eliminarReceta(idReceta);
    }
    
    @Test
    @DisplayName("DELETE /usuario/eliminar-receta/{id} debe redirigir sin eliminar si la receta es ajena")
    void testEliminarReceta_Ajena() throws Exception {
        Long idRecetaAjena = 102L;

        when(recetaService.obtenerPorId(idRecetaAjena)).thenReturn(mockRecetaAjena);

        mockMvc.perform(delete("/usuario/eliminar-receta/{id}", idRecetaAjena)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/usuario/dashboard"));

        verify(recetaService, never()).eliminarReceta(anyLong());
    }

    @Test
    @DisplayName("POST /usuario/actualizar-perfil debe llamar al servicio de actualización y redirigir")
    void testActualizarPerfil() throws Exception {
        Usuario usuarioActualizadoForm = new Usuario(); 
        usuarioActualizadoForm.setNombreCompleto("Nuevo Nombre"); 
        String nuevaPassword = "newPassword123";

        mockMvc.perform(post("/usuario/actualizar-perfil")
                        .flashAttr("usuarioActualizado", usuarioActualizadoForm)
                        .param("nuevaPassword", nuevaPassword)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/usuario/dashboard"))
                .andExpect(flash().attributeExists("mensaje"));

        verify(usuarioService, times(1)).actualizarPerfil(
            eq(USERNAME), 
            any(Usuario.class), 
            eq(nuevaPassword)
        );
    }
}