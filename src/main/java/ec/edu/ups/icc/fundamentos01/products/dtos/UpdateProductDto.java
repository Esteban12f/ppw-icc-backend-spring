package ec.edu.ups.icc.fundamentos01.products.dtos;

import java.util.Set;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/*
 * DTO utilizado para actualizar completamente un producto.
 *
 * Permite actualizar los datos editables del producto
 * y cambiar la categoría asociada.
 *
 * No permite cambiar el usuario propietario.
 */
public class UpdateProductDto {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 3, max = 150, message = "El nombre debe tener entre 3 y 150 caracteres")
    private String name;

    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.0", inclusive = true, message = "El precio no puede ser negativo")
    private Double price;

    @NotNull(message = "El stock es obligatorio")
    @Min(value = 0, message = "El stock no puede ser negativo")
    private Integer stock;

    @NotNull(message = "El ID de la categoría es obligatorio")
    private Long categoryId;

    @Size(max = 500, message = "La descripción no debe superar los 500 caracteres")
    private String description;

    private Set<Long> categoryIds;

    public UpdateProductDto() {
    }

    

    public UpdateProductDto(
            @NotBlank(message = "El nombre es obligatorio") @Size(min = 3, max = 150, message = "El nombre debe tener entre 3 y 150 caracteres") String name,
            @NotNull(message = "El precio es obligatorio") @DecimalMin(value = "0.0", inclusive = true, message = "El precio no puede ser negativo") Double price,
            @NotNull(message = "El stock es obligatorio") @Min(value = 0, message = "El stock no puede ser negativo") Integer stock,
            @NotNull(message = "El ID de la categoría es obligatorio") Long categoryId,
            @Size(max = 500, message = "La descripción no debe superar los 500 caracteres") String description,
            Set<Long> categoryIds) {
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.categoryId = categoryId;
        this.description = description;
        this.categoryIds = categoryIds;
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<Long> getCategoryIds() {
        return categoryIds;
    }

    public void setCategoryIds(Set<Long> categoryIds) {
        this.categoryIds = categoryIds;
    }

    
    
}