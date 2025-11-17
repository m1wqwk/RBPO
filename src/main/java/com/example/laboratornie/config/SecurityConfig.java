package com.example.laboratornie.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        CsrfTokenRequestAttributeHandler requestHandler = new CsrfTokenRequestAttributeHandler();
        requestHandler.setCsrfRequestAttributeName("_csrf");

        http
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                )
                .csrf(csrf -> csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        .csrfTokenRequestHandler(requestHandler)
                        .ignoringRequestMatchers("/api/**")
                )
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/").permitAll()
                        .requestMatchers("/api/auth/register").hasRole("ADMIN")
                        .requestMatchers("/api/bookings/**").hasAnyRole("USER", "MANAGER", "ADMIN")
                        .requestMatchers("/api/guests/email/**").hasAnyRole("USER", "MANAGER", "ADMIN")
                        .requestMatchers("/api/guests/passport/**").hasAnyRole("USER", "MANAGER", "ADMIN")
                        .requestMatchers("/api/rooms/**").hasAnyRole("MANAGER", "ADMIN")
                        .requestMatchers("/api/payments/**").hasAnyRole("MANAGER", "ADMIN")
                        .requestMatchers("/api/guests/**").hasAnyRole("MANAGER", "ADMIN")
                        .requestMatchers("/api/hotels/**").hasRole("ADMIN")
                        .requestMatchers("/api/auth/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}