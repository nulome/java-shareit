package ru.practicum.shareit.user;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.CreateUserRequestDto;
import ru.practicum.shareit.user.dto.PatchUserRequestDto;
import ru.practicum.shareit.user.dto.UserRequestDto;

import static ru.practicum.shareit.related.Constants.CONTROLLER_USER_PATH;

@Controller
@RequestMapping(path = CONTROLLER_USER_PATH)
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {

    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> createUser(@RequestBody @Valid CreateUserRequestDto createUserRequestDto) {
        log.info("Creating user {}", createUserRequestDto);
        return userClient.createUser(createUserRequestDto);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> readUser(@PathVariable int userId) {
        log.info("Get user by userId={}", userId);
        return userClient.readUser(userId);
    }

    @PutMapping
    public ResponseEntity<Object> updateUser(@RequestBody @Valid UserRequestDto userRequestDto) {
        log.info("Put by user={}", userRequestDto);
        return userClient.updateUser(userRequestDto);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteUser(@PathVariable int userId) {
        log.info("Delete user by userId={}", userId);
        return userClient.deleteUser(userId);
    }


    @GetMapping
    public ResponseEntity<Object> getUsers(@RequestParam(value = "page-number", defaultValue = "0") @Min(0) int number,
                                           @RequestParam(value = "page-size", defaultValue = "10") @Min(3) @Max(50) int size) {
        log.info("Get users with page-number={}, page-size={}", number, size);
        return userClient.getUsers(number, size);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> changeUser(@PathVariable int userId, @RequestBody @Valid PatchUserRequestDto patchUserRequestDto) {
        log.info("Patch user {}, userId={}", patchUserRequestDto, userId);
        return userClient.changeUser(userId, patchUserRequestDto);
    }

}
