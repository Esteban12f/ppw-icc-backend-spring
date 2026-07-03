package ec.edu.ups.icc.fundamentos01.products.repositories;

import ec.edu.ups.icc.fundamentos01.products.entities.ProductEntity;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Long> {

        Optional<ProductEntity> findByNameIgnoreCaseAndDeletedFalse(String name);

        List<ProductEntity> findByDeletedFalse();

        Optional<ProductEntity> findByIdAndDeletedFalse(Long id);

        List<ProductEntity> findByOwner_IdAndDeletedFalse(Long ownerId);

        /*
         * Busca productos activos asociados a una categoría.
         *
         * Como ProductEntity ahora tiene:
         * Set<CategoryEntity> categories
         *
         * Ya no se puede usar findByCategory_IdAndDeletedFalse.
         * Se debe hacer JOIN con p.categories.
         */
        @Query("""
                        SELECT DISTINCT p
                        FROM ProductEntity p
                        JOIN p.categories c
                        WHERE p.deleted = false
                          AND c.id = :categoryId
                          AND c.deleted = false
                        """)
        List<ProductEntity> findByCategory_IdAndDeletedFalse(
                        @Param("categoryId") Long categoryId);

        /*
         * Busca productos activos por nombre de categoría.
         */
        @Query("""
                        SELECT DISTINCT p
                        FROM ProductEntity p
                        JOIN p.categories c
                        WHERE p.deleted = false
                          AND LOWER(c.name) = LOWER(:categoryName)
                          AND c.deleted = false
                        """)
        List<ProductEntity> findByCategory_NameIgnoreCaseAndDeletedFalse(
                        @Param("categoryName") String categoryName);

        @Query("""
                        SELECT DISTINCT p
                        FROM ProductEntity p
                        LEFT JOIN p.categories c
                        WHERE p.deleted = false
                          AND p.owner.id = :userId
                          AND p.owner.deleted = false
                          AND (COALESCE(:name, '') = '' OR LOWER(p.name) LIKE LOWER(CONCAT('%', COALESCE(:name, ''), '%')))
                          AND (:minPrice IS NULL OR p.price >= :minPrice)
                          AND (:maxPrice IS NULL OR p.price <= :maxPrice)
                          AND (:categoryId IS NULL OR c.id = :categoryId)
                          AND (:categoryId IS NULL OR c.deleted = false)
                        """)
        List<ProductEntity> findByOwnerIdWithFilters(
                        @Param("userId") Long userId,
                        @Param("name") String name,
                        @Param("minPrice") Double minPrice,
                        @Param("maxPrice") Double maxPrice,
                        @Param("categoryId") Long categoryId);

        /*
         * Busca productos activos de una categoría aplicando filtros opcionales.
         *
         * La categoría se consulta a través de la tabla intermedia product_categories.
         */
        @Query("""
                        SELECT DISTINCT p
                        FROM ProductEntity p
                        JOIN p.categories c
                        WHERE p.deleted = false
                          AND c.id = :categoryId
                          AND c.deleted = false
                          AND p.owner.deleted = false
                          AND (COALESCE(:name, '') = '' OR LOWER(p.name) LIKE LOWER(CONCAT('%', COALESCE(:name, ''), '%')))
                          AND (:minPrice IS NULL OR p.price >= :minPrice)
                          AND (:maxPrice IS NULL OR p.price <= :maxPrice)
                          AND (:userId IS NULL OR p.owner.id = :userId)
                        """)
        List<ProductEntity> findByCategoryIdWithFilters(
                        @Param("categoryId") Long categoryId,
                        @Param("name") String name,
                        @Param("minPrice") Double minPrice,
                        @Param("maxPrice") Double maxPrice,
                        @Param("userId") Long userId);
}