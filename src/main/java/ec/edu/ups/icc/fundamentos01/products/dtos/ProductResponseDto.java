package ec.edu.ups.icc.fundamentos01.products.dtos;
import java.time.LocalDateTime;
import java.util.Set;

import ec.edu.ups.icc.fundamentos01.categories.dtos.CategoryResponseDto;
import ec.edu.ups.icc.fundamentos01.users.dtos.UserResponseDto;

/*
 * DTO utilizado para devolver al cliente los datos públicos
 * de un producto, incluyendo información resumida
 * del usuario propietario y de la categoría.
 */
public class ProductResponseDto {

    private Long id;

    private String name;

    private Double price;

    private Integer stock;

    private UserResponseDto owner;

    private Set<CategoryResponseDto> categories;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private String description;

    public ProductResponseDto() {
    }

    public ProductResponseDto(Long id, String name, Double price, Integer stock, UserResponseDto owner,
            Set<CategoryResponseDto> categories, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.owner = owner;
        this.categories = categories;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public UserResponseDto getOwner() {
        return owner;
    }

    public void setOwner(UserResponseDto owner) {
        this.owner = owner;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<CategoryResponseDto> getCategories() {
        return categories;
    }

    public void setCategories(Set<CategoryResponseDto> categories) {
        this.categories = categories;
    }

}
