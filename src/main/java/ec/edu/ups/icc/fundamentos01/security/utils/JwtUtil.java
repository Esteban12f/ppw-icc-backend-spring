package ec.edu.ups.icc.fundamentos01.security.utils;

import ec.edu.ups.icc.fundamentos01.security.config.JwtProperties;
import ec.edu.ups.icc.fundamentos01.security.services.UserDetailsImpl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;

import java.util.Date;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

/*
 * JwtUtil se encarga de generar, leer y validar tokens JWT.
 *
 * En esta práctica se diferencian dos tipos de token:
 * - access token
 * - refresh token
 *
 * Para diferenciarlos se agrega un claim llamado "type".
 */
@Component
public class JwtUtil {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);

    /*
     * Claim utilizado para diferenciar el tipo de token.
     */
    private static final String TOKEN_TYPE_CLAIM = "type";

    /*
     * Valor usado para access tokens.
     */
    private static final String ACCESS_TOKEN_TYPE = "access";

    /*
     * Valor usado para refresh tokens.
     */
    private static final String REFRESH_TOKEN_TYPE = "refresh";

    private final JwtProperties jwtProperties;

    private final SecretKey key;

    public JwtUtil(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        this.key = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes());
    }

    /*
     * Genera un access token desde Authentication.
     *
     * Este método se usa en login.
     */
    public String generateAccessToken(Authentication authentication) {
        UserDetailsImpl userPrincipal =
                (UserDetailsImpl) authentication.getPrincipal();

        return buildToken(
                userPrincipal,
                jwtProperties.getExpiration(),
                ACCESS_TOKEN_TYPE
        );
    }

    /*
     * Genera un access token desde UserDetailsImpl.
     *
     * Este método se usa en register y refresh.
     */
    public String generateAccessTokenFromUserDetails(
            UserDetailsImpl userDetails
    ) {
        return buildToken(
                userDetails,
                jwtProperties.getExpiration(),
                ACCESS_TOKEN_TYPE
        );
    }

    /*
     * Genera un refresh token.
     *
     * Este token dura más tiempo y solo debe usarse en:
     * POST /api/auth/refresh
     */
    public String generateRefreshToken(
            UserDetailsImpl userDetails
    ) {
        return buildToken(
                userDetails,
                jwtProperties.getRefreshExpiration(),
                REFRESH_TOKEN_TYPE
        );
    }

    /*
     * Método centralizado para construir tokens JWT.
     *
     * tokenType puede ser:
     * - access
     * - refresh
     */
    private String buildToken(
            UserDetailsImpl userDetails,
            Long expirationMs,
            String tokenType
    ) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationMs);

        String roles = userDetails.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        return Jwts.builder()
                .subject(String.valueOf(userDetails.getId()))
                .claim("email", userDetails.getEmail())
                .claim("name", userDetails.getName())
                .claim("roles", roles)
                .claim(TOKEN_TYPE_CLAIM, tokenType)
                .issuer(jwtProperties.getIssuer())
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(key, Jwts.SIG.HS256)
                .compact();
    }

    /*
     * Mantiene compatibilidad con el nombre anterior.
     *
     * Si en AuthService todavía se llama generateToken(),
     * este método seguirá funcionando como access token.
     */
    public String generateToken(Authentication authentication) {
        return generateAccessToken(authentication);
    }

    /*
     * Mantiene compatibilidad con el nombre anterior.
     */
    public String generateTokenFromUserDetails(
            UserDetailsImpl userDetails
    ) {
        return generateAccessTokenFromUserDetails(userDetails);
    }

    /*
     * Extrae el ID del usuario desde el subject.
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = getClaims(token);

        return Long.parseLong(claims.getSubject());
    }

    /*
     * Extrae el email desde cualquier token válido.
     */
    public String getEmailFromToken(String token) {
        Claims claims = getClaims(token);

        return claims.get("email", String.class);
    }

    /*
     * Extrae el tipo del token.
     *
     * Valores esperados:
     * - access
     * - refresh
     */
    public String getTokenType(String token) {
        Claims claims = getClaims(token);

        return claims.get(TOKEN_TYPE_CLAIM, String.class);
    }

    /*
     * Valida firma, formato y expiración del token.
     *
     * No valida si es access o refresh.
     * Solo valida que el JWT sea técnicamente correcto.
     */
    public boolean validateToken(String authToken) {
        try {
            getClaims(authToken);

            return true;

        } catch (SignatureException ex) {
            logger.error("Firma JWT inválida: {}", ex.getMessage());

        } catch (MalformedJwtException ex) {
            logger.error("Token JWT malformado: {}", ex.getMessage());

        } catch (ExpiredJwtException ex) {
            logger.error("Token JWT expirado: {}", ex.getMessage());

        } catch (UnsupportedJwtException ex) {
            logger.error("Token JWT no soportado: {}", ex.getMessage());

        } catch (IllegalArgumentException ex) {
            logger.error("JWT claims string está vacío: {}", ex.getMessage());
        }

        return false;
    }

    /*
     * Valida que el token sea un access token.
     *
     * Este método debe usarse en JwtAuthenticationFilter.
     */
    public boolean validateAccessToken(String token) {
        return validateToken(token)
                && ACCESS_TOKEN_TYPE.equals(getTokenType(token));
    }

    /*
     * Valida que el token sea un refresh token.
     *
     * Este método debe usarse en /auth/refresh.
     */
    public boolean validateRefreshToken(String token) {
        return validateToken(token)
                && REFRESH_TOKEN_TYPE.equals(getTokenType(token));
    }

    /*
     * Obtiene los claims del token.
     *
     * Si el token es inválido, este método lanza excepción.
     */
    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}