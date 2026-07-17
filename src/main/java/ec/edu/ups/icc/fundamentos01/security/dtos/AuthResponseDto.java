package ec.edu.ups.icc.fundamentos01.security.dtos;

import java.util.Set;

/*
 * DTO de respuesta para login, register y refresh.
 *
 * token:
 * - representa el access token
 * - se usa en Authorization: Bearer <token>
 *
 * refreshToken:
 * - se usa solo en /auth/refresh
 * - no debe usarse para consumir endpoints protegidos
 */
public class AuthResponseDto {

    /*
     * Access token.
     *
     * Este token se usa para consumir endpoints protegidos.
     */
    private String token;

    /*
     * Refresh token.
     *
     * Este token se usa para renovar el access token
     * cuando expire.
     */
    private String refreshToken;

    /*
     * Tipo de token.
     *
     * Se mantiene Bearer porque el access token se envía como:
     * Authorization: Bearer <token>
     */
    private String type = "Bearer";

    /*
     * ID del usuario autenticado.
     */
    private Long userId;

    /*
     * Nombre del usuario autenticado.
     */
    private String name;

    /*
     * Email del usuario autenticado.
     */
    private String email;

    /*
     * Roles del usuario autenticado.
     */
    private Set<String> roles;

    public AuthResponseDto() {
    }

    public AuthResponseDto(
            String token,
            String refreshToken,
            Long userId,
            String name,
            String email,
            Set<String> roles
    ) {
        this.token = token;
        this.refreshToken = refreshToken;
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.roles = roles;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

	public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    
    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }
}