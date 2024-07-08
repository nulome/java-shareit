package ru.practicum.shareit.request.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.Data;


@Data
public class CreateItemRequestReqDto {
    @Size(max = 255)
    @NotBlank
    private String description;
}
