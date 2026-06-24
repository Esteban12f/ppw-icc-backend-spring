package ec.edu.ups.icc.fundamentos01.users.services;

import ec.edu.ups.icc.fundamentos01.users.dtos.CreateUserDto;
import ec.edu.ups.icc.fundamentos01.users.dtos.PartialUpdateUserDto;
import ec.edu.ups.icc.fundamentos01.users.dtos.UpdateUserDto;
import ec.edu.ups.icc.fundamentos01.users.dtos.UserResponseDto;
import ec.edu.ups.icc.fundamentos01.users.entities.UserEntity;
import ec.edu.ups.icc.fundamentos01.users.mappers.UserMapper;
import ec.edu.ups.icc.fundamentos01.users.models.UserModel;
import ec.edu.ups.icc.fundamentos01.users.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/*
 * Implementación del servicio de usuarios.
 *
 * En esta clase se reemplaza la lista en memoria por UserRepository.
 */
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /*
     * Retorna todos los usuarios almacenados en PostgreSQL.
     */
    @Override
    public List<UserResponseDto> findAll() {

        return userRepository.findAll()
                .stream()
                .filter(entity -> !entity.isDeleted())
                .map(UserMapper::toModelFromEntity)
                .map(UserMapper::toResponse)
                .toList();
    }

    /*
     * Busca un usuario por id.
     */
    @Override
    public UserResponseDto findOne(Long id) {

        return userRepository.findById(id)
                .filter(entity -> !entity.isDeleted())
                .map(UserMapper::toModelFromEntity)
                .map(UserMapper::toResponse)
                .orElseThrow(() -> new IllegalStateException("User not found"));
    }

    /*
     * Crea un nuevo usuario.
     */
    @Override
    public UserResponseDto create(CreateUserDto dto) {

        UserModel model = UserMapper.toModelFromDTO(dto);

        UserEntity entity = UserMapper.toEntityFromModel(model);

        UserEntity savedEntity = userRepository.save(entity);

        UserModel savedModel = UserMapper.toModelFromEntity(savedEntity);

        return UserMapper.toResponse(savedModel);
    }

    /*
     * Actualiza completamente un usuario.
     */
    @Override
    public UserResponseDto update(Long id, UpdateUserDto dto) {

        UserEntity entity = userRepository.findById(id)
                .filter(user -> !user.isDeleted())
                .orElseThrow(() -> new IllegalStateException("User not found"));

        entity.setName(dto.getName());
        entity.setEmail(dto.getEmail());

        UserEntity savedEntity = userRepository.save(entity);

        UserModel model = UserMapper.toModelFromEntity(savedEntity);

        return UserMapper.toResponse(model);
    }

    /*
     * Actualiza parcialmente un usuario.
     */
    @Override
    public UserResponseDto partialUpdate(Long id, PartialUpdateUserDto dto) {

        UserEntity entity = userRepository.findById(id)
                .filter(user -> !user.isDeleted())
                .orElseThrow(() -> new IllegalStateException("User not found"));

        if (dto.getName() != null) {
            entity.setName(dto.getName());
        }

        if (dto.getEmail() != null) {
            entity.setEmail(dto.getEmail());
        }

        UserEntity savedEntity = userRepository.save(entity);

        UserModel model = UserMapper.toModelFromEntity(savedEntity);

        return UserMapper.toResponse(model);
    }

    /*
     * Elimina lógicamente un usuario por id.
     */
    @Override
    public void delete(Long id) {

        UserEntity entity = userRepository.findById(id)
                .filter(user -> !user.isDeleted())
                .orElseThrow(() -> new IllegalStateException("User not found"));

        entity.setDeleted(true);

        userRepository.save(entity);
    }
}