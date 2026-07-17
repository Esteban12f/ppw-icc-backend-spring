package ec.edu.ups.icc.fundamentos01.security.services;

import ec.edu.ups.icc.fundamentos01.core.exceptions.domain.BadRequestException;
import ec.edu.ups.icc.fundamentos01.security.config.JwtProperties;
import ec.edu.ups.icc.fundamentos01.security.entities.RefreshTokenEntity;
import ec.edu.ups.icc.fundamentos01.security.repositories.RefreshTokenRepository;
import ec.edu.ups.icc.fundamentos01.security.utils.JwtUtil;
import ec.edu.ups.icc.fundamentos01.users.entities.UserEntity;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/*
 * Servicio encargado de crear, validar, rotar y revocar refresh tokens.
 */
@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    private final JwtUtil jwtUtil;

    private final JwtProperties jwtProperties;

    public RefreshTokenService(
            RefreshTokenRepository refreshTokenRepository,
            JwtUtil jwtUtil,
            JwtProperties jwtProperties
    ) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtUtil = jwtUtil;
        this.jwtProperties = jwtProperties;
    }

    /*
     * Crea un refresh token para un usuario.
     *
     * El token se firma como JWT y también se guarda en base de datos.
     */
    @Transactional
    public RefreshTokenEntity createRefreshToken(
            UserEntity user,
            UserDetailsImpl userDetails
    ) {
        String token = jwtUtil.generateRefreshToken(userDetails);

        LocalDateTime expiresAt = LocalDateTime.now()
                .plus(Duration.ofMillis(jwtProperties.getRefreshExpiration()));

        RefreshTokenEntity refreshToken = new RefreshTokenEntity(
                user,
                token,
                expiresAt
        );

        return refreshTokenRepository.save(refreshToken);
    }

    /*
     * Valida un refresh token recibido desde el cliente.
     *
     * Validaciones:
     * 1. El JWT debe tener firma válida.
     * 2. El JWT debe ser de tipo refresh.
     * 3. El token debe existir en base de datos.
     * 4. El token no debe estar revocado.
     * 5. El token no debe estar expirado.
     * 6. El usuario dueño del token debe seguir activo.
     */
    @Transactional
    public RefreshTokenEntity validateAndGetActiveToken(String token) {
        if (!jwtUtil.validateRefreshToken(token)) {
            throw new BadRequestException("Refresh token inválido");
        }

        RefreshTokenEntity refreshToken = refreshTokenRepository
                .findByTokenAndRevokedFalse(token)
                .orElseThrow(() ->
                        new BadRequestException(
                                "Refresh token no encontrado o revocado"
                        )
                );

        if (refreshToken.isExpired()) {
            refreshToken.setRevoked(true);
            refreshTokenRepository.save(refreshToken);

            throw new BadRequestException("Refresh token expirado");
        }

        if (refreshToken.getUser() == null
                || refreshToken.getUser().isDeleted()) {
            throw new BadRequestException(
                    "Usuario no válido para este refresh token"
            );
        }

        return refreshToken;
    }

    /*
     * Revoca un refresh token específico.
     *
     * Se usa en:
     * - refresh, para rotar tokens
     * - logout, para cerrar sesión
     */
    @Transactional
    public void revoke(RefreshTokenEntity refreshToken) {
        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);
    }

    /*
     * Revoca todos los refresh tokens activos de un usuario.
     *
     * En esta práctica se usa durante login para dejar
     * una sola sesión activa por usuario.
     *
     * Si se quisiera permitir varias sesiones o varios dispositivos,
     * se podría no llamar a este método durante login.
     */
    @Transactional
    public void revokeAllByUser(UserEntity user) {
        List<RefreshTokenEntity> tokens =
                refreshTokenRepository.findByUser_IdAndRevokedFalse(
                        user.getId()
                );

        tokens.forEach(token -> token.setRevoked(true));

        refreshTokenRepository.saveAll(tokens);
    }
}
