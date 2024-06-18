package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserResponse;

import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
class UserMapperTest {

    private UserMapper userMapper;

    @BeforeEach
    void setUp() {
        userMapper = new UserMapperImpl();
    }

    @Test
    void toResponse() {
        UserResponse userActual = userMapper.toResponse(null);
        assertNull(userActual);
    }

    @Test
    void toUserDto() {
        UserDto userActual = userMapper.toUserDto(null);
        assertNull(userActual);
    }
}