package ec.edu.ups.icc.fundamentos01.products.services;

import ec.edu.ups.icc.fundamentos01.categories.entity.CategoryEntity;
import ec.edu.ups.icc.fundamentos01.categories.repositories.CategoryRepository;
import ec.edu.ups.icc.fundamentos01.core.dto.PaginationDto;
import ec.edu.ups.icc.fundamentos01.core.exceptions.domain.BadRequestException;
import ec.edu.ups.icc.fundamentos01.core.exceptions.domain.ConflictException;
import ec.edu.ups.icc.fundamentos01.core.exceptions.domain.NotFoundException;
import ec.edu.ups.icc.fundamentos01.products.dtos.CreateProductDto;
import ec.edu.ups.icc.fundamentos01.products.dtos.PartialUpdateProductDto;
import ec.edu.ups.icc.fundamentos01.products.dtos.ProductFilterByCategoryDto;
import ec.edu.ups.icc.fundamentos01.products.dtos.ProductFilterByUserDto;
import ec.edu.ups.icc.fundamentos01.products.dtos.ProductResponseDto;
import ec.edu.ups.icc.fundamentos01.products.dtos.UpdateProductDto;
import ec.edu.ups.icc.fundamentos01.products.entities.ProductEntity;
import ec.edu.ups.icc.fundamentos01.products.mappers.ProductMapper;
import ec.edu.ups.icc.fundamentos01.products.repositories.ProductRepository;
import ec.edu.ups.icc.fundamentos01.security.services.UserDetailsImpl;
import ec.edu.ups.icc.fundamentos01.users.entities.UserEntity;
import ec.edu.ups.icc.fundamentos01.users.repositories.UserRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/*
 * Servicio encargado de la lógica de negocio de productos.
 *
 * En esta práctica se agrega validación de ownership.
 *
 * Reglas:
 * - ROLE_USER solo puede modificar o eliminar productos propios.
 * - ROLE_ADMIN puede modificar o eliminar cualquier producto.
 * - El owner de un producto nuevo se obtiene desde el token JWT.
 */
