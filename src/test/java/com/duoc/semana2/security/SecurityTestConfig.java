package com.duoc.semana2.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
public class SecurityTestConfig {

    @Bean
    public UserDetailsService userDetailsService() {
        return new InMemoryUserDetailsManager(
                User.withUsername("admin").password("1234").roles("ADMIN").build(),
                User.withUsername("user").password("1234").roles("USER").build()
        );
    }

    @Bean
    public JwtService jwtService() {
        // Valores de prueba (NO se usan para firmar JWT real)
        String fakeSecret = "TEST_SECRET_KEY_12345678901234567890";
        long expiration = 3600000L; // 1 hora para tests

        return new JwtService(fakeSecret, expiration);
    }
}
