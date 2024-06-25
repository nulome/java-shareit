package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.StatusBooking;
import ru.practicum.shareit.item.dto.ItemResponse;
import ru.practicum.shareit.user.dto.UserResponse;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
class BookingResponseTest {

    @Autowired
    private JacksonTester<BookingResponse> json;


    @Test
    void testSerialize() throws Exception {
        ItemResponse itemResponse = new ItemResponse(9, "nameItemResponse", "desc", true, null);
        LocalDateTime localDateTime = LocalDateTime.of(2000, 10, 1, 10, 10);
        UserResponse user = new UserResponse(5, "name", "email@rr");
        BookingResponse bookingResponse = new BookingResponse(1, localDateTime, localDateTime, itemResponse,
                user, StatusBooking.APPROVED);

        JsonContent<BookingResponse> result = json.write(bookingResponse);
        assertThat(result).doesNotHaveEmptyJsonPathValue("@.id");
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo("APPROVED");
        assertThat(result).extractingJsonPathStringValue("$.booker.email").isEqualTo("email@rr");
        assertThat(result).extractingJsonPathNumberValue("$.item.id").isEqualTo(9);
        assertThat(result).extractingJsonPathStringValue("$.item.name").isEqualTo("nameItemResponse");
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2000-10-01T10:10:00");

    }
}