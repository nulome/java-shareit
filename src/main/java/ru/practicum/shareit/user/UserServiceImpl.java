package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.handler.UnknownValueException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    public final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Override
    public UserDto createUser(UserDto userDto) {
        log.info("Получен запрос Post /users - {}", userDto.getEmail());
        User user = modelMapper.map(userDto, User.class);
        user = userRepository.createUser(user);
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto updateUser(UserDto userDto) {
        log.info("Получен запрос Put /users - {}", userDto.getEmail());
        User user = modelMapper.map(userDto, User.class);
        checkAndReceiptUserInDataBase(user.getId());
        user = userRepository.updateUser(user);
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto deleteUser(Integer userId) {
        log.info("Получен запрос Delete /users/{}", userId);
        User user = checkAndReceiptUserInDataBase(userId);
        userRepository.deleteUser(userId);
        return UserMapper.toUserDto(user);
    }

    @Override
    public List<UserDto> getUsers() {
        log.info("Получен запрос Get /users");
        List<User> listUser = userRepository.getUsers();
        return listUser.stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getUser(Integer userId) {
        log.info("Получен запрос Get /users/{}", userId);
        User user = checkAndReceiptUserInDataBase(userId);
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto changeUser(Integer userId, UserDto userDto) {
        log.info("Получен запрос Patch /users/{}", userId);
        User user = checkAndReceiptUserInDataBase(userId);
        changeUserByDto(user, userDto);
        user = userRepository.updateUser(user);
        return UserMapper.toUserDto(user);
    }

    private void changeUserByDto(User user, UserDto userDto) {
        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            user.setEmail(userDto.getEmail());
        }
    }

    private User checkAndReceiptUserInDataBase(Integer userId) {
        log.trace("Проверка User: {} в базе", userId);
        try {
            return userRepository.getUser(userId);
        } catch (EmptyResultDataAccessException e) {
            log.error("Ошибка в запросе к базе данных. Не найдено значение по userId: {} \n {}", userId, e.getMessage());
            throw new UnknownValueException("Передан не верный userId: " + userId);
        }
    }
}
