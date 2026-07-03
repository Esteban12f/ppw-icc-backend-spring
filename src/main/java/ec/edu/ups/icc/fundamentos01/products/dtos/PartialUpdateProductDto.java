package ec.edu.ups.icc.fundamentos01.products.dtos;

import java.util.Set;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

/*
 * DTO utilizado para actualizar parcialmente un producto.
 *
 * Solo se actualizan los campos enviados.
 */
public class PartialUpdateProductDto {

    @Size(min = 3, max = 150, message = "El nombre debe tener entre 3 y 150 caracteres")
    private String name;

    @DecimalMin(value = "0.0", inclusive = true, message = "El precio no puede ser negativo")
    private Double price;

    @Min(value = 0, message = "El stock no puede ser negativo")
    private Integer stock;

    @Size(max = 500, message = "La descripción no debe superar los 500 caracteres")
    private String description;

    private Set<Long> categoryIds;

    public PartialUpdateProductDto() {
    }


    public PartialUpdateProductDto(
            @Size(min = 3, max = 150, message = "El nombre debe tener entre 3 y 150 caracteres") String name,
            @DecimalMin(value = "0.0", inclusive = true, message = "El precio no puede ser negativo") Double price,
            @Min(value = 0, message = "El stock no puede ser negativo") Integer stock,
            @Size(max = 500, message = "La descripción no debe superar los 500 caracteres") String description,
            Set<Long> categoryIds) {
        this.name = name;
        this.price = price;
        this.stock = stock;
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