package ec.edu.ups.icc.fundamentos01.products.dtos;

import java.util.Set;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

/*
 * DTO utilizado para actualización parcial de productos.
 *
 * A diferencia de UpdateProductDto, aquí todos los campos
 * son opcionales porque PATCH solo modifica lo que se envía.
 */
public class PartialUpdateProductDto {

    /*
     * Nombre opcional.
     */
    @Size(min = 2, max = 150, message = "El nombre debe tener entre 2 y 150 caracteres")
    private String name;

    /*
     * Descripción opcional.
     */
    @Size(max = 300, message = "La descripción no debe superar los 300 caracteres")
    private String description;

    /*
     * Precio opcional.
     */
    @DecimalMin(
            value = "0.0",
            inclusive = true,
            message = "El precio no puede ser negativo"
    )
    private Double price;

    /*
     * Stock opcional.
     */
    @Min(value = 0, message = "El stock no puede ser negativo")
    private Integer stock;

    /*
     * Categorías opcionales.
     *
     * Si se envía este campo, reemplaza las categorías actuales.
     */
    private Set<Long> categoryIds;

    public PartialUpdateProductDto() {
    }

    public PartialUpdateProductDto(
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