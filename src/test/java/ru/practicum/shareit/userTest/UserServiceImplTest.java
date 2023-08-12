package ru.practicum.shareit.userTest;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.practicum.shareit.user.UserDto.UserDTO;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.UserServiceImpl;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;


public class UserServiceImplTest {

    private UserService service;

    private UserRepository repository;

    private static User correctUser;

    private static User correctEditUser;

    private static UserDTO correctUserDto;

    private static UserDTO newUserDto;

    private static UserDTO userEditName;

    private static List<User> userList;

    @BeforeAll
    static void before() {
        correctUserDto = new UserDTO(
                1L,
                "Пушкин",
                "alexander.push@mail.ru");

        correctUser = new User(1L,
                "Пушкин",
                "alexander.push@mail.ru");

        correctEditUser = new User(1L,
                "Лермонтов",
                "alexander.push@mail.ru");

        newUserDto = new UserDTO("Пушкин",
                "alexander.push@mail.ru");

        userEditName = new UserDTO(1L,
                "Лермонтов",
                "alexander.push@mail.ru");

        userList = new ArrayList<>();

        for (int i = 0; i < 9; i++) {
            userList.add(new User((i + 2L), correctUserDto.getName(), correctUserDto.getEmail()));
        }
    }

    @BeforeEach
    void setUp() {
        repository = Mockito.mock(UserRepository.class);
        service = new UserServiceImpl(repository);
    }

    @Test
    void saveUser() {
        UserDTO userDTO = new UserDTO("Пушкин", "alexander.push@mail.ru");
        when(repository.save(any(User.class)))
                .thenReturn(correctUser);
        UserDTO user = service.saveUser(newUserDto);
        assertThat(user.getId(), notNullValue());
        assertThat(user.getName(), equalTo(userDTO.getName()));
        assertThat(user.getEmail(), equalTo(userDTO.getEmail()));
    }

    @Test
    void editUser() {
        when(repository.findById(anyLong()))
                .thenReturn(Optional.of(correctUser));
        when(repository.save(any(User.class)))
                .thenReturn(correctEditUser);

        UserDTO userDtoEdit = service.editUser(userEditName, 1L);

        assertThat(userDtoEdit.getId(), equalTo(userEditName.getId()));
        assertThat(userDtoEdit.getName(), equalTo(userEditName.getName()));
        assertThat(userDtoEdit.getEmail(), equalTo(userEditName.getEmail()));

        verify(repository, times(1)).save(any(User.class));
    }

    @Test
    void delete() {
        when(repository.findById(anyLong()))
                .thenReturn(Optional.of(correctUser));
        doNothing().when(repository).deleteById(anyLong());

        service.delete(1L);

        verify(repository, times(1)).deleteById(anyLong());
    }


    @Test
    void getUser() {
        when(repository.findById(anyLong()))
                .thenReturn(Optional.of(correctUser));

        UserDTO userDto = service.getUser(1L);

        assertThat(userDto.getId(), equalTo(correctUser.getId()));
        assertThat(userDto.getName(), equalTo(correctUser.getName()));
        assertThat(userDto.getEmail(), equalTo(correctUser.getEmail()));
        verify(repository, times(1)).findById(anyLong());
    }

    @Test
    void getAllUsers() {
        when(repository.findAll())
                .thenReturn(userList);
        List<UserDTO> users = service.getAllUsers();
        assertThat(userList.size(), equalTo(users.size()));
        assertThat(userList.get(0).getId(), equalTo(users.get(0).getId()));
        assertThat(userList.get(0).getName(), equalTo(users.get(0).getName()));
        assertThat(userList.get(0).getEmail(), equalTo(users.get(0).getEmail()));

    }


}
