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
 * Si no hay token o el token es inválido, la petición continúa
 * sin usuario autenticado y Spring Security devolverá 401
 * si el endpoint está protegido.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    /*
     * Logger para depuración y errores.
     */
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    /*
     * Utilidad para validar y leer JWT.
     */
    private final JwtUtil jwtUtil;

    /*
     * Servicio que carga usuarios desde la base de datos.
     */
    private final UserDetailsServiceImpl userDetailsService;

    /*
     * Propiedades JWT:
     * - header
     * - prefix
     */
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
     * Método principal del filtro.
     *
     * Se ejecuta una vez por cada request.
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
             * Si existe token y es válido, se autentica al usuario.
             */
            if (StringUtils.hasText(jwt) && jwtUtil.validateToken(jwt)) {

                /*
                 * Paso 3:
                 * Obtener el email guardado dentro del token.
                 */
                String email = jwtUtil.getEmailFromToken(jwt);

                /*
                 * Paso 4:
                 * Cargar el usuario desde PostgreSQL.
                 */
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);

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
                 * Agregar detalles de la request como IP y sessionId.
                 */
                authentication.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                /*
                 * Paso 7:
                 * Guardar la autenticación en el SecurityContext.
                 *
                 * Desde este momento, Spring considera que el usuario
                 * está autenticado para esta petición.
                 */
                SecurityContextHolder.getContext().setAuthentication(authentication);

                logger.debug("Usuario autenticado: {}", email);
            }

        } catch (Exception ex) {
            /*
             * Si ocurre un error al procesar el token,
             * no se lanza la excepción.
             *
             * Se deja la petición sin autenticación y Spring Security
             * se encargará de responder 401 si corresponde.
             */
            logger.error("No se pudo establecer la autenticación del usuario", ex);
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

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(jwtProperties.getPrefix())) {
            return bearerToken.substring(jwtProperties.getPrefix().length());
        }

        return null;
    }
}