package ec.edu.ups.icc.fundamentos01.core.health;

import ec.edu.ups.icc.fundamentos01.products.repositories.ProductRepository;

import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.stereotype.Component;

/*
 * HealthIndicator personalizado.
 *
 * Este componente agrega información adicional al endpoint:
 *
 * GET /api/actuator/health
 *
 * Sirve para comprobar que la aplicación puede consultar
 * información desde la base de datos.
 */
@Component
public class CustomHealthIndicator implements HealthIndicator {

    private final ProductRepository productRepository;

    public CustomHealthIndicator(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public Health health() {
        try {
            long totalProducts = productRepository.count();

            return Health.up()
                    .withDetail("database", "PostgreSQL conectado")
                    .withDetail("products", totalProducts)
                    .withDetail("message", "La API está funcionando correctamente")
                    .build();

        } catch (Exception exception) {
            return Health.down()
                    .withDetail("database", "Error al consultar PostgreSQL")
                    .withDetail("error", exception.getMessage())
                    .build();
        }
    }
}
