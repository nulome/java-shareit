package ru.practicum.shareit.item.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class InfoBookingByItemDto {
    private Integer id;
    private Integer bookerId;
    private LocalDateTime start;
    private LocalDateTime end;
}
