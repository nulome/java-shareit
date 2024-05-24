package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {

    UserDto createUser(UserDto userDto);

    UserDto updateUser(UserDto userDto);

    UserDto deleteUser(Integer userId);

    List<UserDto> getUsers();

    UserDto getUser(Integer userId);

    UserDto changeUser(Integer userId, UserDto userDto);
}
