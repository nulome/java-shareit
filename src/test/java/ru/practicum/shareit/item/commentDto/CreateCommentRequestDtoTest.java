package ru.practicum.shareit.item.commentDto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@JsonTest
class CreateCommentRequestDtoTest {

    @Autowired
    private JacksonTester<CreateCommentRequestDto> json;

    @Test
    public void testDeserialize() throws Exception {
        String jsonContent =
                "{\"text\":\"text\"}";

        CreateCommentRequestDto result = json.parse(jsonContent).getObject();
        assertThat(result.getText()).isEqualTo("text");
    }

}