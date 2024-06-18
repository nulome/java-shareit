package ru.practicum.shareit.user.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
class CreateUserRequestDtoTest {
    @Autowired
    private JacksonTester<CreateUserRequestDto> json;

    @Test
    public void testDeserialize() throws Exception {
        String jsonContent =
                "{\"name\":\"name\",\"email\":\"email@rr\"}";

        CreateUserRequestDto result = json.parse(jsonContent).getObject();

        assertThat(result.getName()).isEqualTo("name");
        assertThat(result.getEmail()).isEqualTo("email@rr");
    }

}