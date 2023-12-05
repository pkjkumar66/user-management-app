package com.example.app.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class ResourceSecurityConfig {

    @Bean
    public InMemoryUserDetailsManager UserDetailsService() {

        UserDetails user = User.builder()
                .username("user")
                .password("{bcrypt}$2a$10$PLaRbuq6K8pE0PXbZ6DciePX6G67Rs8p5ElprGDD1yq2X4cdbZa5i")
                .roles("USER")
                .build();

        UserDetails admin = User.builder()
                .username("admin")
                .password("{bcrypt}$2a$10$PLaRbuq6K8pE0PXbZ6DciePX6G67Rs8p5ElprGDD1yq2X4cdbZa5i")
                .roles("USER", "ADMIN")
                .build();

        return new InMemoryUserDetailsManager(user, admin);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.authorizeHttpRequests(configurer ->
                configurer
                        .requestMatchers(HttpMethod.GET, "/api/v1/users/all").hasRole("USER")
                        .requestMatchers(HttpMethod.GET, "/api/v1/users/**").hasRole("USER")
                        .requestMatchers(HttpMethod.POST, "/api/v1/users/add").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/users/update/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/users/delete/**").hasRole("ADMIN")
        );

        // use HTTP Basic authentication
        http.httpBasic(Customizer.withDefaults());

        // disable Cross Site Request Forgery (CSRF)
        // in general, not required for stateless REST APIs that use POST, PUT, DELETE and/or PATCH
        http.csrf(csrf -> csrf.disable());

        return http.build();
    }
}













