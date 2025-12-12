package com.duoc.semana2.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

public class RecetaTest {

    // Usuario simple para usar en tests que requieren una relación
    private final Usuario mockUsuario = new Usuario();
    // --- Tests de Constructores y Estado Inicial ---

    @Test
    void testNoArgsConstructor() {
        Receta receta = new Receta();
        assertNotNull(receta);
        // Valores por defecto:
        assertEquals(0, receta.getVistas());
        assertEquals(0, receta.getLikes());
        assertTrue(receta.getPublica());
        assertNotNull(receta.getFotosEmojis());
        assertNotNull(receta.getUsuariosQueFavoritearon());
    }

    @Test
    void testAllArgsConstructor() {
        LocalDateTime now = LocalDateTime.now();

        Receta receta = new Receta(
            1L, "Lasaña", "Italiana", "Italia", "Media", "🍝",
            30, 60, 4, 600, 10, 5, 
            false, now, "pasta\ncarne", "Paso 1||Paso 2", 
            "Una deliciosa lasaña", mockUsuario, 
            Collections.singletonList("emoji1"), 
            Collections.singletonList(mockUsuario)
        );

        assertNotNull(receta);
        assertEquals("Italiana", receta.getTipoCocina());
        assertEquals(false, receta.getPublica());
        assertEquals(mockUsuario, receta.getUsuario());
    }

    // --- Tests de Cobertura de Setters y Getters (Lombok @Data) ---

    @Test
    void testFullSettersAndGettersCoverage() {
        Receta receta = new Receta();
        
        // 1. Setters de campos básicos/primitivos (Cubren casi todos los métodos 0% en la imagen)
        receta.setId(5L);
        receta.setNombre("Tarta de Manzana");
        receta.setTipoCocina("Postre");         // Cobertura específica
        receta.setPais("Chile");                // Cobertura específica
        receta.setDificultad("Fácil");          // Cobertura específica
        receta.setEmoji("🍎");                   // Cobertura específica
        receta.setTiempoPreparacion(15);
        receta.setTiempoCoccion(45);
        receta.setPorciones(8);
        receta.setCalorias(350);
        receta.setVistas(100);
        receta.setLikes(10);                    // Cobertura específica
        receta.setPublica(false);               // Cobertura específica
        receta.setFechaCreacion(LocalDateTime.now());
        receta.setIngredientes("Manzanas\nAzúcar");
        receta.setInstrucciones("Mezclar||Hornear");
        receta.setDescripcion("Clásico postre chileno.");
        receta.setUsuario(mockUsuario);

        // 2. Setters de colecciones (Cubren los dos últimos métodos 0% en la imagen)
        List<String> fotos = Arrays.asList("foto1.jpg");
        List<Usuario> favs = Arrays.asList(mockUsuario);
        receta.setFotosEmojis(fotos);
        receta.setUsuariosQueFavoritearon(favs);
        
        // 3. Verificación de Getters
        assertEquals(5L, receta.getId());
        assertEquals("Tarta de Manzana", receta.getNombre());
        assertEquals("Postre", receta.getTipoCocina());
        assertEquals("Chile", receta.getPais());
        assertEquals("Fácil", receta.getDificultad());
        assertEquals("🍎", receta.getEmoji());
        assertEquals(15, receta.getTiempoPreparacion());
        assertEquals(45, receta.getTiempoCoccion());
        assertEquals(8, receta.getPorciones());
        assertEquals(350, receta.getCalorias());
        assertEquals(100, receta.getVistas());
        assertEquals(10, receta.getLikes());
        assertFalse(receta.getPublica());
        assertNotNull(receta.getFechaCreacion());
        assertEquals("Manzanas\nAzúcar", receta.getIngredientes());
        assertEquals("Mezclar||Hornear", receta.getInstrucciones());
        assertEquals("Clásico postre chileno.", receta.getDescripcion());
        assertEquals(mockUsuario, receta.getUsuario());
        assertEquals(fotos, receta.getFotosEmojis());
        assertEquals(favs, receta.getUsuariosQueFavoritearon());
    }

    // --- Tests de Lógica Condicional (Métodos Calculados) ---

    @Test
    void testGetTiempoTotal_ConValores() {
        Receta receta = new Receta();
        receta.setTiempoPreparacion(10);
        receta.setTiempoCoccion(20);
        assertEquals(30, receta.getTiempoTotal());
    }

    @Test
    void testGetTiempoTotal_ConValoresNulos() {
        // Cubre la rama 'else' donde los valores son null
        Receta receta = new Receta(); 
        assertEquals(0, receta.getTiempoTotal()); // Ambos null

        receta.setTiempoPreparacion(15);
        receta.setTiempoCoccion(null); 
        assertEquals(15, receta.getTiempoTotal()); // TiempoCoccion null

        receta.setTiempoPreparacion(null); 
        receta.setTiempoCoccion(5); 
        assertEquals(5, receta.getTiempoTotal()); // TiempoPreparacion null
    }

