package ec.edu.ups.icc.fundamentos01.products.controllers;

import ec.edu.ups.icc.fundamentos01.core.dto.PaginationDto;
import ec.edu.ups.icc.fundamentos01.products.dtos.CreateProductDto;
import ec.edu.ups.icc.fundamentos01.products.dtos.PartialUpdateProductDto;
import ec.edu.ups.icc.fundamentos01.products.dtos.ProductResponseDto;
import ec.edu.ups.icc.fundamentos01.products.dtos.UpdateProductDto;
import ec.edu.ups.icc.fundamentos01.products.services.ProductService;
import ec.edu.ups.icc.fundamentos01.security.config.OpenApiConfig;
import ec.edu.ups.icc.fundamentos01.security.services.UserDetailsImpl;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

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
 * Todos los endpoints de este controlador requieren JWT,
 * porque el proyecto usa .anyRequest().authenticated().
 */
@Tag(
        name = "Productos",
        description = "Gestión de productos con paginación, roles y ownership"
)
@SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_NAME)
@RestController
@RequestMapping("/products")
public class ProductsController {

    /*
     * Servicio de productos.
     */
    private final ProductService productService;

    public ProductsController(ProductService productService) {
        this.productService = productService;
    }

    /*
     * Endpoint administrativo.
     *
     * Solo ROLE_ADMIN puede consumir este endpoint.
     */
    @Operation(
            summary = "Listar todos los productos",
            description = """
                    Devuelve todos los productos activos sin paginación.
                    
                    Este endpoint es administrativo y requiere ROLE_ADMIN.
                    Para consultas normales se recomienda usar /products/page o /products/slice.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Listado completo de productos"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Token ausente o inválido"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "El usuario no tiene ROLE_ADMIN"
            )
    })
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<ProductResponseDto> findAll() {
        return productService.findAll();
    }

    /*
     * Endpoint paginado con Page.
     */
    @Operation(
            summary = "Listar productos con Page",
            description = """
                    Devuelve productos activos usando Page.
                    
                    Incluye metadatos como:
                    - totalElements
                    - totalPages
                    - number
                    - size
                    - first
                    - last
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Página de productos obtenida correctamente"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Parámetros de paginación inválidos"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Token ausente o inválido"
            )
    })
    @GetMapping("/page")
    public Page<ProductResponseDto> findAllPage(
            @Valid @ModelAttribute PaginationDto pagination
    ) {
        return productService.findAllPage(pagination);
    }

    /*
     * Alias para endpoint paginado.
     */
    @Operation(
            summary = "Listar productos paginados",
            description = "Alias de /products/page para mantener compatibilidad con prácticas anteriores."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Página de productos obtenida correctamente"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Parámetros de paginación inválidos"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Token ausente o inválido"
            )
    })
    @GetMapping("/paginated")
    public Page<ProductResponseDto> findAllPaginated(
            @Valid @ModelAttribute PaginationDto pagination
    ) {
        return productService.findAllPage(pagination);
    }

    /*
     * Endpoint paginado con Slice.
     */
    @Operation(
            summary = "Listar productos con Slice",
            description = """
                    Devuelve productos usando Slice.
                    
                    No calcula totalElements ni totalPages.
                    Es útil para navegación simple o scroll infinito.
                    
                    Según la regla trabajada, este endpoint devuelve
                    únicamente los productos del usuario autenticado.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Slice de productos obtenido correctamente"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Parámetros de paginación inválidos"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Token ausente o inválido"
            )
    })
    @GetMapping("/slice")
    public Slice<ProductResponseDto> findAllSlice(
            @Valid @ModelAttribute PaginationDto pagination,
            @AuthenticationPrincipal UserDetailsImpl currentUser
    ) {
        return productService.findAllSlice(pagination, currentUser);
    }

    /*
     * Obtener producto por ID.
     */
    @Operation(
            summary = "Obtener producto por ID",
            description = "Devuelve la información de un producto activo por su identificador."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Producto encontrado"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Token ausente o inválido"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Producto no encontrado"
            )
    })
    @GetMapping("/{id}")
    public ProductResponseDto findOne(
            @PathVariable Long id
    ) {
        return productService.findOne(id);
    }

    /*
     * Crear producto.
     *
     * El owner se toma desde el usuario autenticado.
     */
    @Operation(
            summary = "Crear producto",
            description = """
                    Crea un producto asociado al usuario autenticado.
                    
                    El cliente no debe enviar userId.
                    El owner se obtiene desde el JWT mediante @AuthenticationPrincipal.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Producto creado correctamente"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos de entrada inválidos"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Token ausente o inválido"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Nombre de producto ya registrado"
            )
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductResponseDto create(
            @Valid @RequestBody CreateProductDto dto,
            @AuthenticationPrincipal UserDetailsImpl currentUser
    ) {
        return productService.create(dto, currentUser);
    }

    /*
     * Actualizar producto.
     *
     * Se valida ownership en el servicio.
     */
    @Operation(
            summary = "Actualizar producto",
            description = """
                    Actualiza completamente un producto.
                    
                    Reglas:
                    - ROLE_USER solo puede actualizar productos propios.
                    - ROLE_ADMIN puede actualizar cualquier producto.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Producto actualizado correctamente"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos de entrada inválidos"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Token ausente o inválido"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "El usuario no es propietario del producto"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Producto no encontrado"
            )
    })
    @PutMapping("/{id}")
    public ProductResponseDto update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateProductDto dto,
            @AuthenticationPrincipal UserDetailsImpl currentUser
    ) {
        return productService.update(id, dto, currentUser);
    }

    /*
     * Actualizar producto parcialmente.
     */
    @Operation(
            summary = "Actualizar parcialmente producto",
            description = """
                    Actualiza solo los campos enviados.
                    
                    También valida ownership:
                    - ROLE_USER solo puede modificar productos propios.
                    - ROLE_ADMIN puede modificar cualquier producto.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Producto actualizado correctamente"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos de entrada inválidos"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Token ausente o inválido"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "El usuario no es propietario del producto"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Producto no encontrado"
            )
    })
    @PatchMapping("/{id}")
    public ProductResponseDto partialUpdate(
            @PathVariable Long id,
            @Valid @RequestBody PartialUpdateProductDto dto,
            @AuthenticationPrincipal UserDetailsImpl currentUser
    ) {
        return productService.partialUpdate(id, dto, currentUser);
    }

    /*
     * Eliminar producto.
     */
    @Operation(
            summary = "Eliminar producto",
            description = """
                    Elimina lógicamente un producto.
                    
                    Reglas:
                    - ROLE_USER solo puede eliminar productos propios.
                    - ROLE_ADMIN puede eliminar cualquier producto.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Producto eliminado correctamente"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Token ausente o inválido"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "El usuario no es propietario del producto"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Producto no encontrado"
            )
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetailsImpl currentUser
    ) {
        productService.delete(id, currentUser);
    }

    /*
     * Consultar productos de un usuario.
     */
    @Operation(
            summary = "Consultar productos por usuario",
            description = "Devuelve productos activos asociados a un usuario específico."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Productos encontrados"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Token ausente o inválido"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuario no encontrado"
            )
    })
    @GetMapping("/user/{userId}")
    public List<ProductResponseDto> findByUserId(
            @PathVariable Long userId
    ) {
        return productService.findByUserId(userId);
    }

    /*
     * Consultar productos por categoría.
     */
    @Operation(
            summary = "Consultar productos por categoría",
            description = "Devuelve productos activos asociados a una categoría específica."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Productos encontrados"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Token ausente o inválido"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Categoría no encontrada"
            )
    })
    @GetMapping("/category/{categoryId}")
    public List<ProductResponseDto> findByCategoryId(
            @PathVariable Long categoryId
    ) {
        return productService.findByCategoryId(categoryId);
    }
}