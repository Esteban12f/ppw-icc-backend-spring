package ec.edu.ups.icc.fundamentos01.security.services;

import ec.edu.ups.icc.fundamentos01.core.exceptions.domain.BadRequestException;
import ec.edu.ups.icc.fundamentos01.core.exceptions.domain.ConflictException;
import ec.edu.ups.icc.fundamentos01.security.dtos.AuthResponseDto;
import ec.edu.ups.icc.fundamentos01.security.dtos.LoginRequestDto;
import ec.edu.ups.icc.fundamentos01.security.dtos.RegisterRequestDto;
import ec.edu.ups.icc.fundamentos01.security.entities.RoleEntity;
import ec.edu.ups.icc.fundamentos01.security.enums.RoleName;
import ec.edu.ups.icc.fundamentos01.security.repositories.RoleRepository;
import ec.edu.ups.icc.fundamentos01.security.utils.JwtUtil;
import ec.edu.ups.icc.fundamentos01.users.entities.UserEntity;
import ec.edu.ups.icc.fundamentos01.users.repositories.UserRepository;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/*
 * AuthService contiene la lógica de autenticación.
 *
 * Responsabilidades:
 * - Login de usuarios.
 * - Registro de usuarios.
 * - Cifrado de contraseñas.
 * - Asignación de roles.
 * - Generación de tokens JWT.
 */
@Service
public class AuthService {

    /*
     * AuthenticationManager valida email y contraseña.
     */
    private final AuthenticationManager authenticationManager;

    /*
     * Repositorio de usuarios.
     */
    private final UserRepository userRepository;

    /*
     * Repositorio de roles.
     */
    private final RoleRepository roleRepository;

    /*
     * PasswordEncoder cifra y valida contraseñas.
     */
    private final PasswordEncoder passwordEncoder;

    /*
     * Utilidad para generar tokens JWT.
     */
    private final JwtUtil jwtUtil;

    public AuthService(
            AuthenticationManager authenticationManager,
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder,
            JwtUtil jwtUtil
    ) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    /*
     * Login de usuario.
     *
     * Flujo:
     * 1. Recibe email y contraseña.
     * 2. AuthenticationManager valida las credenciales.
     * 3. Si son correctas, se genera un JWT.
     * 4. Se devuelven token y datos básicos del usuario.
     *
     * Si las credenciales son incorrectas,
     * Spring Security lanza una excepción y responde 401.
     */
    @Transactional(readOnly = true)
    public AuthResponseDto login(LoginRequestDto loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        /*
         * Guarda la autenticación en el contexto de seguridad.
         */
        SecurityContextHolder.getContext().setAuthentication(authentication);

        /*
         * Genera el token JWT usando los datos del usuario autenticado.
         */
        String jwt = jwtUtil.generateToken(authentication);

        /*
         * Obtiene el usuario autenticado desde Spring Security.
         */
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        /*
         * Convierte authorities a nombres de roles.
         */
        Set<String> roles = userDetails.getAuthorities()
                .stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toSet());

        return new AuthResponseDto(
                jwt,
                userDetails.getId(),
                userDetails.getName(),
                userDetails.getEmail(),
                roles
        );
    }

    /*
     * Registro de usuario.
     *
     * Flujo:
     * 1. Verifica que el email no esté registrado.
     * 2. Crea un nuevo UserEntity.
     * 3. Cifra la contraseña con BCrypt.
     * 4. Asigna ROLE_USER por defecto.
     * 5. Guarda el usuario.
     * 6. Genera un JWT para iniciar sesión automáticamente.
     */
    @Transactional
    public AuthResponseDto register(RegisterRequestDto registerRequest) {

        /*
         * Evita registrar dos usuarios con el mismo email.
         */
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new ConflictException("El email ya está registrado");
        }

        /*
         * Crea la entidad de usuario.
         */
        UserEntity user = new UserEntity();

        user.setName(registerRequest.getName());
        user.setEmail(registerRequest.getEmail());

        /*
         * La contraseña se cifra antes de guardarse.
         *
         * Nunca debe guardarse la contraseña en texto plano.
         */
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));

        /*
         * Busca el rol ROLE_USER para asignarlo por defecto.
         */
        RoleEntity userRole = roleRepository.findByName(RoleName.ROLE_USER)
                .orElseThrow(() -> new BadRequestException("Rol por defecto no encontrado"));

        Set<RoleEntity> roles = new HashSet<>();
        roles.add(userRole);

        user.setRoles(roles);

        /*
         * Guarda el usuario con su rol asignado.
         */
        UserEntity savedUser = userRepository.save(user);

        /*
         * Convierte el usuario guardado a UserDetailsImpl
         * para generar el JWT.
         */
        UserDetailsImpl userDetails = UserDetailsImpl.build(savedUser);

        String jwt = jwtUtil.generateTokenFromUserDetails(userDetails);

        Set<String> roleNames = userDetails.getAuthorities()
                .stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toSet());

        return new AuthResponseDto(
                jwt,
                savedUser.getId(),
                savedUser.getName(),
                savedUser.getEmail(),
                roleNames
        );
    }
}
