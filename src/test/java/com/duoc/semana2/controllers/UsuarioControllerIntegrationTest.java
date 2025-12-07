package com.duoc.semana2.controllers;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Collections;

import com.duoc.semana2.model.Receta;
import com.duoc.semana2.model.Usuario;
import com.duoc.semana2.service.RecetaService;
import com.duoc.semana2.service.UsuarioService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class UsuarioControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RecetaService recetaService;

    @MockBean
    private UsuarioService usuarioService;

    private Principal principalMock(String username) {
        return () -> username;
    }

    // ================================================================
    // 1. GET /usuario/dashboard
    // ================================================================
    @Test
    void dashboard_debeRetornarVistaConDatos() throws Exception {

        Usuario usuarioMock = mock(Usuario.class);
        when(usuarioMock.getId()).thenReturn(1L);

        when(usuarioService.obtenerPorUsername("jaime"))
                .thenReturn(usuarioMock);

        when(recetaService.obtenerRecetasPorUsuario(1L))
                .thenReturn(Collections.emptyList());

        when(recetaService.obtenerRecetasFavoritas(1L))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/usuario/dashboard")
                .principal(principalMock("jaime")))
                .andExpect(status().isOk())
                .andExpect(view().name("usuario-dashboard"))
                .andExpect(model().attributeExists("usuario"))
                .andExpect(model().attributeExists("misRecetas"))
                .andExpect(model().attributeExists("recetasFavoritas"));
    }

    // ================================================================
    // 2. GET /usuario/nueva-receta
    // ================================================================
    @Test
    void nuevaReceta_debeMostrarFormulario() throws Exception {
        mockMvc.perform(get("/usuario/nueva-receta"))
                .andExpect(status().isOk())
                .andExpect(view().name("formulario-receta"))
                .andExpect(model().attributeExists("receta"));
    }

    // ================================================================
    // 3. POST /usuario/guardar-receta
    // ================================================================
    @Test
    void guardarReceta_debeRedirigirDashboard() throws Exception {

        Usuario usuarioMock = mock(Usuario.class);
        when(usuarioService.obtenerPorUsername("jaime"))
                .thenReturn(usuarioMock);

        Receta recetaFake = mock(Receta.class);

        mockMvc.perform(post("/usuario/guardar-receta")
                .flashAttr("receta", recetaFake)
                .principal(principalMock("jaime")))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/usuario/dashboard"));
    }

    // ================================================================
    // 4. GET /usuario/editar-receta/{id} (Receta válida)
    // ================================================================
    @Test
    void editarReceta_recetaPerteneceUsuario_debeMostrarFormulario() throws Exception {

        Usuario usuarioMock = mock(Usuario.class);
        when(usuarioMock.getId()).thenReturn(1L);
        when(usuarioService.obtenerPorUsername("jaime"))
                .thenReturn(usuarioMock);

        Receta recetaMock = mock(Receta.class);
        when(recetaMock.getUsuario()).thenReturn(usuarioMock);
        when(recetaService.obtenerPorId(10L))
                .thenReturn(recetaMock);

        mockMvc.perform(get("/usuario/editar-receta/10")
                .principal(principalMock("jaime")))
                .andExpect(status().isOk())
                .andExpect(view().name("formulario-receta"))
                .andExpect(model().attributeExists("receta"));
    }

    // ================================================================
    // 4b. GET /usuario/editar-receta/{id} (Receta NO pertenece)
    // ================================================================
    @Test
    void editarReceta_recetaNoPertenece_debeRedirigirDashboard() throws Exception {

        Usuario usuarioMock = mock(Usuario.class);
        when(usuarioMock.getId()).thenReturn(1L);
        when(usuarioService.obtenerPorUsername("jaime"))
                .thenReturn(usuarioMock);

        Usuario otroUsuario = mock(Usuario.class);
        when(otroUsuario.getId()).thenReturn(2L);

        Receta recetaMock = mock(Receta.class);
        when(recetaMock.getUsuario()).thenReturn(otroUsuario);

        when(recetaService.obtenerPorId(10L))
                .thenReturn(recetaMock);

        mockMvc.perform(get("/usuario/editar-receta/10")
                .principal(principalMock("jaime")))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/usuario/dashboard"));
    }

    // ================================================================
    // 5. DELETE /usuario/eliminar-receta/{id}
    // ================================================================
    @Test
    void eliminarReceta_debeRedirigirDashboard() throws Exception {

        Usuario usuarioMock = mock(Usuario.class);
        when(usuarioMock.getId()).thenReturn(1L);

        when(usuarioService.obtenerPorUsername("jaime"))
                .thenReturn(usuarioMock);

        Receta recetaMock = mock(Receta.class);
        when(recetaMock.getUsuario()).thenReturn(usuarioMock);

        when(recetaService.obtenerPorId(5L))
                .thenReturn(recetaMock);

        mockMvc.perform(delete("/usuario/eliminar-receta/5")
                .principal(principalMock("jaime")))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/usuario/dashboard"));
    }

    // ================================================================
    // 6. POST /usuario/actualizar-perfil
    // ================================================================
    @Test
    void actualizarPerfil_debeRedirigirDashboard() throws Exception {

        Usuario usuarioFake = mock(Usuario.class);

        mockMvc.perform(post("/usuario/actualizar-perfil")
                .flashAttr("usuarioActualizado", usuarioFake)
                .param("nuevaPassword", "123456")
                .principal(principalMock("jaime")))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/usuario/dashboard"));
    }
}
