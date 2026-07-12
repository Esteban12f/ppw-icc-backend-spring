package ec.edu.ups.icc.fundamentos01.products.repositories;

import ec.edu.ups.icc.fundamentos01.products.entities.ProductEntity;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
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

        /*
         * Consulta productos activos usando Page.
         *
         * Page incluye totalElements y totalPages.
         */
        @Query(value = """
                        SELECT p
                        FROM ProductEntity p
                        WHERE p.deleted = false
                        """, countQuery = """
                        SELECT COUNT(p)
                        FROM ProductEntity p
                        WHERE p.deleted = false
                        """)
        Page<ProductEntity> findActivePage(Pageable pageable);

        /*
         * Consulta productos activos usando Slice.
         *
         * Slice no calcula totalElements ni totalPages.
         */
        @Query("""
                        SELECT p
                        FROM ProductEntity p
                        WHERE p.deleted = false
                        """)
        Slice<ProductEntity> findActiveSlice(Pageable pageable);

        /*
         * Productos de una categoría usando Page.
         */
        @Query(value = """
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
                        """, countQuery = """
                        SELECT COUNT(DISTINCT p)
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
        Page<ProductEntity> findByCategoryIdWithFiltersPage(
                        @Param("categoryId") Long categoryId,
                        @Param("name") String name,
                        @Param("minPrice") Double minPrice,
                        @Param("maxPrice") Double maxPrice,
                        @Param("userId") Long userId,
                        Pageable pageable);

        /*
         * Productos de una categoría usando Slice.
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
        Slice<ProductEntity> findByCategoryIdWithFiltersSlice(
                        @Param("categoryId") Long categoryId,
                        @Param("name") String name,
                        @Param("minPrice") Double minPrice,
                        @Param("maxPrice") Double maxPrice,
                        @Param("userId") Long userId,
                        Pageable pageable);

        /*
         * Consulta productos activos del usuario autenticado usando Slice.
         *
         * Este método se usa para que /api/products/slice
         * devuelva únicamente los productos del dueño.
         */
        @Query("""
                        SELECT p
                        FROM ProductEntity p
                        WHERE p.deleted = false
                          AND p.owner.id = :ownerId
                          AND p.owner.deleted = false
                        """)
        Slice<ProductEntity> findActiveSliceByOwnerId(
                        @Param("ownerId") Long ownerId,
                        Pageable pageable);
}