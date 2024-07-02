package ru.practicum.shareit.user.dto;

import lombok.Data;

@Data
public class PatchUserRequestDto {
    private String name;
    private String email;
}
