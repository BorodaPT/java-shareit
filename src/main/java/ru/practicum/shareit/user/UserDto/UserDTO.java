package ru.practicum.shareit.user.UserDto;

import lombok.Getter;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

@Getter
public class UserDTO {

    private final Long id;

    private String name;

    @Email(message = "Неверный формат электронной почты")
    @Size(max = 100, message = "Превышена максимальная длина комментария(200)")
    private String email;

    public UserDTO(Long id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }
}
