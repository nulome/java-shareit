package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserResponse;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.assertNull;

class UserMapperTest {

    private UserMapper userMapper;

    @BeforeEach
    void setUp() {
        userMapper = new UserMapperImpl();
    }

    @Test
    void toResponse_whenResponseNull_thenResultNull() {
        UserResponse userActual = userMapper.toResponse(null);
        assertNull(userActual);
    }

    @Test
    void toUserDto_whenResponseNull_thenResultNull() {
        User user = null;
        UserDto userActual = userMapper.toUserDto(user);
        assertNull(userActual);
    }
}