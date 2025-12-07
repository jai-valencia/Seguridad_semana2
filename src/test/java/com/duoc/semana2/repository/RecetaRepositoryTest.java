package com.duoc.semana2.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;

import com.duoc.semana2.model.Receta;
import com.duoc.semana2.model.Usuario;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;

@DataJpaTest
class RecetaRepositoryTest {

    @Autowired
    private RecetaRepository recetaRepository;

    private Receta r1;
    private Receta r2;

    @BeforeEach
    void setup() {

        r1 = new Receta();
        r1.setNombre("Pasta");
        r1.setTipoCocina("Italiana");
        r1.setIngredientes("Tomate");
        r1.setPais("Italia");
        r1.setDificultad("Media");
        r1.setVistas(100);
        r1.setFechaCreacion(LocalDateTime.now().minusDays(10));

        r2 = new Receta();
        r2.setNombre("Sushi");
        r2.setTipoCocina("Japonesa");
        r2.setIngredientes("Arroz");
        r2.setPais("Japón");
        r2.setDificultad("Alta");
        r2.setVistas(20);
        r2.setFechaCreacion(LocalDateTime.now().minusDays(1));

        recetaRepository.save(r1);
        recetaRepository.save(r2);
    }

    @Test
    void buscarConFiltros_filtraPorNombre() {
        List<Receta> resultado = recetaRepository.buscarConFiltros(
                "pas", null, null, null, null);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getNombre()).isEqualTo("Pasta");
    }

    @Test
    void buscarConFiltros_sinFiltrosRetornaTodas() {
        List<Receta> resultado = recetaRepository.buscarConFiltros(
                null, null, null, null, null);

        assertThat(resultado).hasSize(2);
    }

    @Test
    void findTopRecetas_retornarMasVistas() {
        List<Receta> resultado = recetaRepository.findTopRecetas(PageRequest.of(0, 1));

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getNombre()).isEqualTo("Pasta");
    }

    @Test
    void findRecentRecetas_retornarMasReciente() {
        List<Receta> resultado = recetaRepository.findRecentRecetas(PageRequest.of(0, 1));

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getNombre()).isEqualTo("Sushi");
    }

    @Test
    void findByUsuarioId_retornarRecetasDelUsuario() {

        Usuario usuario = new Usuario();
        usuario.setUsername("user1");
        usuario.setPassword("1234");

        Receta receta = new Receta();
        receta.setNombre("Tacos");
        receta.setUsuario(usuario);

        recetaRepository.save(receta);

        List<Receta> resultado = recetaRepository.findByUsuarioId(usuario.getId());

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getNombre()).isEqualTo("Tacos");
    }

    @Test
    void findFavoritasByUsuarioId_retornarFavoritas() {

        Usuario usuario = new Usuario();
        usuario.setUsername("favUser");
        usuario.setPassword("pass");

        Receta receta = new Receta();
        receta.setNombre("Pizza");

        receta.setUsuariosQueFavoritearon(List.of(usuario));

        recetaRepository.save(receta);

        List<Receta> resultado = recetaRepository.findFavoritasByUsuarioId(usuario.getId());

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getNombre()).isEqualTo("Pizza");
    }
}
