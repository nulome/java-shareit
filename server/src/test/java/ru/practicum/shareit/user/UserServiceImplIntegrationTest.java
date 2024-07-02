package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.user.dto.CreateUserRequestDto;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.dto.UserResponse;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext
class UserServiceImplIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    User user;

    @BeforeAll
    void setUp() {
        user = new User(1, "name", "email@11.ru");
        userRepository.save(user);
        user = new User(2, "name", "emai222@2.ru");
        userRepository.save(user);
    }

    @Test
    void createUser_whenRightRequest_thenActualEmail() {
        userService.createUser(new CreateUserRequestDto("Create3", "User3@email.33"));
        UserResponse userActual = userService.readUser(3);

        assertEquals("User3@email.33", userActual.getEmail());
    }

    @Test
    void updateUser_whenRightRequest_thenActualEmail() {
        UserRequestDto userRequestDto = new UserRequestDto(2, "UpdateName", "UpdE@ja.coco");
        userService.updateUser(userRequestDto);

        UserResponse userActual = userService.readUser(2);
        assertEquals("UpdE@ja.coco", userActual.getEmail());
    }
}