@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    public ProductServiceImpl(
            ProductRepository productRepository,
            UserRepository userRepository,
            CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
    }

    /*
     * Devuelve todos los productos activos.
     */
    @Override
    @Transactional(readOnly = true)
    public List<ProductResponseDto> findAll() {
        return productRepository.findByDeletedFalse()
                .stream()
                .map(ProductMapper::toResponseFromEntity)
                .toList();
    }

    /*
     * Devuelve un producto activo por su ID.
     */
    @Override
    @Transactional(readOnly = true)
    public ProductResponseDto findOne(Long id) {
        ProductEntity entity = findActiveProductOrThrow(id);

        return ProductMapper.toResponseFromEntity(entity);
    }

    /*
     * Crea un producto usando como owner al usuario autenticado.
     *
     * El owner ya no se toma desde CreateProductDto.
     *
     * Esto evita que un usuario pueda crear productos
     * a nombre de otro usuario.
     */
    @Override
    @Transactional
    public ProductResponseDto create(
            CreateProductDto dto,
            UserDetailsImpl currentUser) {
        /*
         * Convierte el usuario autenticado en una entidad JPA.
         */
        UserEntity owner = findCurrentUserEntity(currentUser);

        /*
         * Verifica que el nombre no esté registrado.
         */
        validateProductNameForCreate(dto.getName());

        /*
         * Busca las categorías activas indicadas en el body.
         */
        Set<CategoryEntity> categories = findActiveCategories(dto.getCategoryIds());

        ProductEntity entity = new ProductEntity();

        entity.setName(dto.getName().trim());
        entity.setDescription(dto.getDescription());
        entity.setPrice(dto.getPrice());
        entity.setStock(dto.getStock());

        /*
         * El owner se obtiene desde currentUser.
         */
        entity.setOwner(owner);

        entity.setCategories(categories);

        ProductEntity savedEntity = productRepository.save(entity);

        return ProductMapper.toResponseFromEntity(savedEntity);
    }

    /*
     * Actualiza completamente un producto.
     *
     * Antes de modificarlo se valida ownership.
     */
    @Override
    @Transactional
    public ProductResponseDto update(
            Long id,
            UpdateProductDto dto,
            UserDetailsImpl currentUser) {
        ProductEntity entity = findActiveProductOrThrow(id);

        /*
         * Comprueba que el usuario sea owner o ADMIN.
         */
        validateOwnership(entity, currentUser);

        /*
         * Verifica que el nuevo nombre no esté registrado
         * en otro producto activo.
         */
        validateProductNameForUpdate(id, dto.getName());

        Set<CategoryEntity> categories = findActiveCategories(dto.getCategoryIds());

        entity.setName(dto.getName().trim());
        entity.setDescription(dto.getDescription());
        entity.setPrice(dto.getPrice());
        entity.setStock(dto.getStock());
        entity.setCategories(categories);

        ProductEntity savedEntity = productRepository.save(entity);

        return ProductMapper.toResponseFromEntity(savedEntity);
    }

    /*
     * Actualiza parcialmente un producto.
     *
     * Solo modifica los campos enviados y también valida ownership.
     */
    @Override
    @Transactional
    public ProductResponseDto partialUpdate(
            Long id,
            PartialUpdateProductDto dto,
            UserDetailsImpl currentUser) {
        ProductEntity entity = findActiveProductOrThrow(id);

        /*
         * Comprueba que el usuario sea owner o ADMIN.
         */
        validateOwnership(entity, currentUser);

        if (dto.getName() != null) {
            validateProductNameForUpdate(id, dto.getName());
            entity.setName(dto.getName().trim());
        }

        if (dto.getDescription() != null) {
            entity.setDescription(dto.getDescription());
        }

        if (dto.getPrice() != null) {
            entity.setPrice(dto.getPrice());
        }

        if (dto.getStock() != null) {
            entity.setStock(dto.getStock());
        }

        if (dto.getCategoryIds() != null) {
            Set<CategoryEntity> categories = findActiveCategories(dto.getCategoryIds());
            entity.setCategories(categories);
        }

        ProductEntity savedEntity = productRepository.save(entity);

        return ProductMapper.toResponseFromEntity(savedEntity);
    }

    /*
     * Elimina lógicamente un producto.
     *
     * El producto no se borra físicamente.
     * Solo se establece deleted = true.
     */
    @Override
    @Transactional
    public void delete(
            Long id,
            UserDetailsImpl currentUser) {
        ProductEntity entity = findActiveProductOrThrow(id);

        /*
         * Comprueba que el usuario sea owner o ADMIN.
         */
        validateOwnership(entity, currentUser);

        entity.setDeleted(true);

        productRepository.save(entity);
    }

    /*
     * Consulta productos activos de un usuario.
     */
    @Override
    @Transactional(readOnly = true)
    public List<ProductResponseDto> findByUserId(Long userId) {
        if (!userRepository.existsByIdAndDeletedFalse(userId)) {
            throw new NotFoundException("User not found");
        }

        return productRepository.findByOwner_IdAndDeletedFalse(userId)
                .stream()
                .map(ProductMapper::toResponseFromEntity)
                .toList();
    }

    /*
     * Consulta productos activos de una categoría.
     */
    @Override
    @Transactional(readOnly = true)
    public List<ProductResponseDto> findByCategoryId(Long categoryId) {
        if (!categoryRepository.existsByIdAndDeletedFalse(categoryId)) {
            throw new NotFoundException("Category not found");
        }

        return productRepository.findByCategory_IdAndDeletedFalse(categoryId)
                .stream()
                .map(ProductMapper::toResponseFromEntity)
                .toList();
    }

    /*
     * Consulta productos por usuario aplicando filtros.
     */
    @Override
    @Transactional(readOnly = true)
    public List<ProductResponseDto> findByUserIdWithFilters(
            Long userId,
            ProductFilterByUserDto filters) {
        if (!userRepository.existsByIdAndDeletedFalse(userId)) {
            throw new NotFoundException("User not found");
        }

        validateUserFilters(filters);

        String name = normalizeName(filters.getName());

        return productRepository.findByOwnerIdWithFilters(
                userId,
                name,
                filters.getMinPrice(),
                filters.getMaxPrice(),
                filters.getCategoryId())
                .stream()
                .map(ProductMapper::toResponseFromEntity)
                .toList();
    }

    /*
     * Consulta productos por categoría aplicando filtros.
     */
    @Override
    @Transactional(readOnly = true)
    public List<ProductResponseDto> findByCategoryIdWithFilters(
            Long categoryId,
            ProductFilterByCategoryDto filters) {
        if (!categoryRepository.existsByIdAndDeletedFalse(categoryId)) {
            throw new NotFoundException("Category not found");
        }

        validateCategoryFilters(filters);

        String name = normalizeName(filters.getName());

        return productRepository.findByCategoryIdWithFilters(
                categoryId,
                name,
                filters.getMinPrice(),
                filters.getMaxPrice(),
                filters.getUserId())
                .stream()
                .map(ProductMapper::toResponseFromEntity)
                .toList();
    }

    /*
     * Consulta paginada completa usando Page.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponseDto> findAllPage(
            PaginationDto pagination) {
        Pageable pageable = createPageable(pagination);

        return productRepository.findActivePage(pageable)
                .map(ProductMapper::toResponseFromEntity);
    }

    /*
     * Consulta productos usando Slice.
     *
     * Regla de la práctica:
     * - Cualquier usuario autenticado puede acceder.
     * - Pero solo se devuelven los productos del dueño.
     *
     * Es decir:
     * ROLE_USER, ROLE_ADMIN o cualquier usuario autenticado
     * solo verá sus propios productos en este endpoint.
     */
    @Override
    @Transactional(readOnly = true)
    public Slice<ProductResponseDto> findAllSlice(
            PaginationDto pagination,
            UserDetailsImpl currentUser) {
        UserEntity owner = findCurrentUserEntity(currentUser);

        Pageable pageable = createPageable(pagination);

        return productRepository.findActiveSliceByOwnerId(
                owner.getId(),
                pageable)
                .map(ProductMapper::toResponseFromEntity);
    }

    /*
     * Consulta productos de una categoría usando Page.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponseDto> findByCategoryIdWithFiltersPage(
            Long categoryId,
            ProductFilterByCategoryDto filters,
            PaginationDto pagination) {
        if (!categoryRepository.existsByIdAndDeletedFalse(categoryId)) {
            throw new NotFoundException("Category not found");
        }

        validateCategoryFilters(filters);

        String name = normalizeName(filters.getName());

        Pageable pageable = createPageable(pagination);

        return productRepository.findByCategoryIdWithFiltersPage(
                categoryId,
                name,
                filters.getMinPrice(),
                filters.getMaxPrice(),
                filters.getUserId(),
                pageable)
                .map(ProductMapper::toResponseFromEntity);
    }

    /*
     * Consulta productos de una categoría usando Slice.
     */
    @Override
    @Transactional(readOnly = true)
    public Slice<ProductResponseDto> findByCategoryIdWithFiltersSlice(
            Long categoryId,
            ProductFilterByCategoryDto filters,
            PaginationDto pagination) {
        if (!categoryRepository.existsByIdAndDeletedFalse(categoryId)) {
            throw new NotFoundException("Category not found");
        }

        validateCategoryFilters(filters);

        String name = normalizeName(filters.getName());

        Pageable pageable = createPageable(pagination);

        return productRepository.findByCategoryIdWithFiltersSlice(
                categoryId,
                name,
                filters.getMinPrice(),
                filters.getMaxPrice(),
                filters.getUserId(),
                pageable)
                .map(ProductMapper::toResponseFromEntity);
    }

    /*
     * Busca un producto activo.
     *
     * Si no existe o está eliminado, devuelve 404.
     */
    private ProductEntity findActiveProductOrThrow(Long id) {
        return productRepository.findById(id)
                .filter(product -> !product.isDeleted())
                .orElseThrow(() -> new NotFoundException("Product not found"));
    }

    /*
     * Obtiene el usuario autenticado como entidad JPA.
     *
     * currentUser viene desde el SecurityContext,
     * que fue construido a partir del token JWT.
     */
    private UserEntity findCurrentUserEntity(
            UserDetailsImpl currentUser) {
        if (currentUser == null) {
            throw new AccessDeniedException("Usuario no autenticado");
        }

        return userRepository.findByIdAndDeletedFalse(currentUser.getId())
                .orElseThrow(() -> new AccessDeniedException("Usuario no autorizado"));
    }

    /*
     * Valida si el usuario puede modificar o eliminar el producto.
     *
     * Reglas:
     * 1. ROLE_ADMIN puede modificar cualquier producto.
     * 2. ROLE_USER solo puede modificar productos propios.
     */
    private void validateOwnership(
            ProductEntity product,
            UserDetailsImpl currentUser) {
        if (currentUser == null) {
            throw new AccessDeniedException("Usuario no autenticado");
        }

        /*
         * Un ADMIN puede modificar cualquier producto.
         */
        if (hasRole(currentUser, "ROLE_ADMIN")) {
            return;
        }

        /*
         * Verifica que el producto tenga un propietario válido.
         */
        if (product.getOwner() == null ||
                product.getOwner().getId() == null) {
            throw new AccessDeniedException(
                    "El producto no tiene propietario válido");
        }

        /*
         * Compara el ID del owner con el ID del usuario autenticado.
         */
        if (!product.getOwner().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException(
                    "No puedes modificar productos ajenos");
        }
    }

    /*
     * Verifica si el usuario tiene un rol específico.
     *
     * Ejemplo:
     * ROLE_USER
     * ROLE_ADMIN
     */
    private boolean hasRole(
            UserDetailsImpl user,
            String role) {
        return user.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(authority -> authority.equals(role));
    }

    /*
     * Valida que el nombre no esté registrado al crear.
     */
    private void validateProductNameForCreate(String name) {
        if (name == null || name.isBlank()) {
            throw new BadRequestException("El nombre es obligatorio");
        }

        if (productRepository
                .findByNameIgnoreCaseAndDeletedFalse(name.trim())
                .isPresent()) {
            throw new ConflictException(
                    "Product name already registered");
        }
    }

    /*
     * Valida que el nombre no esté registrado
     * en otro producto al actualizar.
     *
     * Se permite que el producto conserve su mismo nombre.
     */
    private void validateProductNameForUpdate(
            Long currentProductId,
            String name) {
        if (name == null || name.isBlank()) {
            throw new BadRequestException("El nombre es obligatorio");
        }

        productRepository
                .findByNameIgnoreCaseAndDeletedFalse(name.trim())
                .filter(product -> !product.getId().equals(currentProductId))
                .ifPresent(product -> {
                    throw new ConflictException(
                            "Product name already registered");
                });
    }

    /*
     * Busca categorías activas por sus IDs.
     *
     * Si una categoría no existe o está eliminada,
     * se rechaza la operación.
     */
    private Set<CategoryEntity> findActiveCategories(
            Set<Long> categoryIds) {
        if (categoryIds == null || categoryIds.isEmpty()) {
            throw new BadRequestException(
                    "Debe seleccionar al menos una categoría");
        }

        Set<Long> uniqueIds = new HashSet<>(categoryIds);

        Set<CategoryEntity> categories = categoryRepository.findAllById(uniqueIds)
                .stream()
                .filter(category -> !category.isDeleted())
                .collect(Collectors.toSet());

        if (categories.size() != uniqueIds.size()) {
            throw new NotFoundException(
                    "One or more categories were not found");
        }

        return categories;
    }

    /*
     * Valida filtros usados para consultar productos por usuario.
     */
    private void validateUserFilters(
            ProductFilterByUserDto filters) {
        if (filters == null) {
            return;
        }

        if (!filters.hasValidPriceRange()) {
            throw new BadRequestException(
                    "El precio máximo debe ser mayor o igual al precio mínimo");
        }

        if (filters.getCategoryId() != null &&
                !categoryRepository.existsByIdAndDeletedFalse(
                        filters.getCategoryId())) {
            throw new NotFoundException("Category not found");
        }
    }

    /*
     * Valida filtros usados para consultar productos por categoría.
     */
    private void validateCategoryFilters(
            ProductFilterByCategoryDto filters) {
        if (filters == null) {
            return;
        }

        if (!filters.hasValidPriceRange()) {
            throw new BadRequestException(
                    "El precio máximo debe ser mayor o igual al precio mínimo");
        }

        if (filters.getUserId() != null &&
                !userRepository.existsByIdAndDeletedFalse(
                        filters.getUserId())) {
            throw new NotFoundException("User not found");
        }
    }

    /*
     * Normaliza el nombre usado en filtros.
     */
    private String normalizeName(String name) {
        if (name == null || name.isBlank()) {
            return null;
        }

        return name.trim();
    }

    /*
     * Construye Pageable usando los parámetros recibidos.
     */
    private Pageable createPageable(
            PaginationDto pagination) {
        String sortBy = normalizeSortBy(
                pagination.getSortBy());

        Sort.Direction direction = normalizeDirection(
                pagination.getDirection());

        Sort sort = Sort.by(direction, sortBy);

        return PageRequest.of(
                pagination.getPage(),
                pagination.getSize(),
                sort);
    }

    /*
     * Valida el campo de ordenamiento.
     */
    private String normalizeSortBy(String sortBy) {
        if (sortBy == null || sortBy.isBlank()) {
            return "id";
        }

        Set<String> allowedFields = Set.of(
                "id",
                "name",
                "price",
                "stock",
                "createdAt",
                "updatedAt");

        if (!allowedFields.contains(sortBy)) {
            throw new BadRequestException(
                    "Campo de ordenamiento no permitido: " + sortBy);
        }

        return sortBy;
    }

    /*
     * Valida la dirección de ordenamiento.
     */
    private Sort.Direction normalizeDirection(
            String direction) {
        if (direction == null || direction.isBlank()) {
            return Sort.Direction.ASC;
        }

        if (direction.equalsIgnoreCase("asc")) {
            return Sort.Direction.ASC;
        }

        if (direction.equalsIgnoreCase("desc")) {
            return Sort.Direction.DESC;
        }

        throw new BadRequestException(
                "Dirección de ordenamiento no válida: " + direction);
    }
}