package com.duoc.semana2.model;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

class RecetaTest {

    @Test
    void settersYGetters_funcionanCorrectamente() {
        Receta r = new Receta();

        r.setNombre("Pasta");
        r.setTipoCocina("Italiana");
        r.setPais("Italia");
        r.setDificultad("Media");
        r.setVistas(10);
        r.setIngredientes("Tomate\nQueso");
        r.setInstrucciones("Paso1||Paso2");

        assertEquals("Pasta", r.getNombre());
        assertEquals("Italiana", r.getTipoCocina());
        assertEquals("Italia", r.getPais());
        assertEquals("Media", r.getDificultad());
        assertEquals(10, r.getVistas());
        assertEquals("Tomate\nQueso", r.getIngredientes());
        assertEquals("Paso1||Paso2", r.getInstrucciones());
    }

    @Test
    void getTiempoTotal_sumaPreparacionYCoccion() {
        Receta r = new Receta();
        r.setTiempoPreparacion(10);
        r.setTiempoCoccion(20);

        assertEquals(30, r.getTiempoTotal());
    }

    @Test
    void getTiempoTotal_noFallaConNull() {
        Receta r = new Receta();
        r.setTiempoPreparacion(null);
        r.setTiempoCoccion(15);

        assertEquals(15, r.getTiempoTotal());
    }

    @Test
    void getIngredientesList_separadoPorSaltosLinea() {
        Receta r = new Receta();
        r.setIngredientes("Tomate\nQueso\nCarne");

        List<String> ingredientes = r.getIngredientesList();

        assertEquals(3, ingredientes.size());
        assertEquals("Tomate", ingredientes.get(0));
        assertEquals("Queso", ingredientes.get(1));
        assertEquals("Carne", ingredientes.get(2));
    }

    @Test
    void getIngredientesList_vaciaSiNull() {
        Receta r = new Receta();
        r.setIngredientes(null);

        assertTrue(r.getIngredientesList().isEmpty());
    }

    @Test
    void getInstruccionesList_separadoPorDoblePipe() {
        Receta r = new Receta();
        r.setInstrucciones("Paso1||Paso2||Paso3");

        List<String> instrucciones = r.getInstruccionesList();

        assertEquals(3, instrucciones.size());
        assertEquals("Paso1", instrucciones.get(0));
        assertEquals("Paso2", instrucciones.get(1));
        assertEquals("Paso3", instrucciones.get(2));
    }

    @Test
    void fechaCreacion_setterGetter() {
        Receta r = new Receta();
        LocalDateTime now = LocalDateTime.now();
        r.setFechaCreacion(now);

        assertEquals(now, r.getFechaCreacion());
    }
}
