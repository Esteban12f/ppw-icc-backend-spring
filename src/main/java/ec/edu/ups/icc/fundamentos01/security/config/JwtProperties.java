package ec.edu.ups.icc.fundamentos01.security.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/*
 * Clase de configuración para leer las propiedades JWT
 * definidas en application.yml.
 *
 * La anotación @ConfigurationProperties(prefix = "jwt")
 * permite mapear automáticamente los valores:
 *
 * jwt.secret
 * jwt.expiration
 * jwt.refresh-expiration
 * jwt.issuer
 * jwt.header
 * jwt.prefix
 *
 * hacia los atributos de esta clase.
 */
@Configuration
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    /*
     * Clave secreta usada para firmar y validar los tokens JWT.
     *
     * Debe tener al menos 256 bits para usar HS256 de forma segura.
     */
    private String secret;

    /*
     * Tiempo de expiración del access token.
     *
     * Está expresado en milisegundos.
     * Ejemplo: 1800000 = 30 minutos.
     */
    private Long expiration;

    /*
     * Tiempo de expiración del refresh token.
     *
     * En esta práctica se define aunque no necesariamente
     * se implemente todavía el endpoint de refresh.
     */
    private Long refreshExpiration;

    /*
     * Identificador de quién emite el token.
     *
     * Sirve para saber que el token fue generado
     * por esta API.
     */
    private String issuer;

    /*
     * Header HTTP donde se enviará el token.
     *
     * Normalmente se usa:
     * Authorization
     */
    private String header;

    /*
     * Prefijo del token.
     *
     * Normalmente se usa:
     * Bearer
     *
     * Es importante conservar el espacio:
     * "Bearer "
     */
    private String prefix;

    /*
     * Constructor vacío requerido por Spring.
     */
    public JwtProperties() {
    }

    /*
     * Getters y setters usados por Spring para asignar
     * los valores leídos desde application.yml.
     */
    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public Long getExpiration() {
        return expiration;
    }

    public void setExpiration(Long expiration) {
        this.expiration = expiration;
    }

    public Long getRefreshExpiration() {
        return refreshExpiration;
    }

    public void setRefreshExpiration(Long refreshExpiration) {
        this.refreshExpiration = refreshExpiration;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }
}
