package ru.practicum.shareit.user;

import javax.persistence.EntityNotFoundException;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.user.dto.CreateUserRequestDto;
import ru.practicum.shareit.user.dto.PatchUserRequestDto;
import ru.practicum.shareit.user.dto.UserResponse;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    private UserService userService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;

    private final EasyRandom random = new EasyRandom();
    private CreateUserRequestDto createUserRequest;
    private User user;

    private UserResponse userResponse;
    private int userId;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userRepository, userMapper);
        createUserRequest = random.nextObject(CreateUserRequestDto.class);
        user = random.nextObject(User.class);
        userResponse = random.nextObject(UserResponse.class);
        userId = 1;
    }

    @Test
    void createUser_whenRightRequest_thenVerifyMethod() {
        when(userMapper.toUser(createUserRequest)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toResponse(user)).thenAnswer(invocationOnMock -> {
            userResponse.setId(invocationOnMock.getArgument(0, User.class).getId());
            return userResponse;
        });

        userService.createUser(createUserRequest);

        assertEquals(user.getId(), userResponse.getId());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void readUser_whenRightRequest_thenActualId() {
        when(userRepository.getUserById(anyInt())).thenReturn(Optional.of(user));
        when(userMapper.toResponse(user)).thenAnswer(invocationOnMock -> {
            userResponse.setId(userId);
            return userResponse;
        });

        UserResponse actual = userService.readUser(userId);

        assertEquals(userId, actual.getId());
        verify(userRepository).getUserById(userId);
    }

    @Test
    void readUser_whenNotUser_thenThrows() {
        when(userRepository.getUserById(anyInt())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.readUser(userId));
        verify(userMapper, never()).toResponse(any(User.class));
    }

    @Test
    void getUsers_whenRightRequest_thenListSizeOne() {
        Page<User> page = new PageImpl<>(new ArrayList<>(List.of(user)),
                PageRequest.of(0, 2), 10L);
        when(userRepository.findAll(any(PageRequest.class))).thenReturn(page);
        userResponse.setId(77);
        when(userMapper.toResponse(any(User.class))).thenReturn(userResponse);

        List<UserResponse> listActual = userService.getUsers(PageRequest.of(0, 2)).getContent();

        assertEquals(1, listActual.size());
        assertEquals(77, listActual.get(0).getId());
        verify(userRepository).findAll(any(PageRequest.class));
    }

    @Test
    void changeUser_whenPatchEmail_thenUpdateEmailUser() {
        PatchUserRequestDto patchUser = new PatchUserRequestDto();
        patchUser.setEmail("emailPatch@lol.com");
        doAnswer(i -> {
            user.setEmail(i.getArgument(1, String.class));
            return null;
        }).when(userRepository).updateUserEmail(userId, patchUser.getEmail());
        when(userRepository.getReferenceById(userId)).thenReturn(user);
        when(userMapper.toResponse(user)).thenAnswer(i -> {
            userResponse.setEmail(user.getEmail());
            return userResponse;
        });

        UserResponse actual = userService.changeUser(userId, patchUser);
        assertEquals(patchUser.getEmail(), actual.getEmail());
        verify(userRepository).updateUserEmail(anyInt(), any(String.class));
    }

    @Test
    void changeUser_whenPatchName_thenUpdateNameUser() {
        PatchUserRequestDto patchUser = new PatchUserRequestDto();
        patchUser.setName("namePatchUpdate");
        doAnswer(i -> {
            user.setName(i.getArgument(1, String.class));
            return null;
        }).when(userRepository).updateName(userId, patchUser.getName());
        when(userRepository.getReferenceById(userId)).thenReturn(user);
        when(userMapper.toResponse(user)).thenAnswer(i -> {
            userResponse.setName(user.getName());
            return userResponse;
        });

        UserResponse actual = userService.changeUser(userId, patchUser);
        assertEquals(patchUser.getName(), actual.getName());
        verify(userRepository).updateName(anyInt(), any(String.class));
    }

    @Test
    void changeUser_whenPatchNameEmpty_thenThrows() {
        PatchUserRequestDto patchUser = new PatchUserRequestDto();
        patchUser.setName("   ");

        assertThrows(IllegalArgumentException.class, () -> userService.changeUser(userId, patchUser));
        verify(userRepository, never()).updateName(anyInt(), any(String.class));
    }
}