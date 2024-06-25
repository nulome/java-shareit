package ru.practicum.shareit.request.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class CreateItemRequestReqDto {
    @Size(max = 255)
    @NotBlank
    private String description;
}
