package ru.practicum.shareit.userTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.UserDto.UserDTO;
import ru.practicum.shareit.user.UserService;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper mapper;

    private UserDTO correctUserDto;

    private UserDTO newUserDto;

    private List<UserDTO> userDTOList;

    @BeforeEach
    void setUp() {
        correctUserDto = new UserDTO(
                1L,
                "Пушкин",
                "alexander.push@mail.ru");
        newUserDto = new UserDTO("Пушкин",
                "alexander.push@mail.ru");

        userDTOList = new ArrayList<>();

        for (int i = 0; i < 9; i++) {
            userDTOList.add(new UserDTO((i + 2L), correctUserDto.getName(), correctUserDto.getEmail()));
        }
    }

    @Test
    void addUser() throws Exception {
        when(userService.saveUser(any(UserDTO.class)))
                .thenReturn(correctUserDto);

        String jsonUser = mapper.writeValueAsString(newUserDto);

        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonUser))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.id").value(correctUserDto.getId()))
                        .andExpect(jsonPath("$.name").value(correctUserDto.getName()))
                        .andExpect(jsonPath("$.email").value(correctUserDto.getEmail()));

        verify(userService, times(1)).saveUser(any(UserDTO.class));
    }

    @Test
    void editUser() throws Exception {
        when(userService.editUser(any(UserDTO.class), anyLong()))
                .thenReturn(correctUserDto);

        mvc.perform(patch("/users/1")
                        .content(mapper.writeValueAsString(correctUserDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.id", is(correctUserDto.getId()), Long.class))
                        .andExpect(jsonPath("$.name", is(correctUserDto.getName())))
                        .andExpect(jsonPath("$.email", is(correctUserDto.getEmail())));
        verify(userService, times(1)).editUser(any(UserDTO.class), anyLong());

    }

    @Test
    void deleteUserById() throws Exception {
        doNothing().when(userService).delete(anyLong());
        mvc.perform(delete("/users/1")
                    .contentType(MediaType.APPLICATION_JSON));
        verify(userService, times(1)).delete(anyLong());
    }

    @Test
    void getUser() throws Exception {
        when(userService.getUser(anyLong()))
                .thenReturn(correctUserDto);

        mvc.perform(get("/users/1")
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.id").value(correctUserDto.getId()))
                        .andExpect(jsonPath("$.name").value(correctUserDto.getName()))
                        .andExpect(jsonPath("$.email").value(correctUserDto.getEmail()));
        verify(userService, times(1)).getUser(anyLong());
    }

    @Test
    void getUsers() throws Exception {
        when(userService.getAllUsers())
                .thenReturn(userDTOList);
        mvc.perform(get("/users")
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.[*]").exists())
                        .andExpect(jsonPath("$.[*]").isNotEmpty())
                        .andExpect(jsonPath("$.[*]").isArray())
                        .andExpect(jsonPath("$.size()").value(9))
                        .andExpect(jsonPath("$.[0].id").value(2L))
                        .andExpect(jsonPath("$.[8].id").value(10L));
        verify(userService, times(1)).getAllUsers();
    }

    //wrongRequest
    @Test
    void getExceptionCreateUserWithoutName() throws Exception {
        String jsonUser = "{'email':'123df@mail.ru'}";

        mvc.perform(post("/users")
                        .content(jsonUser)
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest());
        verify(userService, never()).saveUser(any(UserDTO.class));
    }

    @Test
    void getExceptionCreateUserWithBadEmail() throws Exception {
        String jsonUser = "{'name':'tewst','email':'123dfmail.ru'}";

        mvc.perform(post("/users")
                        .content(jsonUser)
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest());
        verify(userService, never()).saveUser(any(UserDTO.class));
    }

    @Test
    void getExceptionEditUserWithBadEmail() throws Exception {
        String jsonUser = "{'name':'tewst','email':'123dfmail.ru'}";

        mvc.perform(patch("/users/1")
                        .content(jsonUser)
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest());
        verify(userService, never()).editUser(any(UserDTO.class), anyLong());
    }

    @Test
    void exceptionGetIdLess0() throws Exception {
        mvc.perform(get("/users/-1")
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest());
        verify(userService, never()).getUser(anyLong());
    }

}
