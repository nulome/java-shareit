package ru.practicum.shareit.item.dto;

import javax.validation.constraints.Size;
import lombok.Data;

@Data
public class PatchItemRequestDto {
    @Size(max = 255)
    private String name;
    @Size(max = 255)
    private String description;
    private Boolean available;
}
