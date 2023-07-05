package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.UserDto.UserDTO;
import ru.practicum.shareit.user.model.User;

@AllArgsConstructor
@Component
public class UserMapper {

    public UserDTO toDTO(User user) {
        Long id = user.getId();
        String name = user.getName();
        String email = user.getEmail();
        return new UserDTO(user.getId(), user.getName(), user.getEmail());
    }

    public User toUser(UserDTO userDTO) {
        return new User(userDTO.getId(), userDTO.getName(), userDTO.getEmail());
    }
}
