package com.inventra.inventra1.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Disabled for development simplicity
                .authorizeHttpRequests(auth -> auth
                        // 1. Publicly accessible routes to prevent redirect loops
                        .requestMatchers("/login", "/signup", "/forgot-password", "/reset-password", "/css/**", "/images/**").permitAll()

                        // 2. Role-based access control
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/manager/**").hasRole("MANAGER")
                        .requestMatchers("/staff/**").hasRole("STAFF")

                        // 3. Any other request requires authentication
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login") // Your custom login page
                        .loginProcessingUrl("/signin") // The URL targeted by your Sign In button
                        .defaultSuccessUrl("/dashboard", true) // Redirects to DashboardController logic
                        .failureUrl("/login?error=true")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                );
        return http.build();
    }
}