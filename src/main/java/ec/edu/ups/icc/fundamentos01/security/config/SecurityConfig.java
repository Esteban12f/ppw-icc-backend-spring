package ec.edu.ups.icc.fundamentos01.security.config;

import ec.edu.ups.icc.fundamentos01.security.filters.JwtAuthenticationEntryPoint;
import ec.edu.ups.icc.fundamentos01.security.filters.JwtAuthenticationFilter;
import ec.edu.ups.icc.fundamentos01.security.services.UserDetailsServiceImpl;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/*
 * Configuración principal de seguridad.
 *
 * En esta práctica se agrega protección para Spring Boot Actuator:
 *
 * - /actuator/health será público para monitoreo.
 * - /actuator/** será solo para ADMIN.
 * - El resto de endpoints requiere autenticación JWT.
 */
@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final UserDetailsServiceImpl userDetailsService;

    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(
            UserDetailsServiceImpl userDetailsService,
            JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
            JwtAuthenticationFilter jwtAuthenticationFilter
    ) {
        this.userDetailsService = userDetailsService;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .exceptionHandling(exception ->
                        exception.authenticationEntryPoint(jwtAuthenticationEntryPoint)
                )
                .authorizeHttpRequests(auth -> auth
                        /*
                         * Endpoints públicos.
                         */
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/status/**").permitAll()

                        /*
                         * Health check público.
                         *
                         * Como el proyecto usa context-path /api,
                         * la URL real será:
                         *
                         * http://localhost:8080/api/actuator/health
                         */
                        .requestMatchers("/actuator/health").permitAll()

                        /*
                         * Los demás endpoints de actuator solo serán
                         * accesibles para usuarios con ROLE_ADMIN.
                         */
                        .requestMatchers("/actuator/**").hasRole("ADMIN")

                        /*
                         * Todos los demás endpoints requieren token JWT.
                         */
                        .anyRequest().authenticated()
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider =
                new DaoAuthenticationProvider(userDetailsService);

        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration
    ) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}