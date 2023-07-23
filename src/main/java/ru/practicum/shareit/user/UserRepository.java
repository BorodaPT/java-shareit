package ru.practicum.shareit.user;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserRepository {

    User getUser(long id);

    List<User> getUsers();

    User create(User user);

    User edit(User user);

    void delete(long id);
}
