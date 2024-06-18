package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
class InfoBookingByItemDtoTest {

    @Autowired
    private JacksonTester<InfoBookingByItemDto> json;

    @Test
    void testSerialize() throws Exception {
        LocalDateTime localDateTime = LocalDateTime.of(2000, 10, 1, 10, 10);
        InfoBookingByItemDto infoBookingByItemDto = new InfoBookingByItemDto(1, 1, localDateTime, localDateTime);

        JsonContent<InfoBookingByItemDto> result = json.write(infoBookingByItemDto);
        assertThat(result).doesNotHaveEmptyJsonPathValue("@.id");
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2000-10-01T10:10:00");

    }

}