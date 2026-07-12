package ec.edu.ups.icc.fundamentos01.products.services;

import ec.edu.ups.icc.fundamentos01.core.dto.PaginationDto;
import ec.edu.ups.icc.fundamentos01.products.dtos.CreateProductDto;
import ec.edu.ups.icc.fundamentos01.products.dtos.PartialUpdateProductDto;
import ec.edu.ups.icc.fundamentos01.products.dtos.ProductFilterByCategoryDto;
import ec.edu.ups.icc.fundamentos01.products.dtos.ProductFilterByUserDto;
import ec.edu.ups.icc.fundamentos01.products.dtos.ProductResponseDto;
import ec.edu.ups.icc.fundamentos01.products.dtos.UpdateProductDto;
import ec.edu.ups.icc.fundamentos01.security.services.UserDetailsImpl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;

/*
 * Servicio que define las operaciones disponibles
 * para la gestión de productos.
 *
 * Los métodos que modifican información reciben
 * UserDetailsImpl para poder validar ownership.
 */
public interface ProductService {

        /*
         * Devuelve todos los productos activos.
         *
         * El controlador protege este endpoint para ROLE_ADMIN.
         */
        List<ProductResponseDto> findAll();

        /*
         * Devuelve un producto activo por su ID.
         */
        ProductResponseDto findOne(Long id);

        /*
         * Crea un producto usando como owner
         * al usuario autenticado.
         *
         * El owner no viene desde CreateProductDto.
         */
        ProductResponseDto create(
                        CreateProductDto dto,
                        UserDetailsImpl currentUser);

        /*
         * Actualiza completamente un producto.
         *
         * Antes de modificar se valida:
         * - ROLE_ADMIN puede modificar cualquier producto.
         * - ROLE_USER solo puede modificar productos propios.
         */
        ProductResponseDto update(
                        Long id,
                        UpdateProductDto dto,
                        UserDetailsImpl currentUser);

        /*
         * Actualiza parcialmente un producto.
         *
         * También aplica validación de ownership.
         */
        ProductResponseDto partialUpdate(
                        Long id,
                        PartialUpdateProductDto dto,
                        UserDetailsImpl currentUser);

        /*
         * Elimina lógicamente un producto.
         *
         * También aplica validación de ownership.
         */
        void delete(
                        Long id,
                        UserDetailsImpl currentUser);

        /*
         * Consulta productos de un usuario.
         */
        List<ProductResponseDto> findByUserId(Long userId);

        /*
         * Consulta productos de una categoría.
         */
        List<ProductResponseDto> findByCategoryId(Long categoryId);

        /*
         * Consulta productos de un usuario aplicando filtros.
         */
        List<ProductResponseDto> findByUserIdWithFilters(
                        Long userId,
                        ProductFilterByUserDto filters);

        /*
         * Consulta productos de una categoría aplicando filtros.
         */
        List<ProductResponseDto> findByCategoryIdWithFilters(
                        Long categoryId,
                        ProductFilterByCategoryDto filters);

        /*
         * Consulta general paginada usando Page.
         */
        Page<ProductResponseDto> findAllPage(PaginationDto pagination);

        /*
         * Retorna productos activos usando Slice.
         *
         * A diferencia de Page, este endpoint devuelve únicamente
         * los productos del usuario autenticado.
         */
        Slice<ProductResponseDto> findAllSlice(
                        PaginationDto pagination,
                        UserDetailsImpl currentUser);

        /*
         * Consulta productos de una categoría usando Page.
         */
        Page<ProductResponseDto> findByCategoryIdWithFiltersPage(
                        Long categoryId,
                        ProductFilterByCategoryDto filters,
                        PaginationDto pagination);

        /*
         * Consulta productos de una categoría usando Slice.
         */
        Slice<ProductResponseDto> findByCategoryIdWithFiltersSlice(
                        Long categoryId,
                        ProductFilterByCategoryDto filters,
                        PaginationDto pagination);
}