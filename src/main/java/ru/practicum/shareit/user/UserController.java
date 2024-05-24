package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {

    private final UserServiceImpl userService;


    @PostMapping
    public UserDto createUser(@RequestBody @Valid UserDto userDto) {
        return userService.createUser(userDto);
    }

    @PutMapping
    public UserDto updateUser(@RequestBody @Valid UserDto userDto) {
        return userService.updateUser(userDto);
    }

    @DeleteMapping("/{userId}")
    public UserDto deleteUser(@PathVariable int userId) {
        return userService.deleteUser(userId);
    }

    @GetMapping
    public List<UserDto> getUsers() {
        return userService.getUsers();
    }

    @GetMapping("/{userId}")
    public UserDto getUser(@PathVariable int userId) {
        return userService.getUser(userId);
    }

    @PatchMapping("/{userId}")
    public UserDto changeUser(@PathVariable int userId, @RequestBody UserDto userDto) {
        return userService.changeUser(userId, userDto);
    }

}
