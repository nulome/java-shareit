package ru.practicum.shareit.user.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;
import lombok.Data;


@Data
public class PatchUserRequestDto {
    @Size(max = 255)
    private String name;
    @Size(max = 256)
    @Email
    private String email;
}
