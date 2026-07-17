package ec.edu.ups.icc.fundamentos01.security.controllers;

import ec.edu.ups.icc.fundamentos01.security.dtos.AuthResponseDto;
import ec.edu.ups.icc.fundamentos01.security.dtos.LoginRequestDto;
import ec.edu.ups.icc.fundamentos01.security.dtos.RefreshTokenRequestDto;
import ec.edu.ups.icc.fundamentos01.security.dtos.RegisterRequestDto;
import ec.edu.ups.icc.fundamentos01.security.services.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/*
 * Controlador REST para autenticación.
 *
 * Estos endpoints son públicos:
 * - register
 * - login
 * - refresh
 * - logout
 *
 * Por eso NO se usa @SecurityRequirement a nivel de clase.
 */
@Tag(
        name = "Autenticación",
        description = "Endpoints públicos para registro, inicio de sesión, refresh token y logout"
)
@RestController
@RequestMapping("/auth")
public class AuthController {

    /*
     * Servicio que contiene la lógica de autenticación.
     */
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /*
     * Registra un nuevo usuario.
     */
    @Operation(
            summary = "Registrar usuario",
            description = """
                    Crea un nuevo usuario en el sistema.
                    
                    El usuario registrado recibe ROLE_USER por defecto.
                    Además, la respuesta incluye access token y refresh token.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Usuario registrado correctamente"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos de entrada inválidos"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "El email ya está registrado"
            )
    })
    @PostMapping("/register")
    public ResponseEntity<AuthResponseDto> register(
            @Valid @RequestBody RegisterRequestDto registerRequest
    ) {
        AuthResponseDto response = authService.register(registerRequest);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    /*
     * Inicia sesión.
     */
    @Operation(
            summary = "Iniciar sesión",
            description = """
                    Valida email y contraseña.
                    
                    Si las credenciales son correctas, devuelve:
                    - access token
                    - refresh token
                    - datos básicos del usuario
                    - roles asignados
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Login correcto"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos de entrada inválidos"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Credenciales inválidas"
            )
    })
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(
            @Valid @RequestBody LoginRequestDto loginRequest
    ) {
        AuthResponseDto response = authService.login(loginRequest);

        return ResponseEntity.ok(response);
    }

    /*
     * Renueva access token usando refresh token.
     */
    @Operation(
            summary = "Renovar tokens",
            description = """
                    Recibe un refresh token válido y devuelve nuevos tokens.
                    
                    Este endpoint aplica rotación:
                    - revoca el refresh token usado
                    - genera un nuevo access token
                    - genera un nuevo refresh token
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Tokens renovados correctamente"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Refresh token inválido, expirado o revocado"
            )
    })
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponseDto> refresh(
            @Valid @RequestBody RefreshTokenRequestDto request
    ) {
        AuthResponseDto response = authService.refresh(request);

        return ResponseEntity.ok(response);
    }

    /*
     * Cierra sesión revocando refresh token.
     */
    @Operation(
            summary = "Cerrar sesión",
            description = """
                    Revoca el refresh token enviado.
                    
                    Después del logout, ese refresh token ya no podrá usarse
                    para renovar sesión.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Logout correcto"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Refresh token inválido o revocado"
            )
    })
    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(
            @Valid @RequestBody RefreshTokenRequestDto request
    ) {
        authService.logout(request);
    }
}