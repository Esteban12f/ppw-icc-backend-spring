package ec.edu.ups.icc.fundamentos01.security.services;

import ec.edu.ups.icc.fundamentos01.users.entities.UserEntity;

import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/*
 * UserDetailsImpl adapta nuestra entidad UserEntity
 * al formato que Spring Security entiende.
 *
 * Spring Security no trabaja directamente con UserEntity,
 * sino con objetos que implementan la interfaz UserDetails.
 *
 * Esta clase permite usar:
 * - email como username
 * - passwordHash como contraseña
 * - roles como authorities
 */
public class UserDetailsImpl implements UserDetails {

    /*
     * ID del usuario.
     *
     * Se usa para identificar al usuario autenticado
     * y para aplicar reglas de ownership.
     */
    private final Long id;

    /*
     * Nombre del usuario.
     */
    private final String name;

    /*
     * Email del usuario.
     *
     * En este proyecto el email funciona como username.
     */
    private final String email;

    /*
     * Contraseña cifrada del usuario.
     *
     * Debe estar en formato BCrypt.
     */
    private final String password;

    /*
     * Authorities representan los roles o permisos
     * que Spring Security usará para autorizar peticiones.
     *
     * Ejemplo:
     * ROLE_USER
     * ROLE_ADMIN
     */
    private final Collection<? extends GrantedAuthority> authorities;

    public UserDetailsImpl(
            Long id,
            String name,
            String email,
            String password,
            Collection<? extends GrantedAuthority> authorities
    ) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.authorities = authorities;
    }

    /*
     * Factory method para crear UserDetailsImpl desde UserEntity.
     *
     * Convierte los roles de la base de datos:
     *
     * Set<RoleEntity>
     *
     * en authorities de Spring Security:
     *
     * Collection<GrantedAuthority>
     */
    public static UserDetailsImpl build(UserEntity user) {
        Collection<GrantedAuthority> authorities = user.getRoles()
                .stream()
                .map(role -> new SimpleGrantedAuthority(role.getName().name()))
                .collect(Collectors.toList());

        return new UserDetailsImpl(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPassword(),
                authorities
        );
    }

    /*
     * Getters propios de la aplicación.
     */
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
    
    public String getEmail() {
        return email;
    }

    /*
     * Devuelve los roles/permisos del usuario.
     *
     * Spring Security usa este método para validar reglas como:
     *
     * hasRole('ADMIN')
     * hasAuthority('ROLE_USER')
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    /*
     * Devuelve la contraseña cifrada.
     *
     * Spring Security la usa durante el login
     * para comparar con la contraseña enviada por el cliente.
     */
    @Override
    public String getPassword() {
        return password;
    }

    /*
     * Devuelve el identificador del usuario.
     *
     * En este proyecto usamos email como username.
     */
    @Override
    public String getUsername() {
        return email;
    }

    /*
     * Métodos de estado de la cuenta.
     *
     * En esta práctica se devuelven true porque no se implementan
     * todavía bloqueos, expiración de cuenta o expiración de credenciales.
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    
    @Override
    public boolean isEnabled() {
        return true;
    }
}