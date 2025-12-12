package com.duoc.semana2.service;

import com.duoc.semana2.model.Receta;
import com.duoc.semana2.repository.RecetaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecetaServiceTest {

    @Mock
    private RecetaRepository recetaRepository;

    @InjectMocks
    private RecetaService recetaService;

    private Receta r1;
    private Receta r2;

    @BeforeEach
    void setUp() {
        r1 = Receta.builder()
                .id(1L)
                .nombre("Pastel de Choclo")
                .tipoCocina("chilena")
                .pais("Chile")
                .dificultad("media")
                .vistas(10)
                .likes(2)
                .fechaCreacion(LocalDateTime.now().minusDays(2))
                .build();

        r2 = Receta.builder()
                .id(2L)
                .nombre("Pizza Artesanal")
                .tipoCocina("italiana")
                .pais("Italia")
                .dificultad("baja")
                .vistas(50)
                .likes(15)
                .fechaCreacion(LocalDateTime.now())
                .build();
    }

    // ---------------------------------------------------------
    // obtenerPorId
    // ---------------------------------------------------------
    @Test
    void obtenerPorId_cuandoExiste_retornaReceta() {
        when(recetaRepository.findById(1L)).thenReturn(Optional.of(r1));

        Receta resultado = recetaService.obtenerPorId(1L);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getNombre()).isEqualTo("Pastel de Choclo");
        verify(recetaRepository).findById(1L);
    }

    @Test
    void obtenerPorId_cuandoNoExiste_retornaNull() {
        when(recetaRepository.findById(99L)).thenReturn(Optional.empty());

        Receta resultado = recetaService.obtenerPorId(99L);

        assertThat(resultado).isNull();
        verify(recetaRepository).findById(99L);
    }

    // ---------------------------------------------------------
    // obtenerTodasLasRecetas
    // ---------------------------------------------------------
    @Test
    void obtenerTodasLasRecetas_retornaLista() {
        when(recetaRepository.findAll()).thenReturn(Arrays.asList(r1, r2));

        List<Receta> resultado = recetaService.obtenerTodasLasRecetas();

        assertThat(resultado).hasSize(2);
        assertThat(resultado).containsExactly(r1, r2);
        verify(recetaRepository).findAll();
    }

    // ---------------------------------------------------------
    // buscarRecetas (delegando a buscarConFiltros)
    // ---------------------------------------------------------
    @Test
    void buscarRecetas_delegaEnRepositorio() {
        when(recetaRepository.buscarConFiltros(
                anyString(), anyString(), anyString(), anyString(), anyString()
        )).thenReturn(Collections.singletonList(r2));

        List<Receta> resultado = recetaService.buscarRecetas(
                "pizza", "italiana", "queso", "Italia", "baja"
        );

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0)).isEqualTo(r2);

        verify(recetaRepository).buscarConFiltros(
                eq("pizza"), eq("italiana"), eq("queso"), eq("Italia"), eq("baja")
        );
    }

    // ---------------------------------------------------------
    // obtenerRecetasPopulares
    // ---------------------------------------------------------
    @Test
    void obtenerRecetasPopulares_usaPageableYRetornaLista() {
        when(recetaRepository.findTopRecetas(any(Pageable.class)))
                .thenReturn(Arrays.asList(r2, r1));

        List<Receta> resultado = recetaService.obtenerRecetasPopulares(2);

        assertThat(resultado).hasSize(2);
        assertThat(resultado.get(0)).isEqualTo(r2);

        verify(recetaRepository).findTopRecetas(any(Pageable.class));
    }

    // ---------------------------------------------------------
    // obtenerRecetasRecientes
    // ---------------------------------------------------------
    @Test
    void obtenerRecetasRecientes_usaPageableYRetornaLista() {
        when(recetaRepository.findRecentRecetas(any(Pageable.class)))
                .thenReturn(Arrays.asList(r2, r1));

        List<Receta> resultado = recetaService.obtenerRecetasRecientes(5);

        assertThat(resultado).hasSize(2);
        assertThat(resultado.get(0)).isEqualTo(r2);

        verify(recetaRepository).findRecentRecetas(any(Pageable.class));
    }

    // ---------------------------------------------------------
    // obtenerRecetasPorUsuario
    // ---------------------------------------------------------
    @Test
    void obtenerRecetasPorUsuario_delegaEnRepositorio() {
        when(recetaRepository.findByUsuarioId(1L))
                .thenReturn(Arrays.asList(r1, r2));

        List<Receta> resultado = recetaService.obtenerRecetasPorUsuario(1L);

        assertThat(resultado).hasSize(2);
        verify(recetaRepository).findByUsuarioId(1L);
    }

    // ---------------------------------------------------------
    // obtenerRecetasFavoritas
    // ---------------------------------------------------------
    @Test
    void obtenerRecetasFavoritas_delegaEnRepositorio() {
        when(recetaRepository.findFavoritasByUsuarioId(1L))
                .thenReturn(Collections.singletonList(r2));

        List<Receta> resultado = recetaService.obtenerRecetasFavoritas(1L);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0)).isEqualTo(r2);
        verify(recetaRepository).findFavoritasByUsuarioId(1L);
    }

    // ---------------------------------------------------------
    // guardarReceta
    // ---------------------------------------------------------
    @Test
    void guardarReceta_invocaSaveEnRepositorio() {
        Receta nueva = Receta.builder()
                .nombre("Nueva Receta")
                .build();

        when(recetaRepository.save(any(Receta.class)))
                .thenAnswer(invocation -> {
                    Receta r = invocation.getArgument(0);
                    r.setId(10L);
                    return r;
                });

        recetaService.guardarReceta(nueva);

        ArgumentCaptor<Receta> captor = ArgumentCaptor.forClass(Receta.class);
        verify(recetaRepository).save(captor.capture());

        Receta enviada = captor.getValue();
        assertThat(enviada.getNombre()).isEqualTo("Nueva Receta");
    }

    // ---------------------------------------------------------
    // eliminarReceta
    // ---------------------------------------------------------
    @Test
    void eliminarReceta_invocaDeleteByIdEnRepositorio() {
        Long id = 5L;

        recetaService.eliminarReceta(id);

        verify(recetaRepository).deleteById(id);
    }
}
