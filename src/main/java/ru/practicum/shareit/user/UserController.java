package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.UserDto.UserDTO;
import ru.practicum.shareit.user.model.User;

import javax.validation.Valid;
import java.util.List;
import static java.util.stream.Collectors.toList;


@RestController
@Validated
@RequestMapping(path = "/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public List<UserDTO> getUsers() {
        return userService.getUsers().stream().map(UserMapper::toDTO).collect(toList());
    }


    @GetMapping("/{id}")
    public UserDTO getUser(@PathVariable("id") Long id) {
        return UserMapper.toDTO(userService.getUser(id));
    }

    @PostMapping
    public UserDTO addUser(@Valid @RequestBody UserDTO userDTO) {
        User user = userService.create(UserMapper.toUser(userDTO));
        return UserMapper.toDTO(user);
    }

    @PatchMapping("/{userId}")
    public UserDTO updateUser(@Valid @RequestBody UserDTO userDTO,
                              @PathVariable("userId") Long userId) {
        User user = UserMapper.toUser(userDTO);
        user.setId(userId);
        return UserMapper.toDTO(userService.edit(user));
    }

    @DeleteMapping("/{userId}")
    public void deleteUserById(@PathVariable("userId") Long userId) {
        userService.delete(userId);
    }

}
