package ru.practicum.shareit.item.dto;

import lombok.Data;

import java.time.ZonedDateTime;

@Data
public class InfoBookingByItemDto {
    private Integer id;
    private Integer bookerId;
    private ZonedDateTime start;
    private ZonedDateTime end;
}
