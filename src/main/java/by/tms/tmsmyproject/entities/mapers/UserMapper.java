package by.tms.tmsmyproject.entities.mapers;

import by.tms.tmsmyproject.entities.User;
import by.tms.tmsmyproject.entities.dto.user.*;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toEntity(UserRequestCreateDto userRequestCreateDto);

    User toEntity(UserRegistrationDto userRegistrationDto);

    User toEntity(UserRequestUpdateDto userRequestCreateDto);

    User toEntity(UserRequestUpdateClientDto userRequestCreateDto);

    UserResponseGetDto toDto(User user);

    UserRequestCreateDto toCreateDto(User user);

    UserRegistrationDto toRegistrationDto(User user);

    UserRequestUpdateClientDto toClientUpdateDto(User user);

    UserRequestUpdateDto toAdminUpdateDto(User user);

    List<UserResponseGetDto> toDtoList(List<User> users);

}
