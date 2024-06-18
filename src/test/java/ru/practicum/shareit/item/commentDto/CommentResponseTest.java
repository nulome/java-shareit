package ru.practicum.shareit.item.commentDto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
class CommentResponseTest {

    @Autowired
    private JacksonTester<CommentResponse> json;

    @Test
    void testSerialize() throws Exception {
        LocalDateTime localDateTime = LocalDateTime.of(2000, 10, 1, 10, 10);
        CommentResponse commentResponse = new CommentResponse(1, "text", "author", localDateTime);

        JsonContent<CommentResponse> result = json.write(commentResponse);
        assertThat(result).doesNotHaveEmptyJsonPathValue("@.id");
        assertThat(result).extractingJsonPathStringValue("$.authorName").isEqualTo("author");
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo("2000-10-01T10:10:00");

    }

}