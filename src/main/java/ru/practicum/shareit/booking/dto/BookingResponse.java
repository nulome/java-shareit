package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.StatusBooking;
import ru.practicum.shareit.item.dto.ItemResponse;
import ru.practicum.shareit.user.dto.UserResponse;

import java.time.LocalDateTime;

@Data
@Builder
public class BookingResponse {
    private Integer id;
    private LocalDateTime start;
    private LocalDateTime end;
    private ItemResponse item;
    private UserResponse booker;
    private StatusBooking status;
}
