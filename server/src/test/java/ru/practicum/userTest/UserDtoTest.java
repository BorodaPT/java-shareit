package ru.practicum.userTest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.user.UserDto.UserDTO;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class UserDtoTest {

    @Autowired
    private JacksonTester<UserDTO> json;

    @Test
    void testUserDto() throws Exception {
        UserDTO userDto = new UserDTO(
                1L,
                "Пушкин",
                "alexander.push@mail.ru");

        JsonContent<UserDTO> result = json.write(userDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Пушкин");
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("alexander.push@mail.ru");
    }

}
