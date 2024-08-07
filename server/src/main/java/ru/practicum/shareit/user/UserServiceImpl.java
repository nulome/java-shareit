package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.*;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityNotFoundException;

import static ru.practicum.shareit.related.Constants.CONTROLLER_USER_PATH;


@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserResponse createUser(CreateUserRequestDto createUserRequestDto) {
        User user = userMapper.toUser(createUserRequestDto);
        user = userRepository.save(user);

        return userMapper.toResponse(user);
    }

    @Override
    public UserResponse readUser(Integer userId) {
        User user = userRepository.getUserById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Значение в базе не найдено user: " + userId));
        return userMapper.toResponse(user);
    }

    @Override
    public UserResponse updateUser(UserRequestDto userRequestDto) {
        UserDto userDto = userMapper.toUserDto(userRequestDto);

        User user = userMapper.toUser(userDto);
        user = userRepository.save(user);
        return userMapper.toResponse(user);
    }

    @Override
    public void deleteUser(Integer userId) {
        userRepository.deleteById(userId);
    }

    @Override
    public Page<UserResponse> getUsers(PageRequest pageRequest) {
        Page<User> listUser = userRepository.findAll(pageRequest);
        return listUser.map(userMapper::toResponse);
    }

    @Override
    public UserResponse changeUser(Integer userId, PatchUserRequestDto patchUserRequestDto) {
        patchUserUpdateByDto(userId, patchUserRequestDto);

        User user = userRepository.getReferenceById(userId);
        return userMapper.toResponse(user);
    }

    private void patchUserUpdateByDto(Integer userId, PatchUserRequestDto patchUserRequestDto) {
        if (patchUserRequestDto.getName() != null) {
            if (patchUserRequestDto.getName().isBlank()) {
                throw new IllegalArgumentException("Поле Name не может быть пустым.");
            }
            log.debug("Обновление имени в {}/{}", CONTROLLER_USER_PATH, userId);
            userRepository.updateName(userId, patchUserRequestDto.getName());
        }
        if (patchUserRequestDto.getEmail() != null) {
            log.debug("Обновление Email в {}/{}", CONTROLLER_USER_PATH, userId);
            userRepository.updateUserEmail(userId, patchUserRequestDto.getEmail());
        }
    }
}
