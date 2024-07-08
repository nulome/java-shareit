package ru.practicum.shareit.related;

import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;

@Setter
@Getter
public class ErrorResponse {
    String error;
    String description;
    ZonedDateTime dateTime;

    public ErrorResponse(String error, String description) {
        this.error = error;
        this.description = description;
        dateTime = ZonedDateTime.now();
    }
}
