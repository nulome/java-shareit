package ru.practicum.shareit.user.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

@Data
public class PatchUserRequestDto {
    @Size(max = 255)
    private String name;
    @Size(max = 155)
    @Email
    private String email;
}