    // --- Tests de Métodos de Lista (Separadores) ---
    
    @Test
    void testGetIngredientesList_ConContenido() {
        Receta receta = new Receta();
        receta.setIngredientes("Huevos\nLeche\nHarina");
        List<String> lista = receta.getIngredientesList();
        assertEquals(3, lista.size());
        assertEquals("Huevos", lista.get(0));
    }

    @Test
    void testGetIngredientesList_NuloOVacio() {
        Receta receta1 = new Receta();
        assertTrue(receta1.getIngredientesList().isEmpty()); // Ingredientes es null

        Receta receta2 = new Receta();
        receta2.setIngredientes("");
        assertTrue(receta2.getIngredientesList().isEmpty()); // Ingredientes es String vacío
    }
    
    @Test
    void testGetInstruccionesList_ConContenido() {
        Receta receta = new Receta();
        receta.setInstrucciones("Paso 1||Paso 2||Paso 3");
        List<String> lista = receta.getInstruccionesList();
        assertEquals(3, lista.size());
        assertEquals("Paso 1", lista.get(0));
    }

    @Test
    void testGetInstruccionesList_NuloOVacio() {
        Receta receta1 = new Receta();
        assertTrue(receta1.getInstruccionesList().isEmpty()); // Instrucciones es null
        
        Receta receta2 = new Receta();
        receta2.setInstrucciones("");
        assertTrue(receta2.getInstruccionesList().isEmpty()); // Instrucciones es String vacío
    }

    // --- Tests de Cobertura para Lombok (equals/hashCode/canEqual/toString) ---

    @Test
    void testEqualsAndHashCode_Completo() {
        Receta r1 = Receta.builder().id(1L).nombre("A").build();
        Receta r2 = Receta.builder().id(1L).nombre("A").build();
        Receta r3 = Receta.builder().id(2L).nombre("B").build();
        
        // 1. Cubrir equals(true) y hashCode (iguales)
        assertTrue(r1.equals(r2));
        assertEquals(r1.hashCode(), r2.hashCode());
        
        // 2. Cubrir equals(false) por diferente ID/Nombre
        assertFalse(r1.equals(r3));

        // 3. Cubrir equals(false) con null
        assertFalse(r1.equals(null));

        // 4. Cubrir equals(false) con clase diferente (cubre canEqual() si retorna false)
        assertFalse(r1.equals(new Object())); 
        
        // 5. Cubrir equals(true) con la misma referencia (r1.equals(r1))
        assertTrue(r1.equals(r1));
    }
    
    @Test
    void testToString() {
        Receta r1 = Receta.builder().id(1L).nombre("Sopa").build();
        // Cubre el método toString() generado por Lombok
        String s = r1.toString();
        assertNotNull(s);
        assertTrue(s.contains("id=1"));
        assertTrue(s.contains("nombre=Sopa"));
    }

    @Test
    void testBuilderConstructor() {
        Receta receta = Receta.builder()
            .nombre("Ensalada")
            .dificultad("Baja")
            .fotosEmojis(new ArrayList<>())
            .build();

        assertNotNull(receta);
        assertEquals("Ensalada", receta.getNombre());
        assertEquals("Baja", receta.getDificultad());
        assertTrue(receta.getFotosEmojis().isEmpty());
    }

