package ec.edu.ups.icc.fundamentos01.security.filters;

import ec.edu.ups.icc.fundamentos01.security.config.JwtProperties;
import ec.edu.ups.icc.fundamentos01.security.services.UserDetailsServiceImpl;
import ec.edu.ups.icc.fundamentos01.security.utils.JwtUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

/*
 * JwtAuthenticationFilter es el filtro que se ejecuta
 * en cada petición HTTP.
 *
 * Su responsabilidad es:
 * 1. Leer el header Authorization.
 * 2. Extraer el token JWT.
 * 3. Validar el token.
 * 4. Cargar el usuario desde la base de datos.
 * 5. Guardar la autenticación en el SecurityContext.
 *
 * En esta práctica el filtro solo debe aceptar access tokens.
 *
 * Si un cliente intenta usar un refresh token en:
 *
 * Authorization: Bearer <refresh-token>
 *
 * el backend debe rechazarlo.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger =
            LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtUtil jwtUtil;

    private final UserDetailsServiceImpl userDetailsService;

    private final JwtProperties jwtProperties;

    public JwtAuthenticationFilter(
            JwtUtil jwtUtil,
            UserDetailsServiceImpl userDetailsService,
            JwtProperties jwtProperties
    ) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.jwtProperties = jwtProperties;
    }

    /*
     * Evita que el filtro se ejecute en endpoints públicos.
     *
     * /auth/** es público porque login, register, refresh y logout
     * no se validan con access token.
     *
     * /auth/refresh se valida internamente usando el refresh token
     * recibido en el body.
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();

        return path.equals("/actuator/health")
                || path.startsWith("/auth/")
                || path.startsWith("/status/");
    }

    /*
     * Método principal del filtro.
     *
     * Se ejecuta una vez por cada request protegida.
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        try {
            /*
             * Paso 1:
             * Extraer JWT desde el header Authorization.
             */
            String jwt = getJwtFromRequest(request);

            /*
             * Paso 2:
             * Si existe token y es un access token válido,
             * se autentica al usuario.
             *
             * Antes se usaba:
             * jwtUtil.validateToken(jwt)
             *
             * Ahora se usa:
             * jwtUtil.validateAccessToken(jwt)
             *
             * Esto evita que un refresh token sea usado
             * como si fuera access token.
             */
            if (StringUtils.hasText(jwt) && jwtUtil.validateAccessToken(jwt)) {

                /*
                 * Paso 3:
                 * Obtener el email guardado dentro del token.
                 */
                String email = jwtUtil.getEmailFromToken(jwt);

                /*
                 * Paso 4:
                 * Cargar el usuario desde PostgreSQL.
                 */
                UserDetails userDetails =
                        userDetailsService.loadUserByUsername(email);

                /*
                 * Paso 5:
                 * Crear objeto de autenticación de Spring Security.
                 *
                 * No se usa contraseña aquí porque el JWT ya fue validado.
                 */
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                /*
                 * Paso 6:
                 * Agregar detalles de la request.
                 */
                authentication.setDetails(
                        new WebAuthenticationDetailsSource()
                                .buildDetails(request)
                );

                /*
                 * Paso 7:
                 * Guardar la autenticación en el SecurityContext.
                 */
                SecurityContextHolder
                        .getContext()
                        .setAuthentication(authentication);

                logger.debug("Usuario autenticado: {}", email);
            }

        } catch (Exception ex) {
            /*
             * Si ocurre un error al procesar el token,
             * no se lanza la excepción.
             *
             * Se deja la petición sin autenticación y Spring Security
             * responderá 401 si el endpoint está protegido.
             */
            logger.error(
                    "No se pudo establecer la autenticación del usuario",
                    ex
            );
        }

        /*
         * Continúa con el resto de filtros y luego con el controlador.
         */
        filterChain.doFilter(request, response);
    }

    /*
     * Extrae el JWT desde el header configurado.
     *
     * Ejemplo:
     *
     * Authorization: Bearer eyJhbGci...
     *
     * Retorna solamente:
     *
     * eyJhbGci...
     */
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(jwtProperties.getHeader());

        if (StringUtils.hasText(bearerToken)
                && bearerToken.startsWith(jwtProperties.getPrefix())) {
            return bearerToken.substring(jwtProperties.getPrefix().length());
        }

        return null;
    }
}