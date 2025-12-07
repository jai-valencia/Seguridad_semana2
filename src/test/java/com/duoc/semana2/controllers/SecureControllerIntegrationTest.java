package com.duoc.semana2.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Test de integración para SecureController.
 */
@SpringBootTest
@AutoConfigureMockMvc
class SecureControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    // ==========================================================
    // GET /api/secure/hello
    // ==========================================================
    @Test
    void hello_debeRetornarTextoJwtOk() throws Exception {

        mockMvc.perform(get("/api/secure/hello"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.TEXT_PLAIN_VALUE))
                .andExpect(content().string("JWT OK 👋"));
    }
}
