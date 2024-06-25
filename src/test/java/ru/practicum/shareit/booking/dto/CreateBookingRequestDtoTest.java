package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@JsonTest
class CreateBookingRequestDtoTest {

    @Autowired
    private JacksonTester<CreateBookingRequestDto> json;

    @Test
    public void testDeserialize() throws Exception {
        LocalDateTime localDateTime = LocalDateTime.of(2000, 10, 1, 10, 10);
        String jsonContent =
                "{\"start\":\"2000-10-01T10:10:00\",\"end\":\"2000-10-01T10:10:00\",\"itemId\":22}";

        CreateBookingRequestDto result = json.parse(jsonContent).getObject();

        assertThat(result.getStart()).isEqualTo(localDateTime);
        assertThat(result.getItemId()).isEqualTo(22);
    }

}