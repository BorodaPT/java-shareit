package ru.practicum.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.user.dto.UserDTO;

import javax.validation.Valid;
import javax.validation.constraints.Min;


@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Validated
public class GatewayUserController {

    private final UserClient userClient;

    @GetMapping
    public ResponseEntity<Object> getUsers() {
        return userClient.getAllUsers();
    }

    @PostMapping
    public ResponseEntity<Object> addUser(@Valid @RequestBody UserDTO userDTO) {
        return userClient.saveUser(userDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getUser(@PathVariable("id") @Min(1) Long id) {
        return userClient.getUser(id);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@Valid @RequestBody UserDTO userDTO,
                                             @PathVariable("userId") @Min(1) Long userId) {
        return userClient.editUser(userId, userDTO);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteUserById(@PathVariable("userId") @Min(1) Long userId) {
       return userClient.delete(userId);
    }
}
