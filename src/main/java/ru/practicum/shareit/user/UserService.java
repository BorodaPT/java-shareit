package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ExceptionBadRequest;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Service
public class UserService {

    @Autowired
    @Qualifier("userRepositoryInMemory")
    private UserRepository userRepository;

    public User getUser(long id) {
        return userRepository.getUser(id);
    }

    public List<User> getUsers() {
        return userRepository.getUsers();
    }

    public User create(User user) {
        if (user.getEmail() == null) {
            throw new ExceptionBadRequest("createUser","В запросе Email отсутстует");
        }
        return userRepository.create(user);
    }

    public User edit(User user) {
        return userRepository.edit(user);
    }

    public void delete(long id) {
        userRepository.delete(id);
    }
}
