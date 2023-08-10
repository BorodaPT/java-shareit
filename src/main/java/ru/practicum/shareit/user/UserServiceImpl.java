package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ExceptionBadRequest;
import ru.practicum.shareit.exception.ExceptionNotFound;
import ru.practicum.shareit.user.UserDto.UserDTO;
import ru.practicum.shareit.user.model.User;

import java.util.List;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository repository;

    @Override
    public List<UserDTO> getAllUsers() {
        List<User> users = repository.findAll();
        return UserMapper.toDTO(users);
    }


    @Override
    public UserDTO getUser(long id) {
        return UserMapper.toDTO(repository.findById(id).orElseThrow(() -> new ExceptionNotFound("selectUser","Пользователь не найден")));
    }

    @Transactional
    @Override
    public UserDTO saveUser(UserDTO userDto) {
        if (userDto.getEmail() == null) {
            throw new ExceptionBadRequest("createUser","В запросе Email отсутстует");
        }
        User user = repository.save(UserMapper.toUser(userDto));
        return UserMapper.toDTO(user);
    }

    @Transactional
    @Override
    public UserDTO saveUser(UserDTO userDto, long id) {
        User user = repository.findById(id).orElseThrow(() -> new ExceptionNotFound("selectUser","Пользователь не найден"));
        if (userDto.getName() != null && !userDto.getName().isBlank()) {
            user.setName(userDto.getName());
        }

        if (userDto.getEmail() != null && !userDto.getEmail().isBlank()) {
            user.setEmail(userDto.getEmail());
        }
        try {
            return UserMapper.toDTO(repository.save(user));
        } catch (DataIntegrityViolationException e) {
            throw new ExceptionBadRequest("Изменение пользователя","Не удалось сохранить изменения");
        }
    }

    public void delete(long id) {
        repository.deleteById(id);
    }
}
