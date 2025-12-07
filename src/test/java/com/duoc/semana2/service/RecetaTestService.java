package com.duoc.semana2.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.duoc.semana2.model.Receta;
import com.duoc.semana2.repository.RecetaRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
class RecetaServiceTest {

    @Mock
    private RecetaRepository recetaRepository;

    @InjectMocks
    private RecetaService recetaService;

    // ======================================================
    // obtenerPorId
    // ======================================================
    @Test
    void obtenerPorId_retornarRecetaSiExiste() {
        Receta receta = new Receta();
        receta.setNombre("Pasta");

        when(recetaRepository.findById(1L)).thenReturn(Optional.of(receta));

        Receta resultado = recetaService.obtenerPorId(1L);

        assertNotNull(resultado);
        assertEquals("Pasta", resultado.getNombre());
    }

    @Test
    void obtenerPorId_retornarNullSiNoExiste() {
        when(recetaRepository.findById(1L)).thenReturn(Optional.empty());

        Receta resultado = recetaService.obtenerPorId(1L);

        assertNull(resultado);
    }

    // ======================================================
    // obtenerTodasLasRecetas
    // ======================================================
    @Test
    void obtenerTodasLasRecetas_ok() {
        when(recetaRepository.findAll()).thenReturn(Collections.emptyList());

        List<Receta> resultado = recetaService.obtenerTodasLasRecetas();

        assertNotNull(resultado);
    }

    // ======================================================
    // buscarRecetas
    // ======================================================
    @Test
    void buscarRecetas_invocaRepositorio() {
        when(recetaRepository.buscarConFiltros(any(), any(), any(), any(), any()))
                .thenReturn(Collections.emptyList());

        recetaService.buscarRecetas("a", "b", "c", "d", "e");

        verify(recetaRepository).buscarConFiltros("a", "b", "c", "d", "e");
    }

    // ======================================================
    // obtenerRecetasPopulares
    // ======================================================
    @Test
    void obtenerRecetasPopulares_ok() {
        when(recetaRepository.findTopRecetas(any(PageRequest.class)))
                .thenReturn(Collections.emptyList());

        recetaService.obtenerRecetasPopulares(3);

        verify(recetaRepository).findTopRecetas(PageRequest.of(0, 3));
    }

    // ======================================================
    // obtenerRecetasRecientes
    // ======================================================
    @Test
    void obtenerRecetasRecientes_ok() {
        when(recetaRepository.findRecentRecetas(any(PageRequest.class)))
                .thenReturn(Collections.emptyList());

        recetaService.obtenerRecetasRecientes(3);

        verify(recetaRepository).findRecentRecetas(PageRequest.of(0, 3));
    }

    // ======================================================
    // obtenerRecetasPorUsuario
    // ======================================================
    @Test
    void obtenerRecetasPorUsuario_ok() {
        when(recetaRepository.findByUsuarioId(10L)).thenReturn(Collections.emptyList());

        recetaService.obtenerRecetasPorUsuario(10L);

        verify(recetaRepository).findByUsuarioId(10L);
    }

    // ======================================================
    // obtenerRecetasFavoritas
    // ======================================================
    @Test
    void obtenerRecetasFavoritas_ok() {
        when(recetaRepository.findFavoritasByUsuarioId(10L))
                .thenReturn(Collections.emptyList());

        recetaService.obtenerRecetasFavoritas(10L);

        verify(recetaRepository).findFavoritasByUsuarioId(10L);
    }

    // ======================================================
    // guardarReceta
    // ======================================================
    @Test
    void guardarReceta_ok() {
        Receta receta = new Receta();
        recetaService.guardarReceta(receta);

        verify(recetaRepository).save(receta);
    }

    // ======================================================
    // eliminarReceta
    // ======================================================
    @Test
    void eliminarReceta_ok() {
        recetaService.eliminarReceta(5L);

        verify(recetaRepository).deleteById(5L);
    }
}
