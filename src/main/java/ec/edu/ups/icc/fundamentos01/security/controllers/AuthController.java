package ec.edu.ups.icc.fundamentos01.security.controllers;

import ec.edu.ups.icc.fundamentos01.security.dtos.AuthResponseDto;
import ec.edu.ups.icc.fundamentos01.security.dtos.LoginRequestDto;
import ec.edu.ups.icc.fundamentos01.security.dtos.RegisterRequestDto;
import ec.edu.ups.icc.fundamentos01.security.services.AuthService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/*
 * AuthController expone los endpoints públicos de autenticación.
 *
 * Estos endpoints no requieren token:
 *
 * POST /api/auth/register
 * POST /api/auth/login
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    /*
     * Servicio que contiene la lógica de login y registro.
     */
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /*
     * Endpoint de login.
     *
     * Recibe email y password.
     *
     * Si las credenciales son correctas:
     * - devuelve 200 OK
     * - devuelve token JWT
     * - devuelve datos básicos del usuario
     *
     * Ruta final:
     * POST /api/auth/login
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(
            @Valid @RequestBody LoginRequestDto loginRequest
    ) {
        AuthResponseDto response = authService.login(loginRequest);

        return ResponseEntity.ok(response);
    }

    /*
     * Endpoint de registro.
     *
     * Recibe name, email y password.
     *
     * Si el registro es correcto:
     * - crea el usuario
     * - asigna ROLE_USER por defecto
     * - genera token JWT
     * - devuelve 201 Created
     *
     * Ruta final:
     * POST /api/auth/register
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponseDto> register(
            @Valid @RequestBody RegisterRequestDto registerRequest
    ) {
        AuthResponseDto response = authService.register(registerRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
