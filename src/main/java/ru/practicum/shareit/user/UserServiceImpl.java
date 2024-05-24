package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.handler.UnknownValueException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    public final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Override
    public User createUser(UserDto userDto) {
        log.info("Получен запрос Post /users - {}", userDto.getEmail());
        User user = modelMapper.map(userDto, User.class);
        return userRepository.createUser(user);
    }

    @Override
    public User updateUser(User user) {
        log.info("Получен запрос Put /users - {}", user.getEmail());
        checkAndReceiptUserInDataBase(user.getId());
        return userRepository.updateUser(user);
    }

    @Override
    public User deleteUser(Integer userId) {
        log.info("Получен запрос Delete /users/{}", userId);
        User user = checkAndReceiptUserInDataBase(userId);
        userRepository.deleteUser(userId);
        return user;
    }

    @Override
    public List<User> getUsers() {
        log.info("Получен запрос Get /users");
        return userRepository.getUsers();
    }

    @Override
    public User getUser(Integer userId) {
        log.info("Получен запрос Get /users/{}", userId);
        return checkAndReceiptUserInDataBase(userId);
    }

    @Override
    public User changeUser(Integer userId, UserDto userDto) {
        log.info("Получен запрос Patch /users/{}", userId);
        User user = checkAndReceiptUserInDataBase(userId);
        changeUserByDto(user, userDto);
        return userRepository.updateUser(user);
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
