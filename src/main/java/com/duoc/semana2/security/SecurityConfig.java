package com.duoc.semana2.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

  @Bean
  public JwtAuthenticationFilter jwtAuthenticationFilter(JwtService jwtService,
                                                         UserDetailsService userDetailsService) {
    return new JwtAuthenticationFilter(jwtService, userDetailsService);
  }

  @Bean
  public PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }

  @Bean
  public DaoAuthenticationProvider authenticationProvider(UserDetailsService uds,
                                                          PasswordEncoder encoder) {
    DaoAuthenticationProvider p = new DaoAuthenticationProvider(uds);
    p.setPasswordEncoder(encoder);
    return p;
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
    return config.getAuthenticationManager();
  }

  
  @Bean
public SecurityFilterChain filterChain(HttpSecurity http,
                                       JwtAuthenticationFilter jwtFilter,
                                       DaoAuthenticationProvider authProvider) throws Exception {
  http
    .csrf(csrf -> csrf.disable())
    .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
    .authorizeHttpRequests(auth -> auth
      
      .requestMatchers("/", "/login", "/auth/login", "/auth/logout", "/health/**",
                       "/css/**", "/js/**", "/images/**", "/public/**").permitAll()
      
      .requestMatchers("/admin").hasRole("ADMIN")
      .requestMatchers("/user").hasAnyRole("USER","ADMIN")
      
      .requestMatchers("/api/secure/admin/**").hasRole("ADMIN")
      .requestMatchers("/api/secure/**").authenticated()
      
      .anyRequest().authenticated()
    )
    .authenticationProvider(authProvider)
    .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

  return http.build();
}

}
