package ru.practicum.shareit.user.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
class UserResponseTest {
    @Autowired
    private JacksonTester<UserResponse> json;

    @Test
    void testSerialize() throws Exception {
        UserResponse user = new UserResponse(1, "name", "email@rr");

        JsonContent<UserResponse> result = json.write(user);
        assertThat(result).doesNotHaveEmptyJsonPathValue("@.id");
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
    }

}