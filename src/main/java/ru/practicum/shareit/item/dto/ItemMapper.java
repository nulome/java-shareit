package ru.practicum.shareit.item.dto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.commentDto.CommentMapper;
import ru.practicum.shareit.item.commentDto.CommentResponse;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemMapper {

    public static ItemWithDateBookingResponse toItemDto(Item item) {
        ItemWithDateBookingResponse itemResponse = ItemWithDateBookingResponse.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
        List<CommentResponse> listComment = item.getComments().stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
        itemResponse.setComments(listComment);
        return itemResponse;
    }
}
