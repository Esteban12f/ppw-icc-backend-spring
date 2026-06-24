package ec.edu.ups.icc.fundamentos01.users.mappers;

import ec.edu.ups.icc.fundamentos01.users.dtos.CreateUserDto;
import ec.edu.ups.icc.fundamentos01.users.dtos.UserResponseDto;
import ec.edu.ups.icc.fundamentos01.users.entities.UserEntity;
import ec.edu.ups.icc.fundamentos01.users.models.UserModel;

/*
 * Clase encargada de convertir objetos entre DTOs, modelos y entidades.
 */
public class UserMapper {

    /*
     * Convierte un CreateUserDto en UserModel.
     */
    public static UserModel toModelFromDTO(CreateUserDto dto) {

        UserModel model = new UserModel();

        model.setName(dto.getName());
        model.setEmail(dto.getEmail());
        model.setPassword(dto.getPassword());

        /*
         * Para esta práctica se usa una asignación simple.
         * En una práctica real se debería usar BCrypt.
         */
        model.setPasswordHash(dto.getPassword());

        return model;
    }

    /*
     * Convierte una entidad JPA en UserModel.
     */
    public static UserModel toModelFromEntity(UserEntity entity) {

        UserModel model = new UserModel();

        model.setId(entity.getId());
        model.setName(entity.getName());
        model.setEmail(entity.getEmail());
        model.setPasswordHash(entity.getPasswordHash());
        model.setCreatedAt(entity.getCreatedAt());
        model.setUpdatedAt(entity.getUpdatedAt());
        model.setDeleted(entity.isDeleted());

        return model;
    }

    /*
     * Convierte un UserModel en UserEntity.
     */
    public static UserEntity toEntityFromModel(UserModel model) {

        UserEntity entity = new UserEntity();

        entity.setId(model.getId());
        entity.setName(model.getName());
        entity.setEmail(model.getEmail());
        entity.setPasswordHash(model.getPasswordHash());

        return entity;
    }

    /*
     * Convierte un UserModel en UserResponseDto.
     * No se expone password ni passwordHash.
     */
    public static UserResponseDto toResponse(UserModel model) {

        UserResponseDto dto = new UserResponseDto();

        dto.setId(model.getId());
        dto.setName(model.getName());
        dto.setEmail(model.getEmail());

        return dto;
    }
}