package ru.practicum.shareit.item.commentDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class CommentShortDto {
    private Integer id;
    private String text;
    private String authorName;
    private Integer itemId;
    private ZonedDateTime created;
}
