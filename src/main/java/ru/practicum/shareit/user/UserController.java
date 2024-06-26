package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.CreateUserRequestDto;
import ru.practicum.shareit.user.dto.PatchUserRequestDto;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.dto.UserResponse;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;

import static ru.practicum.shareit.related.Constants.CONTROLLER_USER_PATH;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = CONTROLLER_USER_PATH)
public class UserController {

    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse createUser(@RequestBody @Valid CreateUserRequestDto createUserRequestDto) {
        return userService.createUser(createUserRequestDto);
    }

    @GetMapping("/{userId}")
    public UserResponse readUser(@PathVariable int userId) {
        return userService.readUser(userId);
    }

    @PutMapping
    public UserResponse updateUser(@RequestBody @Valid UserRequestDto userRequestDto) {
        return userService.updateUser(userRequestDto);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable int userId) {
        userService.deleteUser(userId);
    }


    @GetMapping
    @Validated
    public List<UserResponse> getUsers(@RequestParam(value = "page-number", defaultValue = "0") @Min(0) int number,
                                       @RequestParam(value = "page-size", defaultValue = "10") @Min(3) @Max(50) int size) {
        return userService.getUsers(PageRequest.of(number, size)).getContent();
    }

    @Transactional
    @PatchMapping("/{userId}")
    public UserResponse changeUser(@PathVariable int userId, @RequestBody @Valid PatchUserRequestDto patchUserRequestDto) {
        return userService.changeUser(userId, patchUserRequestDto);
    }

}
