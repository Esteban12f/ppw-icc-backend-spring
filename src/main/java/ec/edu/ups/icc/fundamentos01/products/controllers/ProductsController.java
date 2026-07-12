package ec.edu.ups.icc.fundamentos01.products.controllers;

import ec.edu.ups.icc.fundamentos01.core.dto.PaginationDto;
import ec.edu.ups.icc.fundamentos01.products.dtos.CreateProductDto;
import ec.edu.ups.icc.fundamentos01.products.dtos.PartialUpdateProductDto;
import ec.edu.ups.icc.fundamentos01.products.dtos.ProductResponseDto;
import ec.edu.ups.icc.fundamentos01.products.dtos.UpdateProductDto;
import ec.edu.ups.icc.fundamentos01.products.services.ProductService;
import ec.edu.ups.icc.fundamentos01.security.services.UserDetailsImpl;

import jakarta.validation.Valid;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/*
 * Controlador REST encargado de exponer endpoints HTTP
 * para la gestión de productos.
 *
 * En esta práctica se utiliza @AuthenticationPrincipal
 * para obtener el usuario autenticado desde Spring Security.
 *
 * Los métodos create, update, partialUpdate y delete
 * envían el usuario autenticado al servicio para validar ownership.
 */
@RestController
@RequestMapping("/products")
public class ProductsController {

    private final ProductService productService;

    public ProductsController(ProductService productService) {
        this.productService = productService;
    }

    /*
     * Lista todos los productos activos.
     *
     * GET /api/products
     *
     * Solo un usuario con ROLE_ADMIN puede acceder.
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<ProductResponseDto> findAll() {
        return productService.findAll();
    }

    /*
     * Lista productos utilizando Page.
     *
     * GET /api/products/page
     *
     * Cualquier usuario autenticado puede acceder.
     */
    @GetMapping("/page")
    public Page<ProductResponseDto> findAllPage(
            @Valid @ModelAttribute PaginationDto pagination) {
        return productService.findAllPage(pagination);
    }

    /*
     * Alias de paginación solicitado en prácticas anteriores.
     *
     * GET /api/products/paginated
     */
    @GetMapping("/paginated")
    public Page<ProductResponseDto> findAllPaginated(
            @Valid @ModelAttribute PaginationDto pagination) {
        return productService.findAllPage(pagination);
    }

    /*
     * Lista productos usando Slice.
     *
     * Ruta:
     * GET /api/products/slice
     *
     * Regla:
     * - Cualquier usuario autenticado puede acceder.
     * - Solo se devuelven los productos del usuario autenticado.
     *
     * El usuario se obtiene desde el token JWT mediante
     * 
     * @AuthenticationPrincipal.
     */
    @GetMapping("/slice")
    public Slice<ProductResponseDto> findAllSlice(
            @Valid @ModelAttribute PaginationDto pagination,
            @AuthenticationPrincipal UserDetailsImpl currentUser) {
        return productService.findAllSlice(pagination, currentUser);
    }

    /*
     * Obtiene un producto por ID.
     *
     * GET /api/products/{id}
     */
    @GetMapping("/{id}")
    public ProductResponseDto findOne(
            @PathVariable Long id) {
        return productService.findOne(id);
    }

    /*
     * Crea un producto.
     *
     * POST /api/products
     *
     * El owner se obtiene desde el token JWT mediante
     * 
     * @AuthenticationPrincipal.
     *
     * Ya no se recibe userId en CreateProductDto.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductResponseDto create(
            @Valid @RequestBody CreateProductDto dto,
            @AuthenticationPrincipal UserDetailsImpl currentUser) {
        return productService.create(dto, currentUser);
    }

    /*
     * Actualiza completamente un producto.
     *
     * PUT /api/products/{id}
     *
     * Se envía currentUser al servicio para validar ownership.
     */
    @PutMapping("/{id}")
    public ProductResponseDto update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateProductDto dto,
            @AuthenticationPrincipal UserDetailsImpl currentUser) {
        return productService.update(id, dto, currentUser);
    }

    /*
     * Actualiza parcialmente un producto.
     *
     * PATCH /api/products/{id}
     *
     * Se envía currentUser al servicio para validar ownership.
     */
    @PatchMapping("/{id}")
    public ProductResponseDto partialUpdate(
            @PathVariable Long id,
            @Valid @RequestBody PartialUpdateProductDto dto,
            @AuthenticationPrincipal UserDetailsImpl currentUser) {
        return productService.partialUpdate(id, dto, currentUser);
    }

    /*
     * Elimina lógicamente un producto.
     *
     * DELETE /api/products/{id}
     *
     * Se valida que el usuario sea owner o ADMIN.
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetailsImpl currentUser) {
        productService.delete(id, currentUser);
    }

    /*
     * Consulta productos de un usuario.
     *
     * GET /api/products/user/{userId}
     */
    @GetMapping("/user/{userId}")
    public List<ProductResponseDto> findByUserId(
            @PathVariable Long userId) {
        return productService.findByUserId(userId);
    }

    /*
     * Consulta productos de una categoría.
     *
     * GET /api/products/category/{categoryId}
     */
    @GetMapping("/category/{categoryId}")
    public List<ProductResponseDto> findByCategoryId(
            @PathVariable Long categoryId) {
        return productService.findByCategoryId(categoryId);
    }
}