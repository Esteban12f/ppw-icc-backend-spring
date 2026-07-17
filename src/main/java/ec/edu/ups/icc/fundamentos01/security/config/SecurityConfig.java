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
 * En esta práctica se permite el acceso público a Swagger UI
 * y al documento OpenAPI para que la documentación pueda visualizarse
 * sin token.
 *
 * Importante:
 * Aunque Swagger UI sea público, los endpoints protegidos siguen
 * requiriendo JWT cuando se intentan ejecutar.
 */
@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    /*
     * Servicio usado por Spring Security para cargar usuarios.
     */
    private final UserDetailsServiceImpl userDetailsService;

    /*
     * Componente que devuelve errores 401 en formato JSON.
     */
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    /*
     * Filtro que valida access tokens JWT en cada request protegida.
     */
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

    /*
     * SecurityFilterChain define las reglas generales de seguridad.
     *
     * csrf.disable():
     * - Se desactiva CSRF porque la API usa JWT.
     *
     * sessionCreationPolicy.STATELESS:
     * - El servidor no guarda sesiones.
     *
     * /auth/**:
     * - Login, registro, refresh y logout son públicos.
     *
     * /swagger-ui/**, /swagger-ui.html y /v3/api-docs/**:
     * - Se permiten públicamente para poder ver la documentación.
     *
     * /actuator/health:
     * - Se permite para monitoreo.
     *
     * /actuator/**:
     * - Se deja para ADMIN.
     *
     * anyRequest().authenticated():
     * - Todo lo demás requiere token JWT.
     */
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
                         * Endpoints públicos de autenticación.
                         */
                        .requestMatchers("/auth/**").permitAll()

                        /*
                         * Endpoints públicos de Swagger y OpenAPI.
                         *
                         * Si el proyecto usa context-path /api,
                         * NO se coloca /api aquí.
                         *
                         * Spring Security evalúa rutas internas:
                         * /swagger-ui/**
                         * /v3/api-docs/**
                         */
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**"
                        ).permitAll()

                        /*
                         * Endpoints públicos de estado y monitoreo.
                         */
                        .requestMatchers("/status/**").permitAll()
                        .requestMatchers("/actuator/health").permitAll()

                        /*
                         * Otros endpoints de actuator solo para ADMIN.
                         */
                        .requestMatchers("/actuator/**").hasRole("ADMIN")

                        /*
                         * Todo lo demás requiere autenticación.
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

    /*
     * DaoAuthenticationProvider usa:
     * - UserDetailsServiceImpl para buscar usuarios.
     * - PasswordEncoder para comparar contraseñas cifradas.
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider =
                new DaoAuthenticationProvider(userDetailsService);

        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }

    /*
     * AuthenticationManager se usa en AuthService durante el login.
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration
    ) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /*
     * BCrypt cifra contraseñas de usuarios.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}