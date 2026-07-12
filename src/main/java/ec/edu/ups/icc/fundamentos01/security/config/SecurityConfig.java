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
 * SecurityConfig contiene la configuración principal de Spring Security.
 *
 * En la práctica anterior se protegieron todos los endpoints con JWT.
 * En esta práctica se habilita la protección por roles usando @PreAuthorize.
 *
 * @EnableMethodSecurity:
 * - Habilita anotaciones de seguridad en métodos.
 * - Permite usar @PreAuthorize.
 * - Permite expresiones como hasRole('ADMIN') o hasAnyRole('ADMIN', 'MODERATOR').
 */
@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    /*
     * Servicio usado por Spring Security para cargar usuarios desde la base de datos.
     */
    private final UserDetailsServiceImpl userDetailsService;

    /*
     * Componente encargado de responder 401 Unauthorized en formato JSON
     * cuando un endpoint protegido se consume sin token o con token inválido.
     */
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    /*
     * Filtro que valida el token JWT en cada petición HTTP.
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
     * - Se desactiva CSRF porque esta API trabaja con JWT y no con sesiones web.
     *
     * sessionCreationPolicy.STATELESS:
     * - Indica que el servidor no guardará sesión.
     * - Cada request debe traer el token JWT.
     *
     * requestMatchers("/auth/**").permitAll():
     * - Login y registro son públicos.
     *
     * anyRequest().authenticated():
     * - Todos los demás endpoints requieren token válido.
     *
     * La protección por rol específico se hará en el controlador
     * usando @PreAuthorize.
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
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/status/**").permitAll()
                        .requestMatchers("/actuator/**").permitAll()
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
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);

        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }

    /*
     * AuthenticationManager se usa en AuthService para validar
     * email y contraseña durante el login.
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration
    ) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /*
     * BCrypt se usa para cifrar contraseñas.
     *
     * Nunca se deben guardar contraseñas en texto plano.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}