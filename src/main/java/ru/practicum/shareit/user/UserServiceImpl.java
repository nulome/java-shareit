package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.*;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityNotFoundException;


@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Override
    public UserResponse createUser(CreateUserRequestDto createUserRequestDto) {
        log.info("Получен запрос Post /users - {}", createUserRequestDto.getEmail());
        User user = modelMapper.map(createUserRequestDto, User.class);
        user = userRepository.save(user);

        return modelMapper.map(user, UserResponse.class);
    }

    @Override
    public UserResponse readUser(Integer userId) {
        log.info("Получен запрос Get /users/{}", userId);
        User user = userRepository.getUserById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Значение в базе не найдено user: " + userId));
        return modelMapper.map(user, UserResponse.class);
    }

    @Override
    public UserResponse updateUser(UserRequestDto userRequestDto) {
        log.info("Получен запрос Put /users - {}", userRequestDto.getEmail());
        UserDto userDto = modelMapper.map(userRequestDto, UserDto.class);

        User user = modelMapper.map(userDto, User.class);
        user = userRepository.save(user);
        return modelMapper.map(user, UserResponse.class);
    }

    @Override
    public void deleteUser(Integer userId) {
        log.info("Получен запрос Delete /users/{}", userId);
        userRepository.deleteById(userId);
    }

    @Override
    public Page<UserResponse> getUsers(PageRequest pageRequest) {
        log.info("Получен запрос Get /users");
        Page<User> listUser = userRepository.findAll(pageRequest);
        return listUser.map(user -> modelMapper.map(user, UserResponse.class));
    }

    @Override
    public UserResponse changeUser(Integer userId, PatchUserRequestDto patchUserRequestDto) {
        log.info("Получен запрос Patch /users/{}", userId);
        patchUserUpdateByDto(userId, patchUserRequestDto);

        User user = userRepository.getReferenceById(userId);
        return modelMapper.map(user, UserResponse.class);
    }

    private void patchUserUpdateByDto(Integer userId, PatchUserRequestDto patchUserRequestDto) {
        if (patchUserRequestDto.getName() != null) {
            if (patchUserRequestDto.getName().isBlank()) {
                throw new IllegalArgumentException("Поле Name не может быть пустым.");
            }
            log.debug("Обновление имени в /users/{}", userId);
            userRepository.updateName(userId, patchUserRequestDto.getName());
        }
        if (patchUserRequestDto.getEmail() != null) {
            log.debug("Обновление Email в /users/{}", userId);
            userRepository.updateUserEmail(userId, patchUserRequestDto.getEmail());
        }
    }
}
