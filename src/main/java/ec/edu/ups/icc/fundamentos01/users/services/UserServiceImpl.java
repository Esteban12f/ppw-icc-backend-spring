package ec.edu.ups.icc.fundamentos01.users.services;

import ec.edu.ups.icc.fundamentos01.core.exceptions.domain.ConflictException;
import ec.edu.ups.icc.fundamentos01.core.exceptions.domain.NotFoundException;
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

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /*
     * Retorna todos los usuarios activos almacenados en PostgreSQL.
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
     * Busca un usuario activo por id.
     *
     * Si no existe o está eliminado, lanza NotFoundException.
     */
    @Override
    public UserResponseDto findOne(Long id) {

        UserEntity entity = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found"));

        UserModel model = UserMapper.toModelFromEntity(entity);

        return UserMapper.toResponse(model);
    }

    /*
     * Crea un nuevo usuario.
     *
     * Si el email ya existe, lanza ConflictException.
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
     * Actualiza completamente un usuario activo.
     *
     * Si no existe o está eliminado, lanza NotFoundException.
     */
    @Override
    public UserResponseDto update(Long id, UpdateUserDto dto) {

        UserEntity entity = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found"));

        userRepository.findByEmail(dto.getEmail())
                .filter(existingUser -> !existingUser.getId().equals(id))
                .ifPresent(existingUser -> {
                    throw new ConflictException("Email already registered");
                });

        entity.setName(dto.getName());
        entity.setEmail(dto.getEmail());

        UserEntity savedEntity = userRepository.save(entity);

        UserModel model = UserMapper.toModelFromEntity(savedEntity);

        return UserMapper.toResponse(model);
    }

    /*
     * Actualiza parcialmente un usuario activo.
     *
     * Si no existe o está eliminado, lanza NotFoundException.
     */
    @Override
    public UserResponseDto partialUpdate(Long id, PartialUpdateUserDto dto) {

        UserEntity entity = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found"));

        if (entity.isDeleted()) {
            throw new NotFoundException("User not found");
        }

        if (dto.getEmail() != null) {
            userRepository.findByEmail(dto.getEmail())
                    .filter(existingUser -> !existingUser.getId().equals(id))
                    .ifPresent(existingUser -> {
                        throw new ConflictException("Email already registered");
                    });

            entity.setEmail(dto.getEmail());
        }

        if (dto.getName() != null) {
            entity.setName(dto.getName());
        }

        if (dto.getPassword() != null) {
            entity.setPasswordHash("HASH_" + dto.getPassword());
        }

        UserEntity savedEntity = userRepository.save(entity);

        UserModel model = UserMapper.toModelFromEntity(savedEntity);

        return UserMapper.toResponse(model);
    }

    /*
     * Elimina lógicamente un usuario por id.
     *
     * Si no existe o ya está eliminado, lanza NotFoundException.
     */
    @Override
    public void delete(Long id) {

        UserEntity entity = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found"));

        entity.setDeleted(true);

        userRepository.save(entity);
    }
}