package ru.practicum.shareit.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.user.dto.*;


public interface UserService {

    UserResponse createUser(CreateUserRequestDto createUserRequestDto);

    UserResponse readUser(Integer userId);

    UserResponse updateUser(UserRequestDto userRequestDto);

    void deleteUser(Integer userId);

    Page<UserResponse> getUsers(PageRequest pageRequest);

    UserResponse changeUser(Integer userId, PatchUserRequestDto patchUserRequestDto);
}
