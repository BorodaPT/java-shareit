package ru.practicum.shareit.user;

import ru.practicum.shareit.user.UserDto.UserDTO;
import ru.practicum.shareit.user.model.User;


public class UserMapper {

    public static UserDTO toDTO(User user) {
        Long id = user.getId();
        String name = user.getName();
        String email = user.getEmail();
        return new UserDTO(user.getId(), user.getName(), user.getEmail());
    }

    public static User toUser(UserDTO userDTO) {
        return new User(userDTO.getId(), userDTO.getName(), userDTO.getEmail());
    }
}
