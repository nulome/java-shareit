package ru.practicum.shareit.item.commentDto;

import lombok.Data;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.ZonedDateTime;

@Data
public class CommentDto {
    private Integer id;
    private String text;
    private Item item;
    private User author;
    private ZonedDateTime created;
}
