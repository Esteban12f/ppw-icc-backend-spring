package ec.edu.ups.icc.fundamentos01.security.dtos;

import java.util.Set;

/*
 * DTO de respuesta para login y register.
 *
 * Este DTO se devuelve cuando el usuario inicia sesión
 * o se registra correctamente.
 *
 * Incluye:
 * - Token JWT generado por el backend.
 * - Tipo de token, normalmente Bearer.
 * - Datos básicos del usuario autenticado.
 * - Roles asignados al usuario.
 */
public class AuthResponseDto {

    /*
     * Token JWT generado por el servidor.
     *
     * Este token debe enviarse luego en cada petición protegida
     * dentro del header Authorization.
     */
    private String token;

    /*
     * Tipo de token.
     *
     * Se usa "Bearer" porque es el estándar usado para enviar JWT:
     *
     * Authorization: Bearer token
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
     * Roles del usuario.
     *
     * Ejemplo:
     * ROLE_USER
     * ROLE_ADMIN
     */
    private Set<String> roles;

    /*
     * Constructor vacío requerido para serialización/deserialización.
     */
    public AuthResponseDto() {
    }

    /*
     * Constructor usado para devolver una respuesta completa
     * después del login o register.
     */
    public AuthResponseDto(
            String token,
            Long userId,
            String name,
            String email,
            Set<String> roles
    ) {
        this.token = token;
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.roles = roles;
    }

    /*
     * Getters y setters.
     */
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
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