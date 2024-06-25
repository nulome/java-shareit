package ru.practicum.shareit.item.commentDto;

import lombok.Data;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.ZonedDateTime;

@Data
public class CommentDto {
    private Integer id;
    private String text;
    private ItemDto item;
    private UserDto author;
    private ZonedDateTime created;
}
