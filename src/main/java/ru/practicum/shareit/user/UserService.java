package ru.practicum.shareit.user;


import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.UserDto.UserDTO;


import java.util.List;

@Transactional(readOnly = true)
public interface UserService {

    @Transactional(readOnly = true)
    List<UserDTO> getAllUsers();

    @Transactional(readOnly = true)
    UserDTO getUser(long id);

    @Transactional
    UserDTO saveUser(UserDTO userDto);

    @Transactional
    UserDTO saveUser(UserDTO userDto, long id);

    @Transactional
    void delete(long id);

}
