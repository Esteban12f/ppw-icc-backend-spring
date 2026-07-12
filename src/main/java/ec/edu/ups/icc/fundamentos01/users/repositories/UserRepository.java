package ec.edu.ups.icc.fundamentos01.users.repositories;

import ec.edu.ups.icc.fundamentos01.users.entities.UserEntity;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/*
 * Repositorio encargado de gestionar usuarios
 * mediante Spring Data JPA.
 */
@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    /*
     * Busca un usuario por email.
     */
    Optional<UserEntity> findByEmail(String email);

    /*
     * Busca un usuario activo por email.
     *
     * Se utiliza durante la autenticación.
     */
    Optional<UserEntity> findByEmailAndDeletedFalse(String email);

    /*
     * Verifica si existe un usuario con determinado email.
     */
    boolean existsByEmail(String email);

    /*
     * Verifica si existe un usuario activo con determinado email.
     */
    boolean existsByEmailAndDeletedFalse(String email);

    /*
     * Verifica si existe un usuario activo por ID.
     */
    boolean existsByIdAndDeletedFalse(Long id);

    /*
     * Devuelve todos los usuarios activos.
     */
    List<UserEntity> findByDeletedFalse();

    /*
     * Busca un usuario activo por ID.
     *
     * Este método se usa en ownership para convertir
     * el UserDetailsImpl autenticado en una entidad JPA.
     */
    Optional<UserEntity> findByIdAndDeletedFalse(Long id);
}