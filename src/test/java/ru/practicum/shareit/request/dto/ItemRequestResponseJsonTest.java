package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.user.dto.UserResponse;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
class ItemRequestResponseJsonTest {

    @Autowired
    private JacksonTester<ItemRequestResponse> json;

    @Test
    void testSerialize() throws Exception {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("uuuu-MM-dd'T'HH:mm:ss");
        String strTime = LocalDateTime.now().format(formatter);
        LocalDateTime time = LocalDateTime.parse(strTime, formatter);

        UserResponse user = new UserResponse(1, "name", "email@rr");
        ItemRequestResponse itemRequestResponse = new ItemRequestResponse(
                1,
                "john.doe",
                user,
                time,
                Collections.emptyList());

        JsonContent<ItemRequestResponse> result = json.write(itemRequestResponse);
        assertThat(result).doesNotHaveEmptyJsonPathValue("@.id");
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("john.doe");
        assertThat(result).extractingJsonPathNumberValue("$.requestor.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo(time.format(formatter));
    }

    @Test
    public void testDeserialize() throws Exception {

        String jsonContent =
                "{\"id\":1,\"description\":\"john.doe\",\"requestor\":{\"id\":1,\"name\":\"name\",\"email\":\"email@rr\"}" +
                        ",\"created\":\"2024-06-19T23:11\",\"items\":[]}";

        ItemRequestResponse result = json.parse(jsonContent).getObject();

        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getCreated()).isEqualTo(LocalDateTime.of(2024, 6, 19, 23, 11));
        assertThat(result.getRequestor().getId()).isEqualTo(1);
    }

}