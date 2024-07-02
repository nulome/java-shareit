package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@JsonTest
class CreateItemRequestDtoTest {

    @Autowired
    private JacksonTester<CreateItemRequestDto> json;

    @Test
    public void testDeserialize() throws Exception {
        String jsonContent =
                "{\"name\":\"Name\",\"description\":\"Desc\",\"available\":true,\"requestId\":1}";

        CreateItemRequestDto result = json.parse(jsonContent).getObject();

        assertThat(result.getName()).isEqualTo("Name");
        assertThat(result.getAvailable()).isEqualTo(true);
    }

}