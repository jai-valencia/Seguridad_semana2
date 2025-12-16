package com.duoc.semana2.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.containsString; 

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AdminController.class)
class AdminViewTest {

    @Autowired
    private MockMvc mvc;

    @Test
    @WithMockUser(username = "Jaime", roles = {"ADMIN"})
    void testAdminHtmlLoads() throws Exception {
        mvc.perform(get("/admin"))
           .andExpect(status().isOk())
           .andExpect(view().name("admin"))
           .andExpect(content().string(containsString("Mis Recetas")))
           .andExpect(content().string(containsString("Favoritas")))
           .andExpect(content().string(containsString("Perfil")));
    }
}
