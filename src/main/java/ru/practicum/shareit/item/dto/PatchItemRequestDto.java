package ru.practicum.shareit.item.dto;

import lombok.Data;

import javax.validation.constraints.Size;

@Data
public class PatchItemRequestDto {
    @Size(max = 255)
    private String name;
    @Size(max = 255)
    private String description;
    private Boolean available;
}
