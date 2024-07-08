package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.CreateUserRequestDto;
import ru.practicum.shareit.user.dto.PatchUserRequestDto;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.dto.UserResponse;

import java.util.List;

import static ru.practicum.shareit.related.Constants.CONTROLLER_USER_PATH;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = CONTROLLER_USER_PATH)
public class UserController {

    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse createUser(@RequestBody CreateUserRequestDto createUserRequestDto) {
        log.info("Получен запрос Post {} - {}", CONTROLLER_USER_PATH, createUserRequestDto.getEmail());
        return userService.createUser(createUserRequestDto);
    }

    @GetMapping("/{userId}")
    public UserResponse readUser(@PathVariable int userId) {
        log.info("Получен запрос Get {}/{}", CONTROLLER_USER_PATH, userId);
        return userService.readUser(userId);
    }

    @PutMapping
    public UserResponse updateUser(@RequestBody UserRequestDto userRequestDto) {
        log.info("Получен запрос Put {} - {}", CONTROLLER_USER_PATH, userRequestDto.getEmail());
        return userService.updateUser(userRequestDto);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable int userId) {
        log.info("Получен запрос Delete {}/{}", CONTROLLER_USER_PATH, userId);
        userService.deleteUser(userId);
    }


    @GetMapping
    public List<UserResponse> getUsers(@RequestParam int number,
                                       @RequestParam int size) {
        log.info("Получен запрос Get " + CONTROLLER_USER_PATH);
        return userService.getUsers(PageRequest.of(number, size)).getContent();
    }

    @Transactional
    @PatchMapping("/{userId}")
    public UserResponse changeUser(@PathVariable int userId, @RequestBody PatchUserRequestDto patchUserRequestDto) {
        log.info("Получен запрос Patch {}/{}", CONTROLLER_USER_PATH, userId);
        return userService.changeUser(userId, patchUserRequestDto);
    }

}
