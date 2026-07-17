package ec.edu.ups.icc.fundamentos01.security.entities;

import ec.edu.ups.icc.fundamentos01.core.entities.BaseEntity;
import ec.edu.ups.icc.fundamentos01.users.entities.UserEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

/*
 * Entidad JPA que representa un refresh token emitido por el sistema.
 *
 * El refresh token se guarda en base de datos para poder:
 * - validar que existe
 * - verificar si está revocado
 * - verificar si expiró
 * - invalidarlo durante logout
 * - rotarlo durante /auth/refresh
 */
@Entity
@Table(name = "refresh_tokens")
public class RefreshTokenEntity extends BaseEntity {

    /*
     * Usuario dueño del refresh token.
     *
     * Un usuario puede tener uno o varios refresh tokens,
     * dependiendo de la estrategia de sesión.
     *
     * En esta práctica se manejará una sesión activa por usuario,
     * revocando tokens anteriores al hacer login.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    /*
     * Valor del refresh token.
     *
     * En esta práctica se guarda el token completo para facilitar
     * el aprendizaje.
     *
     * En producción se recomienda guardar un hash del token,
     * no el token en texto plano.
     */
    @Column(nullable = false, unique = true, length = 1500)
    private String token;

    /*
     * Fecha y hora de expiración del refresh token.
     *
     * Aunque el JWT ya contiene expiración interna,
     * se guarda también en base de datos para facilitar consultas
     * y control del ciclo de vida del token.
     */
    @Column(nullable = false)
    private LocalDateTime expiresAt;

    /*
     * Indica si el refresh token fue revocado.
     *
     * Un refresh token revocado ya no puede usarse
     * para renovar sesión.
     */
    @Column(nullable = false)
    private boolean revoked = false;

    public RefreshTokenEntity() {
    }

    public RefreshTokenEntity(
            UserEntity user,
            String token,
            LocalDateTime expiresAt
    ) {
        this.user = user;
        this.token = token;
        this.expiresAt = expiresAt;
        this.revoked = false;
    }

    /*
     * Verifica si el refresh token ya expiró según la fecha
     * guardada en la base de datos.
     */
    public boolean isExpired() {
        return expiresAt.isBefore(LocalDateTime.now());
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public boolean isRevoked() {
        return revoked;
    }

    public void setRevoked(boolean revoked) {
        this.revoked = revoked;
    }
}