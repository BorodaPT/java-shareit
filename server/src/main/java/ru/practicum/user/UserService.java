package ru.practicum.user;


import org.springframework.transaction.annotation.Transactional;
import ru.practicum.user.UserDto.UserDTO;


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
    UserDTO editUser(UserDTO userDto, long id);

    @Transactional
    void delete(Long id);

}
