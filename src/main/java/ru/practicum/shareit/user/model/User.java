package ru.practicum.shareit.user.model;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Data
public class User {

    private Long id;

    @NotEmpty(message = "Наименование не может быть пустым или состоять только из пробелов")
    private String name;

    @Email(message = "Неверный формат электронной почты")
    private String email;

    public User(Long id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }
}
