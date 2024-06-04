package ru.practicum.shareit.booking.dto;


import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.StatusBooking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.ZonedDateTime;

@Data
@Builder
public class BookingDto {
    private Integer id;
    private ZonedDateTime start;
    private ZonedDateTime end;
    private Item item;
    private User booker;
    private StatusBooking status;
}
