package ru.practicum.shareit.user;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.ExceptionDataRequest;
import ru.practicum.shareit.exception.ExceptionNotFound;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component("userRepositoryInMemory")
public class UserRepositoryInMemory  {

    private long id;

    public HashMap<Long, User> users;

    public UserRepositoryInMemory() {
        id = 0;
        users = new HashMap<>();
    }


    public User getUser(long id) {
        if (users.containsKey(id)) {
            return users.get(id);
        } else {
            throw new ExceptionNotFound("selectUser","Пользователь не найден");
        }
    }


    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }


    public User create(User user) {
        if (users.containsKey(user.getId())) {
            throw new ExceptionNotFound("createUser","Пользователь уже зарегестрирован");
        } else {
            for (User userF : users.values()) {
                if (userF.getEmail().equals(user.getEmail())) {
                    throw new ExceptionDataRequest("createUser","Email уже зарегестрирован", HttpStatus.CONFLICT);
                }
            }
            id++;
            user.setId(id);
            users.put(user.getId(), user);
            return users.get(user.getId());
        }
    }


    public User edit(User user) {
        if (users.containsKey(user.getId())) {
            User userBase = users.get(user.getId());
            for (User userF : users.values()) {
                if (userF.getEmail().equals(user.getEmail()) && !userF.getId().equals(user.getId())) {
                    throw new ExceptionDataRequest("createUser","Email уже зарегестрирован", HttpStatus.CONFLICT);
                }
            }
            if (user.getName() != null) {
                userBase.setName(user.getName());
            }
            if (user.getEmail() != null) {
                userBase.setEmail(user.getEmail());
            }

            users.put(userBase.getId(), userBase);
            return users.get(userBase.getId());
        } else {
            throw new ExceptionNotFound("updateUser","Пользователь для обновления отсутствует");
        }
    }


    public void delete(long id) {
        if (users.containsKey(id)) {
            users.remove(id);
        } else {
            throw new ExceptionNotFound("deleteUser","Пользователь для удаления отсутствует");
        }
    }

}
