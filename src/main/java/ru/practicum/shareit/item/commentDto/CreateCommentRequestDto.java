package ru.practicum.shareit.item.commentDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateCommentRequestDto {
    @NotBlank
    @Size(max = 255)
    private String text;
}
