package ru.practicum.user;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exception.ExceptionBadRequest;
import ru.practicum.exception.ExceptionNotFound;
import ru.practicum.user.UserDto.UserDTO;
import ru.practicum.user.model.User;

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
        User user = repository.save(UserMapper.toUser(userDto));
        return UserMapper.toDTO(user);
    }

    @Transactional
    @Override
    public UserDTO editUser(UserDTO userDto, long id) {
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

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }
}
