package ru.practicum.shareit.item.commentDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class CommentResponse {
    private Integer id;
    private String text;
    private String authorName;
    private LocalDateTime created;
}
