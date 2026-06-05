package com.icesi.bu_app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.icesi.bu_app.security.CustomUserDetailsService;
import com.icesi.bu_app.security.filters.JwtAuthenticationFilter;
import com.icesi.bu_app.service.IJwtService;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class AppConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(
            CustomUserDetailsService customUserDetailsService,
            PasswordEncoder passwordEncoder) {

        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(customUserDetailsService);

        provider.setPasswordEncoder(passwordEncoder);

        return provider;
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(IJwtService jwtService,
            CustomUserDetailsService customUserDetailsService) {
        return new JwtAuthenticationFilter(jwtService, customUserDetailsService);
    }

    @Bean
    @Order(1)
    public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationProvider authenticationProvider)
            throws Exception {

        return http
                .securityMatcher("/mvc/**")
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/mvc/public/**").permitAll()
                        .requestMatchers("/mvc/auth/login", "/css/**", "/js/**").permitAll()
                        .requestMatchers("/h2-console/**").permitAll()
                        .anyRequest().authenticated())
                .formLogin(form -> form
                        .loginPage("/mvc/auth/login") // URL personalizada para mostrar login
                        .loginProcessingUrl("/mvc/auth/login") // URL que procesa el login
                        .defaultSuccessUrl("/mvc/users", true) // Redirección después del login exitoso
                        .failureUrl("/mvc/auth/login?error") // Redirección en caso de error
                        .usernameParameter("username") // Nombre del campo username
                        .passwordParameter("password") // Nombre del campo password
                        .permitAll())
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/mvc/auth/login?logout")
                        .permitAll())

                .authenticationProvider(authenticationProvider)

                .build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain restSecurityFilterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter)
            throws Exception {
        return http
                .securityMatcher("/rest/**", "/ws/**")
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/rest/auth/**").permitAll()
                        .requestMatchers("/rest/public/**").permitAll()
                        .requestMatchers("/ws", "/ws/**").permitAll()
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        .requestMatchers("/h2-console/**").permitAll()
                        .anyRequest().authenticated())
                .headers(headers -> headers.frameOptions(frame -> frame.disable())) 
                .sessionManagement(t -> t.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) 
                .build();
    }

    private CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowCredentials(true); 
        configuration.addAllowedOriginPattern("*");
        configuration.addAllowedHeader("*"); 
        configuration.addAllowedMethod("*");
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}
