package ec.edu.ups.icc.fundamentos01.products.mappers;

import ec.edu.ups.icc.fundamentos01.categories.dtos.CategoryResponseDto;
import ec.edu.ups.icc.fundamentos01.products.dtos.CreateProductDto;
import ec.edu.ups.icc.fundamentos01.products.dtos.ProductResponseDto;
import ec.edu.ups.icc.fundamentos01.products.entities.ProductEntity;
import ec.edu.ups.icc.fundamentos01.products.models.ProductModel;
import ec.edu.ups.icc.fundamentos01.users.dtos.UserResponseDto;

public class ProductMapper {

    public static ProductModel toModelFromDTO(CreateProductDto dto) {

        ProductModel model = new ProductModel();

        model.setName(dto.getName());
        model.setDescription(dto.getDescription());
        model.setPrice(dto.getPrice());
        model.setStock(dto.getStock());

        return model;
    }

    public static ProductModel toModelFromEntity(ProductEntity entity) {

        ProductModel model = new ProductModel();

        model.setId(entity.getId());
        model.setName(entity.getName());
        model.setDescription(entity.getDescription());
        model.setPrice(entity.getPrice());
        model.setStock(entity.getStock());
        model.setCreatedAt(entity.getCreatedAt());
        model.setUpdatedAt(entity.getUpdatedAt());
        model.setDeleted(entity.isDeleted());

        return model;
    }

    public static ProductEntity toEntityFromModel(ProductModel model) {

        ProductEntity entity = new ProductEntity();

        entity.setId(model.getId());
        entity.setName(model.getName());
        entity.setDescription(model.getDescription());
        entity.setPrice(model.getPrice());
        entity.setStock(model.getStock());

        return entity;
    }

    public static ProductResponseDto toResponse(ProductModel model) {

        ProductResponseDto dto = new ProductResponseDto();

        dto.setId(model.getId());
        dto.setName(model.getName());
        dto.setDescription(model.getDescription());
        dto.setPrice(model.getPrice());
        dto.setStock(model.getStock());
        dto.setCreatedAt(model.getCreatedAt());
        dto.setUpdatedAt(model.getUpdatedAt());

        return dto;
    }

    public static ProductResponseDto toResponseFromEntity(ProductEntity entity) {

        ProductResponseDto dto = new ProductResponseDto();

        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setPrice(entity.getPrice());
        dto.setStock(entity.getStock());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());

        if (entity.getOwner() != null) {
            UserResponseDto ownerDto = new UserResponseDto();

            ownerDto.setId(entity.getOwner().getId());
            ownerDto.setName(entity.getOwner().getName());
            ownerDto.setEmail(entity.getOwner().getEmail());

            dto.setOwner(ownerDto);
        }

        if (entity.getCategory() != null) {
            CategoryResponseDto categoryDto = new CategoryResponseDto();

            categoryDto.setId(entity.getCategory().getId());
            categoryDto.setName(entity.getCategory().getName());
            categoryDto.setDescription(entity.getCategory().getDescription());

            dto.setCategory(categoryDto);
        }

        return dto;
    }
}