package com.duoc.semana2.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import com.duoc.semana2.model.Usuario;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class UsuarioRepositoryTest {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @BeforeEach
    void setup() {
        Usuario u1 = new Usuario();
        u1.setUsername("jaime");
        u1.setPassword("1234");
        u1.setNombre("Jaime Valencia");
        u1.setEmail("jaime@test.com");

        Usuario u2 = new Usuario();
        u2.setUsername("miguel");
        u2.setPassword("abcd");
        u2.setNombre("Miguel Astorga");
        u2.setEmail("miguel@test.com");

        usuarioRepository.save(u1);
        usuarioRepository.save(u2);
    }

    @Test
    void findByUsername_existeUsuario() {
        Optional<Usuario> resultado = usuarioRepository.findByUsername("jaime");

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getEmail()).isEqualTo("jaime@test.com");
    }

    @Test
    void findByUsername_noExisteUsuario() {
        Optional<Usuario> resultado = usuarioRepository.findByUsername("desconocido");

        assertThat(resultado).isNotPresent();
    }
}
