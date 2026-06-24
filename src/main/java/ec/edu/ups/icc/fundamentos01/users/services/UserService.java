package ec.edu.ups.icc.fundamentos01.users.services;

import ec.edu.ups.icc.fundamentos01.users.dtos.CreateUserDto;
import ec.edu.ups.icc.fundamentos01.users.dtos.PartialUpdateUserDto;
import ec.edu.ups.icc.fundamentos01.users.dtos.UpdateUserDto;
import ec.edu.ups.icc.fundamentos01.users.dtos.UserResponseDto;

import java.util.List;

/*
 * Servicio que define las operaciones disponibles
 * para la gestión de usuarios.
 */
public interface UserService {

    List<UserResponseDto> findAll();

    UserResponseDto findOne(Long id);

    UserResponseDto create(CreateUserDto dto);

    UserResponseDto update(Long id, UpdateUserDto dto);

    UserResponseDto partialUpdate(Long id, PartialUpdateUserDto dto);

    void delete(Long id);
}