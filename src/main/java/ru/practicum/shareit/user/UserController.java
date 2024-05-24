package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {

    private final UserServiceImpl userService;


    @PostMapping
    public User createUser(@RequestBody @Valid UserDto userDto) {
        return userService.createUser(userDto);
    }

    @PutMapping
    public User updateUser(@RequestBody @Valid User user) {
        return userService.updateUser(user);
    }

    @DeleteMapping("/{userId}")
    public User deleteUser(@PathVariable int userId) {
        return userService.deleteUser(userId);
    }

    @GetMapping
    public List<User> getUsers() {
        return userService.getUsers();
    }

    @GetMapping("/{userId}")
    public User getUser(@PathVariable int userId) {
        return userService.getUser(userId);
    }

    @PatchMapping("/{userId}")
    public User changeUser(@PathVariable int userId, @RequestBody UserDto userDto) {
        return userService.changeUser(userId, userDto);
    }

}
