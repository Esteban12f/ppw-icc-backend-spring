package ec.edu.ups.icc.fundamentos01.products.dtos;

import java.util.Set;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/*
 * DTO utilizado para crear productos.
 *
 * Desde la práctica de ownership, este DTO ya no recibe userId.
 *
 * El propietario del producto se obtiene directamente
 * desde el usuario autenticado mediante el token JWT.
 *
 * Esto evita que un usuario pueda crear productos
 * a nombre de otra persona.
 */
public class CreateProductDto {

    /*
     * Nombre del producto.
     *
     * Debe ser obligatorio y tener una longitud válida.
     */
    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 150, message = "El nombre debe tener entre 2 y 150 caracteres")
    private String name;

    /*
     * Descripción opcional del producto.
     */
    @Size(max = 300, message = "La descripción no debe superar los 300 caracteres")
    private String description;

    /*
     * Precio del producto.
     *
     * Debe existir y no puede ser negativo.
     */
    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(
            value = "0.0",
            inclusive = true,
            message = "El precio no puede ser negativo"
    )
    private Double price;

    /*
     * Stock disponible.
     *
     * Debe existir y no puede ser negativo.
     */
    @NotNull(message = "El stock es obligatorio")
    @Min(value = 0, message = "El stock no puede ser negativo")
    private Integer stock;

    /*
     * IDs de las categorías asociadas al producto.
     *
     * Al existir una relación ManyToMany, un producto
     * puede pertenecer a varias categorías.
     */
    @NotEmpty(message = "Debe seleccionar al menos una categoría")
    private Set<Long> categoryIds;

    /*
     * Constructor vacío requerido por Jackson.
     */
    public CreateProductDto() {
    }

    /*
     * Constructor completo.
     *
     * No contiene userId porque el owner se obtiene del JWT.
     */
    public CreateProductDto(
            String name,
            String description,
            Double price,
            Integer stock,
            Set<Long> categoryIds
    ) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.stock = stock;
        this.categoryIds = categoryIds;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Set<Long> getCategoryIds() {
        return categoryIds;
    }

    public void setCategoryIds(Set<Long> categoryIds) {
        this.categoryIds = categoryIds;
    }
}