    @Test
    void testRecetaBuilder_FullCoverage() {
        Usuario usuarioMock = new Usuario();
        List<String> fotos = Arrays.asList("f1", "f2");
        List<Usuario> favs = Arrays.asList(usuarioMock);

        Receta.RecetaBuilder builder = Receta.builder()
                .id(10L)
                .nombre("BuilderTest")
                .tipoCocina("Internacional")
                .pais("Chile")
                .dificultad("Media")
                .emoji("🔥")
                .tiempoPreparacion(5)
                .tiempoCoccion(15)
                .porciones(3)
                .calorias(555)
                .vistas(9)
                .likes(2)
                .publica(false)  // ⚠️ este método estaba 0%
                .fechaCreacion(LocalDateTime.now())
                .ingredientes("a\nb")
                .instrucciones("i1||i2")
                .descripcion("desc")
                .usuario(usuarioMock)
                .fotosEmojis(fotos)
                .usuariosQueFavoritearon(favs);

        // ✔ Cubrir toString() del builder
        String builderStr = builder.toString();
        assertNotNull(builderStr);
        assertTrue(builderStr.contains("BuilderTest"));
        assertTrue(builderStr.contains("Chile"));

        // ✔ Ejecutar build() (cubre segundo bloque)
        Receta receta = builder.build();

        // Verificar que realmente asignó todo
        assertEquals(10L, receta.getId());
        assertEquals("BuilderTest", receta.getNombre());
        assertEquals("Internacional", receta.getTipoCocina());
        assertEquals("Chile", receta.getPais());
        assertEquals("Media", receta.getDificultad());
        assertEquals("🔥", receta.getEmoji());
        assertEquals(5, receta.getTiempoPreparacion());
        assertEquals(15, receta.getTiempoCoccion());
        assertEquals(3, receta.getPorciones());
        assertEquals(555, receta.getCalorias());
        assertEquals(9, receta.getVistas());
        assertEquals(2, receta.getLikes());
        assertFalse(receta.getPublica());
        assertNotNull(receta.getFechaCreacion());
        assertEquals("a\nb", receta.getIngredientes());
        assertEquals("i1||i2", receta.getInstrucciones());
        assertEquals("desc", receta.getDescripcion());
        assertEquals(usuarioMock, receta.getUsuario());
        assertEquals(fotos, receta.getFotosEmojis());
        assertEquals(favs, receta.getUsuariosQueFavoritearon());
    }

    @Test
    void testEquals_FullBranchCoverage() {
        
        Receta r1 = Receta.builder().id(1L).nombre("A").build();
        assertTrue(r1.equals(r1));

       
        assertFalse(r1.equals(null));

        
        assertFalse(r1.equals("string"));

        
        class RecetaHija extends Receta {}
        RecetaHija hija = new RecetaHija();
        hija.setId(1L);
        assertFalse(r1.equals(hija));

        
        Receta r2 = Receta.builder().id(1L).nombre("A").build();
        assertTrue(r1.equals(r2));

       
        Receta r3 = Receta.builder().id(2L).nombre("B").build();
        assertFalse(r1.equals(r3));

       
        Receta r4 = Receta.builder().id(1L).nombre(null).build();
        Receta r5 = Receta.builder().id(1L).nombre("Nombre").build();
        assertFalse(r4.equals(r5));
        assertFalse(r5.equals(r4)); 
    }

    @Test
    void testEquals_fullMatrixCoverage() {
        Receta base = Receta.builder()
                .id(1L)
                .nombre("A")
                .tipoCocina("B")
                .pais("C")
                .dificultad("D")
                .emoji("E")
                .tiempoPreparacion(1)
                .tiempoCoccion(2)
                .porciones(3)
                .calorias(4)
                .vistas(5)
                .likes(6)
                .publica(true)
                .fechaCreacion(LocalDateTime.now())
                .ingredientes("x\ny")
                .instrucciones("i1||i2")
                .descripcion("desc")
                .usuario(new Usuario())
                .fotosEmojis(Arrays.asList("f1"))
                .usuariosQueFavoritearon(Arrays.asList(new Usuario()))
                .build();

        // ≠ null
        assertFalse(base.equals(null));

        // ≠ distinto tipo
        assertFalse(base.equals("string"));

        // ≠ canEqual false
        class SubReceta extends Receta {}
        assertFalse(base.equals(new SubReceta()));

        // ≠ mismo objeto
        assertTrue(base.equals(base));

        // = objetos idénticos
        Receta igual = Receta.builder()
                .id(1L)
                .nombre("A")
                .tipoCocina("B")
                .pais("C")
                .dificultad("D")
                .emoji("E")
                .tiempoPreparacion(1)
                .tiempoCoccion(2)
                .porciones(3)
                .calorias(4)
                .vistas(5)
                .likes(6)
                .publica(true)
                .fechaCreacion(base.getFechaCreacion())
                .ingredientes("x\ny")
                .instrucciones("i1||i2")
                .descripcion("desc")
                .usuario(base.getUsuario())
                .fotosEmojis(Arrays.asList("f1"))
                .usuariosQueFavoritearon(Arrays.asList(new Usuario()))
                .build();

        assertTrue(base.equals(igual));
        assertTrue(igual.equals(base));

        // ≠ un campo distinto cada vez (cubre ramas internas de Lombok)
        String[] nuevosNombres = {"Z", null};

        for (String nuevoNombre : nuevosNombres) {
            Receta distinto = Receta.builder().id(1L).build();
            distinto.setNombre(nuevoNombre);
            assertFalse(base.equals(distinto));
        }
    }

