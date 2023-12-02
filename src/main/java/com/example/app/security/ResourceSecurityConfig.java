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

        UserDetails pankaj = User.builder()
                .username("pankaj")
                .password("{noop}test123")
                .roles("EMPLOYEE")
                .build();

        UserDetails sharad = User.builder()
                .username("sharad")
                .password("{noop}test123")
                .roles("EMPLOYEE", "MANAGER")
                .build();

        UserDetails admin = User.builder()
                .username("admin")
                .password("{noop}test123")
                .roles("EMPLOYEE", "MANAGER", "ADMIN")
                .build();

        return new InMemoryUserDetailsManager(pankaj, sharad, admin);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.authorizeHttpRequests(configurer ->
                configurer
                        .requestMatchers(HttpMethod.GET, "/api/users").hasRole("EMPLOYEE")
                        .requestMatchers(HttpMethod.GET, "/api/users/**").hasRole("EMPLOYEE")
                        .requestMatchers(HttpMethod.POST, "/api/users").hasRole("MANAGER")
                        .requestMatchers(HttpMethod.PUT, "/api/users/**").hasRole("MANAGER")
                        .requestMatchers(HttpMethod.DELETE, "/api/users/**").hasRole("ADMIN")
        );

        // use HTTP Basic authentication
        http.httpBasic(Customizer.withDefaults());

        // disable Cross Site Request Forgery (CSRF)
        // in general, not required for stateless REST APIs that use POST, PUT, DELETE and/or PATCH
        http.csrf(csrf -> csrf.disable());

        return http.build();
    }
}













