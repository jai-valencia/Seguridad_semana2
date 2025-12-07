package com.duoc.semana2.model;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

class UsuarioTest {

    @Test
    void gettersYSetters_funcionanCorrectamente() {
        Usuario u = new Usuario();

        u.setUsername("jaime");
        u.setPassword("1234");
        u.setNombre("Jaime Valencia");
        u.setEmail("jaime@test.com");
        u.setBiografia("Bio");
        u.setRol("USER");

        LocalDateTime fecha = LocalDateTime.now();
        u.setFechaRegistro(fecha);

        assertEquals("jaime", u.getUsername());
        assertEquals("1234", u.getPassword());
        assertEquals("Jaime Valencia", u.getNombre());
        assertEquals("jaime@test.com", u.getEmail());
        assertEquals("Bio", u.getBiografia());
        assertEquals("USER", u.getRol());
        assertEquals(fecha, u.getFechaRegistro());
    }

    @Test
    void relacionesIniciales_noSonNull() {
        Usuario u = new Usuario();

        assertNotNull(u.getRecetas());
        assertNotNull(u.getRecetasFavoritas());
    }

    @Test
    void agregarRecetaAFavoritos_ok() {
        Usuario u = new Usuario();
        Receta r = new Receta();
        r.setNombre("Pasta");

        u.setRecetasFavoritas(List.of(r));

        assertEquals(1, u.getRecetasFavoritas().size());
        assertEquals("Pasta", u.getRecetasFavoritas().get(0).getNombre());
    }
}
