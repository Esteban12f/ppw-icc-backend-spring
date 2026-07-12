package ec.edu.ups.icc.fundamentos01.security.config;

import ec.edu.ups.icc.fundamentos01.security.entities.RoleEntity;
import ec.edu.ups.icc.fundamentos01.security.enums.RoleName;
import ec.edu.ups.icc.fundamentos01.security.repositories.RoleRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/*
 * SecurityDataInitializer se ejecuta automáticamente
 * cuando inicia la aplicación.
 *
 * Su función es crear los roles base del sistema:
 * - ROLE_USER
 * - ROLE_ADMIN
 *
 * Esto evita tener que insertar los roles manualmente
 * desde PostgreSQL.
 */
@Component
public class SecurityDataInitializer implements CommandLineRunner {

    /*
     * Repositorio para consultar y guardar roles.
     */
    private final RoleRepository roleRepository;

    public SecurityDataInitializer(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    /*
     * Método ejecutado automáticamente al iniciar Spring Boot.
     */
    @Override
    public void run(String... args) {
        createRoleIfNotExists(
                RoleName.ROLE_USER,
                "Usuario estándar del sistema"
        );

        createRoleIfNotExists(
                RoleName.ROLE_ADMIN,
                "Administrador del sistema"
        );
    }

    /*
     * Crea un rol solo si todavía no existe.
     *
     * Esto evita duplicados cada vez que se reinicia la aplicación.
     */
    private void createRoleIfNotExists(RoleName roleName, String description) {
        if (!roleRepository.existsByName(roleName)) {
            RoleEntity role = new RoleEntity(roleName, description);
            roleRepository.save(role);
        }
    }
}
