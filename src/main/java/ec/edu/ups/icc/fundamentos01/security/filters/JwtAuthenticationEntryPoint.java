package ec.edu.ups.icc.fundamentos01.security.filters;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

/*
 * JwtAuthenticationEntryPoint maneja los errores de autenticación.
 *
 * Se ejecuta cuando un usuario intenta acceder a un endpoint protegido
 * sin enviar token o con un token inválido.
 *
 * Este componente responde con JSON en lugar de mostrar una página HTML
 * de error por defecto de Spring Security.
 */
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    /*
     * Logger para registrar errores de autenticación.
     */
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationEntryPoint.class);

    /*
     * Método que se ejecuta automáticamente cuando Spring Security
     * detecta que la petición no está autenticada.
     *
     * Ejemplos:
     * - No hay token.
     * - Token expirado.
     * - Token malformado.
     * - Token con firma inválida.
     */
    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException, ServletException {

        logger.error("Error de autenticación: {}", authException.getMessage());

        /*
         * Se configura el código HTTP 401 Unauthorized.
         */
        response.setStatus(HttpStatus.UNAUTHORIZED.value());

        /*
         * Se indica que la respuesta será JSON.
         */
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        /*
         * Se construye manualmente el JSON para evitar depender de ObjectMapper.
         */
        String jsonResponse = """
                {
                  "timestamp": "%s",
                  "status": 401,
                  "error": "Unauthorized",
                  "message": "Token de autenticación inválido o ausente",
                  "path": "%s",
                  "details": null
                }
                """.formatted(
                LocalDateTime.now(),
                request.getRequestURI()
        );

        /*
         * Se escribe la respuesta directamente en el cuerpo HTTP.
         */
        response.getWriter().write(jsonResponse);
    }
}