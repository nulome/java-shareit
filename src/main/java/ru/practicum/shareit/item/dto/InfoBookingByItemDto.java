package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InfoBookingByItemDto {
    private Integer id;
    private Integer bookerId;
    private LocalDateTime start;
    private LocalDateTime end;
}
