package ec.edu.ups.icc.fundamentos01.security.services;

import ec.edu.ups.icc.fundamentos01.users.entities.UserEntity;
import ec.edu.ups.icc.fundamentos01.users.repositories.UserRepository;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

/*
 * UserDetailsServiceImpl conecta Spring Security
 * con nuestra base de datos.
 *
 * Spring Security llama automáticamente al método:
 *
 * loadUserByUsername()
 *
 * durante el login y también cuando necesita reconstruir
 * el usuario autenticado desde el token JWT.
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    /*
     * Repositorio usado para buscar usuarios en PostgreSQL.
     */
    private final UserRepository userRepository;

    /*
     * Constructor con inyección de dependencias.
     */
    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /*
     * Método principal requerido por Spring Security.
     *
     * Aunque el método se llama loadUserByUsername,
     * en este proyecto el username realmente es el email.
     *
     * Flujo:
     * 1. Busca el usuario por email.
     * 2. Verifica que no esté eliminado.
     * 3. Convierte UserEntity a UserDetailsImpl.
     *
     * Si el usuario no existe, Spring Security recibe
     * UsernameNotFoundException y devuelve 401 Unauthorized.
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity user = userRepository.findByEmailAndDeletedFalse(email)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Usuario no encontrado con email: " + email
                ));

        return UserDetailsImpl.build(user);
    }
}
