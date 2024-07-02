package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.commentDto.CommentResponse;

import java.util.List;

@Builder
@Data
public class ItemWithDateBookingResponse {
    private Integer id;
    private String name;
    private String description;
    private Boolean available;
    private InfoBookingByItemDto nextBooking;
    private InfoBookingByItemDto lastBooking;
    private List<CommentResponse> comments;
}
