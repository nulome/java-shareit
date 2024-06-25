package ru.practicum.shareit.booking.dto;


import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.StatusBooking;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.ZonedDateTime;

@Data
@Builder
public class BookingDto {
    private Integer id;
    private ZonedDateTime start;
    private ZonedDateTime end;
    private ItemDto item;
    private UserDto booker;
    private StatusBooking status;
}
