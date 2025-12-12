package com.duoc.semana2.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class UsuarioTest {

    private final LocalDateTime now = LocalDateTime.now();

    // Objeto base para pruebas de igualdad
    private Usuario baseUser;

    @BeforeEach
    void setUp() {
        // Inicialización para evitar que los tests de equals fallen por campos nulos
        baseUser = Usuario.builder()
            .id(1L)
            .username("testuser")
            .email("test@mail.com")
            .nombre("Test") // Agregamos más campos para ser explícitos
            .build();
    }

    // --- Tests de Constructores y Valores por Defecto ---

    @Test
    void testNoArgsConstructorAndDefaults() {
        // Cubre @NoArgsConstructor
        Usuario user = new Usuario();
        
        assertNotNull(user);
        assertEquals("USER", user.getRol());
        assertNotNull(user.getFechaRegistro()); 
        assertNotNull(user.getRecetas()); 
        assertNotNull(user.getRecetasFavoritas());
    }

    @Test
    void testAllArgsConstructor() {
        // Cubre @AllArgsConstructor
        List<Receta> recetas = new ArrayList<>();
        recetas.add(new Receta());
        
        Usuario user = new Usuario(
            1L, 
            "admin", 
            "hashedpass", 
            "admin@mail.com", 
            "Admin",
            "Admin User",
            "Chef Principal",
            "Amante de la cocina",
            "ADMIN",
            now,
            recetas,
            new ArrayList<>()
        );

        assertNotNull(user);
        assertEquals("ADMIN", user.getRol());
        assertEquals("Admin User", user.getNombreCompleto());
        assertEquals(1, user.getRecetas().size());
    }

    // --- Tests de Getters y Setters (@Data) ---

    @Test
    void testSettersAndGetters() {
        Usuario user = new Usuario();
        user.setId(2L);
        user.setUsername("setter_user");
        user.setPassword("secure");
        user.setTitulo("Food Critic");
        user.setBiografia("Reviews");
        
        assertEquals(2L, user.getId());
        assertEquals("setter_user", user.getUsername());
        assertEquals("secure", user.getPassword());
        assertEquals("Food Critic", user.getTitulo());
        assertEquals("Reviews", user.getBiografia());
        
        // Asignación de listas
        List<Receta> nuevasRecetas = Arrays.asList(new Receta());
        user.setRecetas(nuevasRecetas);
        assertEquals(1, user.getRecetas().size());
    }

    // --- Tests de Lombok Generados (@Builder, equals, hashCode, toString) ---

    @Test
    void testBuilderConstructorAndDefaults() {
        // Cubre @Builder, incluyendo valores por defecto gracias a @Builder.Default
        Usuario user = Usuario.builder()
            .username("builder_user")
            .email("b@mail.com")
            // No se especifica 'rol' ni 'fechaRegistro'
            .build();
            
        assertNotNull(user);
        assertEquals("builder_user", user.getUsername());
        assertEquals("b@mail.com", user.getEmail());
        // Estas aserciones pasan porque ahora @Builder.Default funciona
        assertEquals("USER", user.getRol()); 
        assertNotNull(user.getFechaRegistro()); 
    }
    
    @Test
    void testEqualsAndHashCode() {
        Usuario sameUser = Usuario.builder()
            .id(1L)
            .username("testuser")
            .email("test@mail.com")
            .nombre("Test")
            .recetas(Arrays.asList(new Receta())) 
            .build();
            
        
        Usuario differentUser = Usuario.builder()
            .id(2L) 
            .username("another")
            .build();

        
        assertTrue(baseUser.equals(sameUser));
        assertEquals(baseUser.hashCode(), sameUser.hashCode());

        
        assertFalse(baseUser.equals(differentUser));
        assertFalse(baseUser.equals(null));
        assertFalse(baseUser.equals(new Object())); 
    }

    @Test
    void testToString() {
        // Cubre el método toString() generado por Lombok
        Usuario user = Usuario.builder()
            .username("tostring_user")
            .email("tostring@mail.com")
            .build();

        String s = user.toString();
        assertNotNull(s);
        assertTrue(s.contains("tostring_user"));
        assertTrue(s.contains("tostring@mail.com"));
    }

    @Test
    void testEquals_fullBranchCoverage() {
    
        Usuario base = Usuario.builder()
                .id(1L)
                .username("u")
                .password("p")
                .email("e@mail.com")
                .nombre("n")
                .nombreCompleto("nc")
                .titulo("t")
                .biografia("bio")
                .rol("USER")
                .fechaRegistro(now)
                .recetas(new ArrayList<>())
                .recetasFavoritas(new ArrayList<>())
                .build();
    
        // 1) mismo objeto
        assertTrue(base.equals(base));
    
        // 2) null
        assertFalse(base.equals(null));
    
        // 3) clase diferente
        assertFalse(base.equals("string"));
    
        // 4) subclase → activa rama canEqual(false)
        class UsuarioHijo extends Usuario {}
        Usuario hijo = new UsuarioHijo();
        hijo.setId(1L);
        assertFalse(base.equals(hijo));
    
        // 5) objeto idéntico (todas las ramas true)
        Usuario igual = Usuario.builder()
                .id(1L)
                .username("u")
                .password("p")
                .email("e@mail.com")
                .nombre("n")
                .nombreCompleto("nc")
                .titulo("t")
                .biografia("bio")
                .rol("USER")
                .fechaRegistro(now)
                .recetas(new ArrayList<>())
                .recetasFavoritas(new ArrayList<>())
                .build();
    
        assertTrue(base.equals(igual));
        assertTrue(igual.equals(base));
    
        // 6) Cambiar un campo por vez → cubre ramas negativas
        String[] nuevosUsernames = {null, "otro"};
    
        for (String nuevo : nuevosUsernames) {
            Usuario mod = Usuario.builder().id(1L).build();
            mod.setUsername(nuevo);
            assertFalse(base.equals(mod));
        }
    
        // 7) Campos nulos/asimétricos → ramas adicionales
        Usuario conNulls = Usuario.builder().id(1L).username(null).build();
        Usuario sinNulls = Usuario.builder().id(1L).username("u").build();
    
        assertFalse(conNulls.equals(sinNulls));
        assertFalse(sinNulls.equals(conNulls));
    }

}