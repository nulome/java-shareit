package ru.practicum.shareit.item.commentDto;

import lombok.Data;

import javax.validation.constraints.Size;

@Data
public class PatchCommentRequestDto {
    @Size(max = 255)
    private String text;
}
