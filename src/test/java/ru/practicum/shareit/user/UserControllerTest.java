package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.CreateUserRequestDto;
import ru.practicum.shareit.user.dto.PatchUserRequestDto;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.dto.UserResponse;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.related.Constants.CONTROLLER_USER_PATH;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private UserService userService;

    @Autowired
    private MockMvc mvc;

    private UserResponse response;
    private int userId;

    @BeforeEach
    void setUp() {
        userId = 1;
        response = new UserResponse(userId, "user", "user@user.com");
    }

    @Test
    @SneakyThrows
    void createUser_whenRightRequest_thenStatusCreated() {
        CreateUserRequestDto request = new CreateUserRequestDto("user", "user@user.com");
        response = new UserResponse(userId, "user", "user@user.com");

        when(userService.createUser(any(CreateUserRequestDto.class)))
                .thenReturn(response);

        mvc.perform(post(CONTROLLER_USER_PATH)
                        .content(mapper.writeValueAsString(request))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(userId), Integer.class))
                .andExpect(jsonPath("$.name", is(response.getName()), String.class))
                .andExpect(jsonPath("$.email", is(response.getEmail()), String.class));

        verify(userService).createUser(any(CreateUserRequestDto.class));
    }

    @Test
    @SneakyThrows
    void createUser_whenBadRequest_thenFailValidation() {
        CreateUserRequestDto request = new CreateUserRequestDto("user", "user@user.com");

        when(userService.createUser(any(CreateUserRequestDto.class)))
                .thenReturn(new UserResponse());

        request.setEmail("user.com");
        mvc.perform(post(CONTROLLER_USER_PATH)
                        .content(mapper.writeValueAsString(request))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(userService, never()).createUser(any(CreateUserRequestDto.class));

        request.setEmail("user@user.com");
        request.setName("  ");
        mvc.perform(post(CONTROLLER_USER_PATH)
                        .content(mapper.writeValueAsString(request))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(userService, never()).createUser(any(CreateUserRequestDto.class));
    }

    @Test
    @SneakyThrows
    void readUser_whenRightPathVariable_thenUserById() {
        when(userService.readUser(userId))
                .thenReturn(response);

        mvc.perform(get(CONTROLLER_USER_PATH + "/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userId), Integer.class))
                .andExpect(jsonPath("$.name", is(response.getName()), String.class))
                .andExpect(jsonPath("$.email", is(response.getEmail()), String.class));

        verify(userService).readUser(anyInt());
    }

    @Test
    @SneakyThrows
    void getUsers_whenRightResponse_thenStatusOkWithJsonSizeOne() {
        Page<UserResponse> page = new PageImpl<>(new ArrayList<>(List.of(response)),
                PageRequest.of(0, 2), 10L);
        when(userService.getUsers(any(PageRequest.class)))
                .thenReturn(page);

        mvc.perform(get(CONTROLLER_USER_PATH)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.hasSize(1)));

        verify(userService).getUsers(any(PageRequest.class));
    }

    @Test
    @SneakyThrows
    void updateUser_thenRequestDto_thenStatusOk() {
        UserRequestDto userRequestDto = new UserRequestDto(1, "name", "emailUpdate@jaco.co");

        when(userService.updateUser(userRequestDto))
                .thenReturn(response);

        mvc.perform(put(CONTROLLER_USER_PATH)
                        .content(mapper.writeValueAsString(userRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(userService).updateUser(userRequestDto);
    }

    @Test
    @SneakyThrows
    void changeUser_thenChangeEmail_thenStatusOk() {
        PatchUserRequestDto patchUserRequestDto = new PatchUserRequestDto();
        patchUserRequestDto.setEmail("emailUpdate@jaco.co");

        when(userService.changeUser(userId, patchUserRequestDto))
                .thenReturn(response);

        mvc.perform(patch(CONTROLLER_USER_PATH + "/1")
                        .content(mapper.writeValueAsString(patchUserRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(userService).changeUser(userId, patchUserRequestDto);
    }

    @Test
    @SneakyThrows
    void changeUser_thenBadRequestEmail_thenStatusBadRequest() {
        PatchUserRequestDto patchUserRequestDto = new PatchUserRequestDto();
        patchUserRequestDto.setEmail("email");

        when(userService.changeUser(userId, patchUserRequestDto))
                .thenReturn(response);

        mvc.perform(patch(CONTROLLER_USER_PATH + "/1")
                        .content(mapper.writeValueAsString(patchUserRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(userService, never()).changeUser(userId, patchUserRequestDto);
    }

}