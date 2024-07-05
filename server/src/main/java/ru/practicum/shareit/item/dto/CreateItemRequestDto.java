package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateItemRequestDto {

    private String name;
    private String description;
    private Boolean available;
    private Integer requestId;
}
