package ru.practicum.shareit.user;

import org.mapstruct.Mapper;
import ru.practicum.shareit.user.dto.CreateUserRequestDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.dto.UserResponse;
import ru.practicum.shareit.user.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(CreateUserRequestDto createUserRequestDto);

    User toUser(UserDto userDto);

    UserResponse toResponse(User user);

    UserDto toUserDto(UserRequestDto userRequestDto);
}
