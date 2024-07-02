package ru.practicum.shareit.user;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.user.model.User;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;
    private final EasyRandom random = new EasyRandom();
    private User user1;
    private int userId;

    @BeforeAll
    void init() {
        user1 = random.nextObject(User.class);
        userId = 1;
        user1.setId(userId);
        userRepository.save(user1);
    }

    @Test
    void getUserById_whenRightRequest_thenActualId() {
        Optional<User> user = userRepository.getUserById(userId);

        assertTrue(user.isPresent());
        assertEquals(userId, user.get().getId());
    }

    @Test
    void updateName_whenRightRequest_thenActualName() {
        String upName = "updateName";
        userRepository.updateName(userId, upName);

        Optional<User> user = userRepository.getUserById(userId);
        assertTrue(user.isPresent());
        assertEquals(upName, user.get().getName());
    }

    @Test
    void updateUserEmail_whenRightRequest_thenActualEmail() {
        String upEmail = "updateEmail";
        userRepository.updateUserEmail(userId, upEmail);

        Optional<User> user = userRepository.getUserById(userId);
        assertTrue(user.isPresent());
        assertEquals(upEmail, user.get().getEmail());
    }
}