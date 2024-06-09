package ru.practicum.shareit.user.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class CreateUserRequestDto {

    @Size(max = 255)
    @NotBlank
    private String name;
    @NotNull
    @Size(max = 256)
    @Email
    private String email;
}
