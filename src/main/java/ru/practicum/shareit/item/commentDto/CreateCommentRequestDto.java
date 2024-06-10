package ru.practicum.shareit.item.commentDto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class CreateCommentRequestDto {
    @NotBlank
    @Size(max = 255)
    private String text;
}
