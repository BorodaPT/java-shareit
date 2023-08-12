package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.ExceptionBadRequest;
import ru.practicum.shareit.user.UserDto.UserDTO;
import ru.practicum.shareit.user.model.User;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;


@RestController
@Validated
@RequestMapping(path = "/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public List<UserDTO> getUsers() {
        return userService.getAllUsers();
    }


    @GetMapping("/{id}")
    public UserDTO getUser(@PathVariable("id") Long id) {
        if (id < 1) {
            throw new ExceptionBadRequest("Получение пользователя","Некорректный id");
        }
        return userService.getUser(id);
    }

    @PostMapping
    public UserDTO addUser(@Valid @RequestBody UserDTO userDTO) {
        return userService.saveUser(userDTO);
    }

    @PatchMapping("/{userId}")
    public UserDTO updateUser(@Valid @RequestBody UserDTO userDTO,
                              @PathVariable("userId") Long userId) {
        User user = UserMapper.toUser(userDTO);
        user.setId(userId);
        return userService.editUser(UserMapper.toDTO(user),userId);
    }

    @DeleteMapping("/{userId}")
    public void deleteUserById(@PathVariable("userId") Long userId) {
        userService.delete(userId);
    }

}
