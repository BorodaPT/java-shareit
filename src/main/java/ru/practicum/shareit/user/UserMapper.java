package ru.practicum.shareit.user;

import ru.practicum.shareit.user.UserDto.UserDTO;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;


public class UserMapper {

    public static UserDTO toDTO(User user) {
        return new UserDTO(user.getId(), user.getName(), user.getEmail());
    }

    public static User toUser(UserDTO userDTO) {
        return new User(userDTO.getId(), userDTO.getName(), userDTO.getEmail());
    }

    public static List<UserDTO> toDTO(Iterable<User> users) {
        List<UserDTO> result = new ArrayList<>();
        for (User user : users) {
            result.add(toDTO(user));
        }
        return result;
    }

}
