package com.minero.logistica.config;

import com.minero.logistica.security.JwtAuthenticationEntryPoint;
import com.minero.logistica.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {
    
    private final UserDetailsService userDetailsService;
    private final JwtAuthenticationEntryPoint unauthorizedHandler;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    
    private static final String[] PUBLIC_URLS = {
        "/api/auth/login",
        "/api/auth/registro",
        "/swagger-ui/**",
        "/swagger-ui.html",
        "/v3/api-docs/**",
        "/swagger-resources/**",
        "/webjars/**"
    };
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }
    
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Cambiar de "*" a la URL específica
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:4200"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(AbstractHttpConfigurer::disable)
            .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(PUBLIC_URLS).permitAll()
                // Usuarios - solo ADMIN
                .requestMatchers("/api/usuarios/**").hasRole("ADMIN")
                // Productos - todos los autenticados
                .requestMatchers("/api/productos/**").authenticated()
                // Proveedores
                .requestMatchers("/api/proveedores/**").hasAnyRole("ADMIN", "ASISTENTE_COMPRAS")
                // Solicitudes
                .requestMatchers("/api/solicitudes-pedido/**").hasAnyRole("ADMIN", "GERENTE", "ASISTENTE_ALMACEN")
                .requestMatchers("/api/solicitudes-pedido/aprobar/**").hasAnyRole("ADMIN", "GERENTE")
                // Cotizaciones
                .requestMatchers("/api/cotizaciones/**").hasAnyRole("ADMIN", "GERENTE", "ASISTENTE_COMPRAS")
                // Órdenes de Compra
                .requestMatchers("/api/ordenes-compra/**").hasAnyRole("ADMIN", "GERENTE", "ASISTENTE_COMPRAS")
                // Recepciones
                .requestMatchers("/api/recepciones/**").hasAnyRole("ADMIN", "JEFE_ALMACEN", "ASISTENTE_ALMACEN")
                // Órdenes de Salida
                .requestMatchers("/api/ordenes-salida/**").hasAnyRole("ADMIN", "JEFE_ALMACEN", "ASISTENTE_ALMACEN")
                // Kardex
                .requestMatchers("/api/kardex/**").hasAnyRole("ADMIN", "JEFE_ALMACEN", "ASISTENTE_ALMACEN")
                // Dashboard
                .requestMatchers("/api/dashboard/**").hasAnyRole("ADMIN", "GERENTE", "JEFE_ALMACEN")
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
}