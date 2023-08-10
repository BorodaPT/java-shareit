package ru.practicum.shareit.userTest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.UserDto.UserDTO;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserIntegrationTest {

    private final UserService service;

    private final EntityManager entityManager;

    private static UserDTO userDTO;

    @BeforeAll
    static void beforeAll() {
        userDTO  = new UserDTO(
                "Пушкин",
                "alexander.push@mail.ru");
    }

    @Test
    void saveUser() {
        UserDTO user = service.saveUser(userDTO);

        TypedQuery<User> query = entityManager.createQuery("Select u from User u where u.id = :id", User.class);
        User userRes = query.setParameter("id", user.getId()).getSingleResult();

        assertThat(user.getId(), notNullValue());
        assertThat(user.getName(), equalTo(userRes.getName()));
        assertThat(user.getEmail(), equalTo(userRes.getEmail()));
    }

    @Test
    void editUser() {
        UserDTO user = service.saveUser(userDTO);

        user = service.editUser(new UserDTO("лермонтов", "alexander.push@mail.ru"), user.getId());

        TypedQuery<User> query = entityManager.createQuery("Select u from User u where u.id = :id", User.class);
        User res = query.setParameter("id", user.getId())
                .getSingleResult();

        assertThat(user.getName(), equalTo(res.getName()));
    }

    @Test
    void delete() {
        int cntBefore = service.getAllUsers().size();
        UserDTO user = service.saveUser(userDTO);
        assertThat(service.getAllUsers().size(), equalTo(cntBefore + 1));
        service.delete(user.getId());
        assertThat(service.getAllUsers().size(), equalTo(cntBefore));
    }

    @Test
    void getUser() {
        UserDTO user =  service.saveUser(userDTO);

        UserDTO userGet = service.getUser(user.getId());

        assertThat(userGet.getId(), equalTo(user.getId()));
        assertThat(userGet.getName(), equalTo(user.getName()));
        assertThat(userGet.getEmail(), equalTo(user.getEmail()));
    }

    @Test
    void getAllUsers() {
        List<UserDTO> mainUsers = new ArrayList<>();
        mainUsers.add(service.saveUser(new UserDTO("ПУШКИН", "alexander.push1@mail.ru")));
        mainUsers.add(service.saveUser(new UserDTO("ПУЩКИН", "alexander.push2@mail.ru")));
        mainUsers.add(service.saveUser(new UserDTO("ПУШКИН", "alexander.push3@mail.ru")));
        mainUsers.add(service.saveUser(new UserDTO("ПУШКИН", "alexander.push4@mail.ru")));
        mainUsers.add(service.saveUser(new UserDTO("ПУШКИН", "alexander.push5@mail.ru")));

        List<UserDTO> baseUsers = service.getAllUsers();
        assertThat(mainUsers.size(), equalTo(baseUsers.size()));
    }
}
