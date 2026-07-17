package ec.edu.ups.icc.fundamentos01.security.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/*
 * Configuración general de OpenAPI.
 *
 * Esta clase personaliza la información que aparece
 * en Swagger UI:
 * - título
 * - versión
 * - descripción
 * - servidor base
 * - esquema de seguridad JWT
 */
@Configuration
public class OpenApiConfig {

    /*
     * Nombre del esquema de seguridad.
     *
     * Este nombre se usará luego en @SecurityRequirement.
     */
    public static final String SECURITY_SCHEME_NAME = "bearerAuth";

    @Bean
    public OpenAPI customOpenAPI() {

        /*
         * Información general de la API.
         */
        Info info = new Info()
                .title("API de Programación y Plataformas Web")
                .version("1.0.0")
                .description("""
                        Documentación interactiva de la API REST desarrollada en Spring Boot.
                        
                        Esta API incluye:
                        - autenticación con JWT
                        - refresh token
                        - autorización por roles
                        - validación de ownership
                        - paginación
                        - manejo global de errores
                        """);

        /*
         * Servidor base.
         *
         * Si el proyecto usa context-path /api, se coloca /api.
         * Esto permite que Swagger construya correctamente las rutas.
         */
        Server localServer = new Server()
                .url("/api")
                .description("Servidor local");

        /*
         * Esquema de seguridad Bearer JWT.
         *
         * Esto habilita el botón Authorize en Swagger UI.
         */
        SecurityScheme bearerScheme = new SecurityScheme()
                .name(SECURITY_SCHEME_NAME)
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .description("Ingrese el JWT generado en /auth/login");

        Components components = new Components()
                .addSecuritySchemes(SECURITY_SCHEME_NAME, bearerScheme);

        return new OpenAPI()
                .info(info)
                .servers(List.of(localServer))
                .components(components);
    }
}