    @Test
    void testHashCode_fullCoverage() {
        Receta r1 = Receta.builder().id(1L).nombre("A").build();
        Receta r2 = Receta.builder().id(1L).nombre("A").build();

        // = mismos valores → mismo hashCode
        assertEquals(r1.hashCode(), r2.hashCode());
        
        // ≠ cambiar un campo → distinto hash
        r2.setNombre("B");
        assertNotEquals(r1.hashCode(), r2.hashCode());

        // ≠ comparar con null fields
        Receta r3 = Receta.builder().id(null).nombre(null).build();
        Receta r4 = Receta.builder().id(null).nombre(null).build();
        assertEquals(r3.hashCode(), r4.hashCode());
    }

    @Test
    void testAllArgsConstructor_fullCoverage() {
        Usuario u = new Usuario();
        List<String> fotos = Arrays.asList("a");
        List<Usuario> favs = Arrays.asList(new Usuario());
        LocalDateTime f = LocalDateTime.now();

        Receta r = new Receta(
            9L, "N", "TC", "CH", "D", "E",
            1,2,3,4,5,6,
            false, f, "ing", "ins",
            "desc", u, fotos, favs
        );

        assertEquals("N", r.getNombre());
        assertEquals("TC", r.getTipoCocina());
        assertEquals("CH", r.getPais());
        assertEquals("D", r.getDificultad());
        assertEquals("E", r.getEmoji());
        assertEquals(1, r.getTiempoPreparacion());
        assertEquals(2, r.getTiempoCoccion());
        assertEquals(3, r.getPorciones());
        assertEquals(4, r.getCalorias());
        assertEquals(5, r.getVistas());
        assertEquals(6, r.getLikes());
        assertFalse(r.getPublica());
        assertEquals(f, r.getFechaCreacion());
        assertEquals("ing", r.getIngredientes());
        assertEquals("ins", r.getInstrucciones());
        assertEquals("desc", r.getDescripcion());
        assertEquals(u, r.getUsuario());
        assertEquals(fotos, r.getFotosEmojis());
        assertEquals(favs, r.getUsuariosQueFavoritearon());
    }

    @Test
    void testEquals_matrixFieldByField() {
    
        Receta base = Receta.builder()
                .id(1L)
                .nombre("A")
                .tipoCocina("TC")
                .pais("P")
                .dificultad("D")
                .emoji("E")
                .tiempoPreparacion(1)
                .tiempoCoccion(2)
                .porciones(3)
                .calorias(4)
                .vistas(5)
                .likes(6)
                .publica(true)
                .fechaCreacion(LocalDateTime.now())
                .ingredientes("i1\ni2")
                .instrucciones("s1||s2")
                .descripcion("desc")
                .usuario(new Usuario())
                .fotosEmojis(Arrays.asList("f1"))
                .usuariosQueFavoritearon(Arrays.asList(new Usuario()))
                .build();
    
        // Lista de campos a probar con objetos modificados
        Receta[] variantes = new Receta[] {
                // distinto nombre
                Receta.builder().id(1L).nombre("Z").build(),
                // null nombre
                Receta.builder().id(1L).nombre(null).build(),
                // distinto tipoCocina
                Receta.builder().id(1L).tipoCocina("X").build(),
                // distinto pais
                Receta.builder().id(1L).pais("ARG").build(),
                // distinto dificultad
                Receta.builder().id(1L).dificultad("Alta").build(),
                // distinto emoji
                Receta.builder().id(1L).emoji("🔥").build(),
                // distinto valores enteros
                Receta.builder().id(1L).tiempoPreparacion(99).build(),
                Receta.builder().id(1L).tiempoCoccion(99).build(),
                Receta.builder().id(1L).porciones(99).build(),
                Receta.builder().id(1L).calorias(99).build(),
                Receta.builder().id(1L).vistas(99).build(),
                Receta.builder().id(1L).likes(99).build(),
                // distinto boolean
                Receta.builder().id(1L).publica(false).build(),
                // distinto fecha
                Receta.builder().id(1L).fechaCreacion(LocalDateTime.now().minusDays(1)).build(),
                // distinto ingredientes
                Receta.builder().id(1L).ingredientes("otra").build(),
                // distinto instrucciones
                Receta.builder().id(1L).instrucciones("x||y").build(),
                // distinto descripcion
                Receta.builder().id(1L).descripcion("otra").build(),
                // distinto usuario
                Receta.builder().id(1L).usuario(new Usuario()).build(),
                // distinta lista fotos
                Receta.builder().id(1L).fotosEmojis(Arrays.asList("X")).build(),
                // distinta lista favoritos
                Receta.builder().id(1L).usuariosQueFavoritearon(Arrays.asList(new Usuario(), new Usuario())).build(),
        };
    
        for (Receta variante : variantes) {
            assertFalse(base.equals(variante), "Debe detectar diferencia entre campos");
        }
    }


}