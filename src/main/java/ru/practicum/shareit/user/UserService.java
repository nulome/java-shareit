package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {

    User createUser(UserDto userDto);

    User updateUser(User user);

    User deleteUser(Integer userId);

    List<User> getUsers();

    User getUser(Integer userId);

    User changeUser(Integer userId, UserDto userDto);
}
