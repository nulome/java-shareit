package ru.practicum.shareit.item.commentDto;

import ru.practicum.shareit.item.model.Comment;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

public class CommentMapper {

    public static CommentResponse toCommentDto(Comment comment) {
        return CommentResponse.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorName(comment.getAuthor().getName())
                .created(convertToLocal(comment.getCreated()))
                .build();
    }

    private static LocalDateTime convertToLocal(ZonedDateTime zdt) {
        return zdt.toLocalDateTime();
    }
}
